package com.meow.footprint.domain.member.service;

import com.meow.footprint.domain.member.dto.*;
import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.domain.member.repository.MemberRepository;
import com.meow.footprint.global.result.error.exception.BusinessException;
import com.meow.footprint.global.result.error.exception.EntityAlreadyExistException;
import com.meow.footprint.global.result.error.exception.EntityNotFoundException;
import com.meow.footprint.global.util.AccountUtil;
import com.meow.footprint.global.util.JWTTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZonedDateTime;
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

    @Transactional
    @Override
    public void register(MemberJoinRequest joinRequest) {
        Member member = modelMapper.map(joinRequest,Member.class);
        if(memberRepository.existsById(member.getId())) throw new EntityAlreadyExistException(MEMBER_ID_ALREADY_EXIST);

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
    public LoginResponse login(LoginRequest loginRequest) {
        Member member = memberRepository.findById(loginRequest.getId())
                .filter(m -> passwordEncoder.matches(loginRequest.getPassword(), m.getPassword()))
                .orElseThrow(() -> new BusinessException(LOGIN_FAIL));
        LoginResponse loginResponse = jwtTokenProvider.getLoginResponse(member);
        redisTemplate.opsForValue().set("RTK:"+ loginResponse.getUserId(), loginResponse.getRefreshToken(), Duration.ofDays(15));
        return loginResponse;
    }

    @Transactional
    @Override
    public void logout(String accessToken) {
        accessToken = accessToken.substring(7);
        try {
            jwtTokenProvider.validateToken(accessToken);
        }catch (RuntimeException e){
            throw new BusinessException(JWT_INVALID);
        }

        // AccessToken에서 정보 가져옴
        Claims claims = jwtTokenProvider.getClaims(accessToken);

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
}
