package com.example.library.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;


@Component
public class JWTGenerator {

    private final String SECRET_KEY = "ThisIsMySecretKey123456789101112"; // Ensure this key is stored securely
    private final Key key = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Claims claims = Jwts.claims().add("Authorities",userPrincipal.getAuthorities())
                .subject(userPrincipal.getUsername()).build();

        return Jwts.builder().claims(claims)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + 86400000)) // Token valid for 1 day
                .signWith(key)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key).build().parseSignedClaims(token).getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect", ex);
        }
    }
}
