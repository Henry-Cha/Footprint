package com.meow.footprint.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginTokenDTO {
    private String userId;
    private String accessToken;
    private String refreshToken;
}
