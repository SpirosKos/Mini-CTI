package com.mini.cti.authentication;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "p4s5w0rd_v3ry_str0ng_4nd_l0ng_3n0ugh_256_bits";
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

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(generateToken(token));
            return true;
        } catch (JwtException e) {
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
