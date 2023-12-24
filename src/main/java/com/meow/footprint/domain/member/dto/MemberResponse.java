package com.meow.footprint.domain.member.dto;

import com.meow.footprint.domain.member.entity.Member;

public record MemberResponse(String id, String name) {
    public static MemberResponse from(Member member){
        return new MemberResponse(member.getId(),member.getName());
    }
}
