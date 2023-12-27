package com.meow.footprint.global.security.filter;

import com.meow.footprint.global.result.error.exception.BusinessException;
import com.meow.footprint.global.util.JWTTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.meow.footprint.global.result.error.ErrorCode.*;

@Log4j2
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    @Value("${auth.whiteList}")
    private String[] whiteList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null) {
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            for (String list : whiteList) {
                if (antPathMatcher.match(list, path)) {
                    log.info("pass token filter .....");
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }

        try {
            // Request Header 에서 JWT 토큰 추출
            String token = parseBearerToken(auth);

            if (path.equals("/members/reissue")) {
                filterChain.doFilter(request, response);
                return;
            }

            //토큰 유효성 검사
            if (jwtTokenProvider.validateAndGetClaims(token) != null) {
                if (redisTemplate.opsForValue().get(token) != null) {
                    throw new BusinessException(BLACK_TOKEN);
                }
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {  // TODO: 2023-12-24 토큰예외 메시지 수정
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().println("토큰 에러");
        }
    }

    private String parseBearerToken(String auth) {
        return Optional.of(auth)
                .filter(token -> token.substring(0, 7).equalsIgnoreCase("Bearer "))
                .map(token -> token.substring(7))
                .orElseThrow(() -> new BusinessException(JWT_BADTYPE));
    }
}
