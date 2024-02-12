package com.meow.footprint.domain.auth.dto;

import com.meow.footprint.domain.member.entity.SocialType;

public interface OAuthLoginReq {

    default SocialType getProviderName() {
        return null;
    }

    default String getAuthorizationCode() {
        return null;
    }

    default String getState() {
        return null;
    }
}
