package com.meow.footprint.domain.auth.dto;

import com.meow.footprint.domain.member.entity.SocialType;
import java.util.Map;
import lombok.Getter;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;
    @Getter
    protected SocialType socialType;
    public OAuth2UserInfo(Map<String, Object> attributes,SocialType socialType) {
        this.attributes = attributes;
        this.socialType = socialType;
    }

    public abstract String getEmail();

    public abstract String getId(); //소셜 식별 값 : 구글 - "sub", 카카오 - "id", 네이버 - "id"
    public abstract String getNickname();
    public abstract String getImageUrl();
}
