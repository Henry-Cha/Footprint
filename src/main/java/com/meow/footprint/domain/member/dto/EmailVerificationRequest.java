package com.meow.footprint.domain.member.dto;

public record EmailVerificationRequest(String email,String authCode) {
}
