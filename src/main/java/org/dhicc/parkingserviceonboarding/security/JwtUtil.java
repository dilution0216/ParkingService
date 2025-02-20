package org.dhicc.parkingserviceonboarding.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expirationTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes); // ✅ SecretKey 생성
        this.expirationTime = expirationTime;
    }

    // ✅ JWT 생성
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)  // ✅ `setSubject()` → `subject()` 로 변경
                .signWith(secretKey) // ✅ `signWith(SecretKey)` 로 변경 (알고리즘 자동 선택됨)
                .expiration(new Date(System.currentTimeMillis() + expirationTime)) // ✅ `setExpiration()` → `expiration()` 변경
                .compact();
    }

    // ✅ JWT에서 Claims(사용자 정보) 추출
    public Claims extractClaims(String token) {
        Jws<Claims> jws = Jwts.parser()  // ✅ `parserBuilder()` → `parser()`로 변경
                .verifyWith(secretKey) // ✅ `setSigningKey()` → `verifyWith(secretKey)` 로 변경
                .build()
                .parseSignedClaims(token); // ✅ `parseClaimsJws()` → `parseSignedClaims()`

        return jws.getPayload(); // ✅ `getBody()` → `getPayload()` 로 변경
    }

    // ✅ JWT에서 사용자 이름(username) 추출
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // ✅ JWT가 만료되었는지 확인
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey) // ✅ 검증 키 설정
                    .build()
                    .parseSignedClaims(token) // ✅ Deprecated된 `parseClaimsJws()` 대신 사용
                    .getPayload();

            return claims.getExpiration().before(new Date()); // ✅ 만료 시간 확인
        } catch (Exception e) {
            return true; // ✅ 만료되었거나 예외 발생 시 true 반환 (예: 변조된 토큰)
        }
    }


    // ✅ JWT 유효성 검증
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }
}
