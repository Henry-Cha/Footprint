package com.meow.footprint.global.util;

import com.meow.footprint.domain.member.dto.LoginResponse;
import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.global.result.error.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.meow.footprint.global.result.error.ErrorCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTTokenProvider {
    @Value("${JWT.SECRET_KEY}")
    private String key;
    @Value("${JWT.VALIDITY_TIME}")
    private long tokenValidityTime;
    @Value("${JWT.REFRESH_TIME}")
    private long refreshTokenValidityTime;

    public LoginResponse getLoginResponse(Member member){
        return new LoginResponse(member.getId(), generateAccessToken(member),generateRefreshToken(member));
    }
    //사용자 정보를 기반으로 토큰을 생성하여 반환 해주는 메서드
    public String generateAccessToken(Member member) {
        // 권한 가져오기
        String authority = "USER";
//        authorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));

        return Jwts.builder() // TODO: 2023-12-24 토큰 권한 수정
                .signWith(createKey())   // 서명
                .setSubject(member.getId())  // JWT 토큰 제목
                .claim("auth",authority)  //권한정보 저장
                .setExpiration(Date.from(ZonedDateTime.now().plusDays(tokenValidityTime).toInstant()))    // JWT 토큰 만료 시간
                .compact();
    }
    public String generateRefreshToken(Member member) {
        return Jwts.builder()
                .signWith(createKey())   // 서명
                .setSubject(member.getId())  // JWT 토큰 제목
                .setExpiration(Date.from(ZonedDateTime.now().plusDays(refreshTokenValidityTime).toInstant()))    // JWT 토큰 만료 시간
                .compact();
    }
    // HS512 알고리즘을 사용해 서명
    private Key createKey() {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    //토큰을 기반으로 사용자 정보를 반환 해주는 메서드
    public String parseTokenToUserInfo(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(createKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    //유효한 토큰인지 확인 해주는 메서드
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            throw new BusinessException(JWT_MALFORM);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(JWT_EXPIRED);
        } catch (RuntimeException e) {
            throw new BusinessException(JWT_INVALID);
        }
    }
    //토큰 정보를 기반으로 Claims 정보를 반환받는 메서드
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(createKey()).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {  // TODO: 2023-12-24 예외처리 수정 
            return e.getClaims();
        }
    }
    // 토큰의 정보로 Authentication 가져옴
    public Authentication getAuthentication(String accessToken) {
        Claims claims = getClaims(accessToken);

        // 클레임에서 권한 정보 가져오기
        if (claims.get("auth") == null) {
            throw new BusinessException(JWT_INVALID);
        }
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }
}