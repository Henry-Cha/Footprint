package com.meow.footprint.domain.auth.dto;

import com.meow.footprint.domain.member.entity.SocialType;
import java.util.Map;

public class OAuth2Info {
    public static class GoogleOAuth2UserInfo extends OAuth2UserInfo {

        public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
            super(attributes, SocialType.GOOGLE);
        }

        @Override
        public String getEmail() {
            return (String) attributes.get("email");
        }

        @Override
        public String getId() {
            return (String) attributes.get("sub");
        }

        @Override
        public String getNickname() {
            return (String) attributes.get("name");
        }

        @Override
        public String getImageUrl() {
            return (String) attributes.get("picture");
        }
    }
    public static class KakaoOAuth2UserInfo extends OAuth2UserInfo {

        public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
            super(attributes,SocialType.KAKAO);
        }

        @Override
        public String getEmail() {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");

            if (account == null) {
                return null;
            }

            return (String) account.get("email");
        }

        @Override
        public String getId() {
            return attributes.get("id").toString();
        }

        @Override
        public String getNickname() {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");

            if (account == null) {
                return null;
            }

            Map<String, Object> profile = (Map<String, Object>) account.get("profile");

            if (profile == null) {
                return null;
            }

            return (String) profile.get("nickname");
        }

        @Override
        public String getImageUrl() {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");

            if (account == null) {
                return null;
            }

            Map<String, Object> profile = (Map<String, Object>) account.get("profile");

            if (profile == null) {
                return null;
            }

            return (String) profile.get("thumbnail_image_url");
        }
    }

    public static class NaverOAuth2UserInfo extends OAuth2UserInfo {

        public NaverOAuth2UserInfo(Map<String, Object> attributes) {
            super((Map<String, Object>) attributes.get("response"),SocialType.NAVER);
        }

        @Override
        public String getEmail() {
            return (String) attributes.get("email");
        }

        @Override
        public String getId() {
            return (String) attributes.get("id");
        }

        @Override
        public String getNickname() {
            return (String) attributes.get("nickname");
        }

        @Override
        public String getImageUrl() {
            return (String) attributes.get("profile_image");
        }
    }
}
