package com.meow.footprint.domain.member.service;

import com.meow.footprint.domain.member.dto.*;
import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.domain.member.entity.Role;
import com.meow.footprint.domain.member.repository.MemberRepository;
import com.meow.footprint.global.result.error.exception.BusinessException;
import com.meow.footprint.global.result.error.exception.EntityAlreadyExistException;
import com.meow.footprint.global.result.error.exception.EntityNotFoundException;
import com.meow.footprint.global.util.AccountUtil;
import com.meow.footprint.global.util.JWTTokenProvider;
import com.meow.footprint.global.util.MailUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.meow.footprint.global.result.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JWTTokenProvider jwtTokenProvider;
    private final AccountUtil accountUtil;
    private final RedisTemplate redisTemplate;
    private final MailUtil mailUtil;
    private static final String AUTH_CODE_PREFIX = "AuthCode ";
    private static final int AUTH_CODE_LENGTH = 6;
    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    @Transactional
    @Override
    public void register(MemberJoinRequest joinRequest) {
        Member member = modelMapper.map(joinRequest,Member.class);
        if(memberRepository.existsById(member.getId())) throw new EntityAlreadyExistException(MEMBER_ID_ALREADY_EXIST);

        member.addRole(Role.ROLE_USER);
        member.encodingPassword(passwordEncoder);
        memberRepository.save(member);
    }

    @Override
    public boolean idCheck(String memberId) {
        return memberRepository.existsById(memberId);
    }

    @Override
    public MemberResponse findMemberById(String memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_ID_NOT_EXIST));
        return MemberResponse.from(member);
    }

    @Transactional
    @Override
    public void updateMember(MemberUpdateRequest memberUpdateRequest, String memberId) {
        if(!accountUtil.checkLoginMember(memberId)){
            throw new BusinessException(FORBIDDEN_ERROR);
        }
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_ID_NOT_EXIST));
        member.setName(memberUpdateRequest.name());
    }

    @Transactional
    @Override
    public void deleteMember(String memberId) {
        if(!accountUtil.checkLoginMember(memberId)){
            throw new BusinessException(FORBIDDEN_ERROR);
        }
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_ID_NOT_EXIST));
        memberRepository.delete(member);
    }

    @Transactional
    @Override
    public LoginTokenDTO login(LoginRequest loginRequest) {
        Member member = memberRepository.findById(loginRequest.getId())
                .filter(m -> passwordEncoder.matches(loginRequest.getPassword(), m.getPassword()))
                .orElseThrow(() -> new BusinessException(LOGIN_FAIL));
        LoginTokenDTO loginTokenDTO = jwtTokenProvider.getLoginResponse(member);
        redisTemplate.opsForValue().set("RTK:"+ loginTokenDTO.getUserId(), loginTokenDTO.getRefreshToken(), Duration.ofDays(15));
        return loginTokenDTO;
    }

    @Transactional
    @Override
    public void logout(String accessToken) {
        accessToken = accessToken.substring(7);
        try {
            jwtTokenProvider.validateAndGetClaims(accessToken);
        }catch (RuntimeException e){
            throw new BusinessException(JWT_INVALID);
        }

        // AccessToken에서 정보 가져옴
        Claims claims = jwtTokenProvider.validateAndGetClaims(accessToken);

        // 해당 user의 RefreshToken redis에 있다면 삭제
        if (redisTemplate.opsForValue().get("RTK:"+claims.getSubject())!=null){
            redisTemplate.delete("RTK:"+claims.getSubject());
        }

        //만료시간 가져옴
        long expiration = claims.getExpiration().toInstant().getEpochSecond() - ZonedDateTime.now().toEpochSecond();
        // 해당 AccessToken logout으로 저장
        redisTemplate.opsForValue().set(accessToken,"logout",expiration, TimeUnit.SECONDS);
    }

    @Transactional
    @Override
    public LoginTokenDTO reissue(LoginTokenDTO loginTokenDTO) {
        String accessToken = loginTokenDTO.getAccessToken();
        String refreshToken = loginTokenDTO.getRefreshToken();
        HashMap<Object, String> claims = jwtTokenProvider.parseClaimsByExpiredToken(accessToken); //만료된 atk를 검증하고 claim정보를 가져옴
        // atk가 만료되지 않은 상황은 재발급하지 않음
        if(claims == null){
            throw new BusinessException(TOKEN_ALIVE);
        }

        Claims refreshClaims = jwtTokenProvider.validateAndGetClaims(refreshToken);
        String userName = claims.get("sub");

        String redisToken = (String) redisTemplate.opsForValue().get("RTK:"+userName);
        if (!Objects.equals(redisToken, refreshToken)){  //atk의 userName으로 db에 저장된 rtk와 전달받은 rtk를 비교
            throw new BusinessException(REFRESH_INVALID);
        }

        Date exp = refreshClaims.getExpiration();
        Date current = Date.from(OffsetDateTime.now().toInstant());

        Member member = memberRepository.findById(userName).orElseThrow(()-> new BusinessException(MEMBER_ID_NOT_EXIST));

        //만료 시간과 현재 시간의 간격 계산
        //만일 3일 미만인 경우에는 Refresh Token도 다시 생성
        long gapTime = (exp.getTime() - current.getTime());
        if(gapTime < (1000 * 60 * 60 * 24 * 3  ) ){
            log.info("new Refresh Token required...  ");
            refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());
            redisTemplate.opsForValue().set("RTK:"+userName,refreshToken, Duration.ofDays(jwtTokenProvider.getRefreshTokenValidityTime()));
        }
        String auth = jwtTokenProvider.getAuthorities(member);
        accessToken = jwtTokenProvider.generateAccessToken(member.getId(),auth);
        return new LoginTokenDTO(member.getId(),accessToken,refreshToken);
    }

    @Transactional
    @Override
    public void updatePassword(PasswordUpdateRequest passwordUpdateRequest, String memberId) {
        if(!accountUtil.checkLoginMember(memberId)){
            throw new BusinessException(FORBIDDEN_ERROR);
        }
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_ID_NOT_EXIST));
        if(!passwordEncoder.matches(passwordUpdateRequest.oldPassword(), member.getPassword())){
            throw new BusinessException(WRONG_PASSWORD);
        }
        member.setPassword(passwordUpdateRequest.newPassword());
        member.encodingPassword(passwordEncoder);
    }

    @Override
    public void sendCodeToEmail(String email) {
        if(memberRepository.existsById(email))
            throw new EntityAlreadyExistException(MEMBER_ID_ALREADY_EXIST);
        String title = "Footprint - 이메일 인증 번호";
        String authCode = createCode();
        mailUtil.createMimeEmailForm(email, title, authCode);
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisTemplate.opsForValue().set(AUTH_CODE_PREFIX+email,authCode,Duration.ofMillis(this.authCodeExpirationMillis));
    }

    @Override
    public void verifiedCode(EmailVerificationRequest emailVerificationRequest) {
        if(memberRepository.existsById(emailVerificationRequest.email()))
            throw new EntityAlreadyExistException(MEMBER_ID_ALREADY_EXIST);
        String redisAuthCode = (String) redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + emailVerificationRequest.email());
        if(redisAuthCode==null || !redisAuthCode.equals(emailVerificationRequest.authCode()))
            throw new BusinessException(FAIL_TO_VERIFICATION_EMAIL);
    }

    private String createCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < AUTH_CODE_LENGTH; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(INTERNAL_SERVER_ERROR);
        }
    }
}
