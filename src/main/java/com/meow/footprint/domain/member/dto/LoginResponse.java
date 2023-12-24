package com.meow.footprint.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    String userId;
    String accessToken;
    String refreshToken;
}
