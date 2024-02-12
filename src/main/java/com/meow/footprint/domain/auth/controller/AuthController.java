package com.meow.footprint.domain.auth.controller;

import static com.meow.footprint.global.result.ResultCode.LOGIN_SUCCESS;

import com.meow.footprint.domain.auth.dto.KakaoLoginReq;
import com.meow.footprint.domain.auth.dto.NaverLoginReq;
import com.meow.footprint.domain.auth.service.AuthService;
import com.meow.footprint.domain.member.dto.LoginTokenDTO;
import com.meow.footprint.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "로그인 관련 api")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/kakao")
    public ResponseEntity<ResultResponse> loginOauth(@RequestBody KakaoLoginReq kakaoLoginReq) {
        LoginTokenDTO loginOauth = authService.loginOauth(kakaoLoginReq);
        return ResponseEntity.ok(ResultResponse.of(LOGIN_SUCCESS, loginOauth));
    }

    @PostMapping("/login/naver")
    public ResponseEntity<ResultResponse> loginOauth(@RequestBody NaverLoginReq naverLoginReq) {
        LoginTokenDTO loginOauth = authService.loginOauth(naverLoginReq);
        return ResponseEntity.ok(ResultResponse.of(LOGIN_SUCCESS, loginOauth));
    }
}
