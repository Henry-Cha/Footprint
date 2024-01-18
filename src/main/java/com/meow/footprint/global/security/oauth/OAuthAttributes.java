package com.meow.footprint.global.security.oauth;

import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.domain.member.entity.Role;
import com.meow.footprint.domain.member.entity.SocialType;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class OAuthAttributes {
    private SocialType socialType;
    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)

    @Builder
    private OAuthAttributes(SocialType socialType,String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.socialType = socialType;
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OAuthAttributes of(SocialType socialType,
                                     String userNameAttributeName, Map<String, Object> attributes) {
        if (socialType == SocialType.NAVER) {
            return ofNaver(socialType,"email", attributes);
        }
        if (socialType == SocialType.KAKAO) {
            return ofKakao(socialType,userNameAttributeName, attributes);
        }
        return ofGoogle(socialType,userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofKakao(SocialType socialType,String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .socialType(socialType)
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new OAuth2Info.KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofGoogle(SocialType socialType,String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .socialType(socialType)
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new OAuth2Info.GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofNaver(SocialType socialType,String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .socialType(socialType)
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new OAuth2Info.NaverOAuth2UserInfo(attributes))
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .id(oauth2UserInfo.getEmail())
                .name(oauth2UserInfo.getNickname())
                .role(Collections.singleton(Role.ROLE_USER))
                .oauthId(oauth2UserInfo.getId())
                .socialType(socialType)
                .build();
    }
}