package com.meow.footprint.domain.auth.dto;

import com.meow.footprint.domain.member.entity.SocialType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoLoginReq implements OAuthLoginReq {

    private String authorizationCode;

    @Override
    public SocialType getProviderName() {
        return SocialType.KAKAO;
    }
}
