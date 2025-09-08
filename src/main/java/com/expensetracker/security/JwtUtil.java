package com.expensetracker.security;

import com.expensetracker.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final long accessTokenExpiration = 1000 * 60 * 60; // 1 hour
    private final long refreshTokenExpiration = 1000L * 60 * 60 * 24 * 7; // 7 days

    private final Key accessKey;
    private final Key refreshKey;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.refresh-secret}") String refreshSecret) {
        this.accessKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.refreshKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(refreshSecret));
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        String role = user.getRoleAsString(); 
        claims.put("role", role);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(accessKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(refreshKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public long getAccessTokenExpiry() {
        return accessTokenExpiration;
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(accessKey).build()
                .parseClaimsJws(token).getBody();
    }
}
