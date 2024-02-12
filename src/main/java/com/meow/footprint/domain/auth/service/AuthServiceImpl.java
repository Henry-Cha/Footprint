package com.meow.footprint.domain.auth.service;

import com.meow.footprint.domain.auth.api.KakaoOAuthApi;
import com.meow.footprint.domain.auth.api.NaverOAuthApi;
import com.meow.footprint.domain.auth.api.OAuthApi;
import com.meow.footprint.domain.auth.dto.OAuth2UserInfo;
import com.meow.footprint.domain.auth.dto.OAuthLoginReq;
import com.meow.footprint.domain.member.dto.LoginTokenDTO;
import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.domain.member.entity.Role;
import com.meow.footprint.domain.member.repository.MemberRepository;
import com.meow.footprint.global.result.error.ErrorCode;
import com.meow.footprint.global.result.error.exception.BusinessException;
import com.meow.footprint.global.util.JWTTokenProvider;
import java.time.Duration;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final InMemoryClientRegistrationRepository inMemoryClient;
    private final MemberRepository memberRepository;
    private final JWTTokenProvider jwtProvider;
    private final RedisTemplate redisTemplate;

    @Override
    public LoginTokenDTO loginOauth(OAuthLoginReq loginReq) {
        try {
            ClientRegistration provider = inMemoryClient.findByRegistrationId(
                loginReq.getProviderName().getSocialName()); //provider 찾음
            OAuthApi oAuthApi = null;
            switch (loginReq.getProviderName()) {
                case KAKAO -> oAuthApi = new KakaoOAuthApi(provider, loginReq);
                case NAVER -> oAuthApi = new NaverOAuthApi(provider, loginReq);
            }
            OAuth2UserInfo oAuth2UserInfo = oAuthApi.loginProcess();
            Member member = memberRepository.findById(oAuth2UserInfo.getEmail())
                .orElseGet(() -> createNewMember(oAuth2UserInfo));

            LoginTokenDTO loginTokenDTO = jwtProvider.getLoginResponse(member);
            redisTemplate.opsForValue()
                .set("RTK:"+loginTokenDTO.getUserId(),loginTokenDTO.getRefreshToken(),
                    Duration.ofDays(jwtProvider.getRefreshTokenValidityTime()));
            return loginTokenDTO;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FAIL_TO_OAUTH_LOGIN);
        }
    }

    private Member createNewMember(OAuth2UserInfo oAuth2UserInfo) {
        Member m = Member.builder()
            .id(oAuth2UserInfo.getEmail())
            .name(oAuth2UserInfo.getNickname())
            .role(Collections.singleton(Role.ROLE_USER))
            .oauthId(oAuth2UserInfo.getId())
            .socialType(oAuth2UserInfo.getSocialType())
            .build();
        return memberRepository.save(m);
    }
}