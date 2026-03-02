package com.mini.cti.authentication;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtService {

    @Value("${app.security.secret-key}")
    private  String SECRET_KEY;

    @Value("${app.security.jwt-expiration}")
    private  long EXPIRATION_TIME;

    private SecretKey getSigningKey(){
       byte[] SECRET_BYTE = Decoders.BASE64.decode(SECRET_KEY);
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
