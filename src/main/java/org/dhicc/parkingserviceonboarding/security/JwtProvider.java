package org.dhicc.parkingserviceonboarding.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)); // ✅ SecretKey 타입으로 변환
        this.expirationMs = expirationMs;
    }

    // ✅ 기존 메서드 유지 (역할 없이 생성)
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    // ✅ 기존 메서드 유지
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key) // ✅ SecretKey 명확히 적용
                .compact();
    }

    // ✅ 역할(Role) 정보를 추가한 새로운 메서드 추가
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // ✅ 역할 정보 추가

        return Jwts.builder()
                .claims(claims) // ✅ 클레임에 역할 추가
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key) // ✅ SecretKey 명확히 적용
                .compact();
    }

    // ✅ JWT 유효성 검증 (최신 방식 적용)
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key) // ✅ 최신 방식 적용 (SecretKey 타입으로 명확히 설정)
                    .build()
                    .parseSignedClaims(token); // ✅ 최신 방식 유지
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ✅ 토큰에서 사용자 이름 추출 (최신 방식 적용)
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key) // ✅ 최신 방식 적용
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject(); // ✅ 최신 방식 유지
    }
}
