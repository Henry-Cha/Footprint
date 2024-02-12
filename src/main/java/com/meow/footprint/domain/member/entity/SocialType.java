package com.meow.footprint.domain.member.entity;

public enum SocialType {
    KAKAO, NAVER, GOOGLE;

    public String getSocialName(){
        return this.name().toLowerCase();
    }
}
