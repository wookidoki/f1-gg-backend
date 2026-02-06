package com.f1gg.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:f1gg-secret-key-must-be-at-least-256-bits-long-for-hs256}")
    private String secretKey;

    @Value("${jwt.access-token-validity:3600000}") // 1시간
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity:604800000}") // 7일
    private long refreshTokenValidity;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    // Access Token 생성
    public String createAccessToken(String email, String role) {
        return createToken(email, role, accessTokenValidity, "ACCESS");
    }

    // Refresh Token 생성
    public String createRefreshToken(String email) {
        return createToken(email, null, refreshTokenValidity, "REFRESH");
    }

    private String createToken(String email, String role, long validity, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        JwtBuilder builder = Jwts.builder()
                .subject(email)
                .claim("type", tokenType)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    // 토큰에서 이메일 추출
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰에서 역할 추출
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
        }
        return false;
    }

    // Access Token인지 확인
    public boolean isAccessToken(String token) {
        String type = getClaims(token).get("type", String.class);
        return "ACCESS".equals(type);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰 남은 유효시간 (밀리초)
    public long getTokenRemainingTime(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }
}
