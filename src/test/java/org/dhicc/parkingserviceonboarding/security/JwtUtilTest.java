package org.dhicc.parkingserviceonboarding.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @BeforeEach
    void setUp() {
        if (secret == null || secret.isEmpty()) {
            secret = "xJ3R2n1L8G5U8H3c+PzJb9MmY8ZlU5vPg1MlVpZ3NzU=";
        }
        if (expirationTime <= 0) {
            expirationTime = 86400000L;  // 기본 1일 설정
        }
        jwtUtil = new JwtUtil(secret, expirationTime);
    }

    /** ✅ 1. 토큰 생성 및 검증 */
    @Test
    void testGenerateAndValidateToken() {
        String username = "testUser";

        String token = jwtUtil.generateToken(username);
        assertNotNull(token, "⚠ 토큰 생성 실패");

        assertTrue(jwtUtil.validateToken(token, username), "⚠ 토큰 검증 실패");
        assertEquals(username, jwtUtil.extractUsername(token), "⚠ 토큰에서 추출한 사용자명이 다릅니다.");
    }

    /** ✅ 2. 만료된 토큰 검증 */
    @Test
    void testExpiredToken() {
        // 🔹 만료 시간을 현재로부터 10초 전으로 설정
        Instant expiredTime = Instant.now().minusSeconds(10);

        String expiredToken = Jwts.builder()
                .subject("expiredUser")
                .expiration(Date.from(expiredTime))  // ✅ 명확히 만료 시간 지정
                .signWith(getSecretKey(secret))
                .compact();

        // ✅ Clock Skew 허용 적용 (5초)
        JwtParser parser = Jwts.parser()
                .verifyWith(getSecretKey(secret))
                .clockSkewSeconds(15)
                .build();

        Jws<Claims> jws = parser.parseSignedClaims(expiredToken);
        Claims claims = jws.getPayload();

        // ✅ 기대하는 값: 만료된 토큰이므로 true여야 함.
        assertTrue(jwtUtil.isTokenExpired(expiredToken), "⚠ 만료된 토큰이 유효한 것으로 판정됨");
    }



    /** ✅ 3. 클레임(Claims) 추출 테스트 */
    @Test
    void testExtractClaims() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        Claims claims = jwtUtil.extractClaims(token);

        assertEquals(username, claims.getSubject(), "⚠ Claims에서 사용자명을 올바르게 추출하지 못함");
    }

    // 🔹 SecretKey 변환 함수 추가
    private SecretKey getSecretKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
