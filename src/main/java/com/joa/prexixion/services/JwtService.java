package com.joa.prexixion.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final String SECRET_KEY = "clave-super-secreta-de-32-bytes!!"; // mínimo 256 bits

    public String generateToken(String username, List<String> permisos) {
        return Jwts.builder()
                .setSubject(username)
                .claim("permisos", permisos)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hora
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
