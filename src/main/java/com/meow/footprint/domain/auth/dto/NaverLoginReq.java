package com.meow.footprint.domain.auth.dto;

import com.meow.footprint.domain.member.entity.SocialType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLoginReq implements OAuthLoginReq {

    private String authorizationCode;
    private String state;

    @Override
    public SocialType getProviderName() {
        return SocialType.NAVER;
    }
}
