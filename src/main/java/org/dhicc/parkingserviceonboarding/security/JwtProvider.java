package org.dhicc.parkingserviceonboarding.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private final long expirationMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.expirationMs = expirationMs;
    }

    /** ✅ UserDetails 기반 JWT 생성 */
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());  // 🔥 `generateToken(String username)` 호출
    }

    /** ✅ `String username` 기반 JWT 생성 (테스트 코드에서 사용) */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** ✅ JWT 토큰 검증 */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** ✅ JWT 토큰에서 사용자 이름 추출 */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
