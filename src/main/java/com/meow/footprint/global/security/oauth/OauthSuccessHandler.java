package com.meow.footprint.global.security.oauth;

import com.meow.footprint.domain.member.dto.LoginTokenDTO;
import com.meow.footprint.global.result.ResultResponse;
import com.meow.footprint.global.util.JWTTokenProvider;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

import static com.meow.footprint.global.result.ResultCode.LOGIN_SUCCESS;

@Slf4j
@RequiredArgsConstructor
@Component
public class OauthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info(authentication.toString());
        LoginTokenDTO loginResponse = jwtTokenProvider.getLoginResponse(authentication);
        redisTemplate.opsForValue().set("RTK:"+loginResponse.getUserId(),loginResponse.getRefreshToken(), Duration.ofDays(jwtTokenProvider.getRefreshTokenValidityTime()));

        ResultResponse result = ResultResponse.of(LOGIN_SUCCESS,loginResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(result);
        response.getWriter().println(jsonStr);
    }

}