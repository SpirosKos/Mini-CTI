package com.mini.cti.authentication;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "your-secret-key-here";
    private final long EXPIRATION_TIME = 43200000;    // 12 hours

    private SecretKey getSigningKey(){
       byte[] SECRET_BYTE = SECRET_KEY.getBytes();
       return Keys.hmacShaKeyFor(SECRET_BYTE);
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(EXPIRATION_TIME)))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateKey(String token){
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(generateToken(token));
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
