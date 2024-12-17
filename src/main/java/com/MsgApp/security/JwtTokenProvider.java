package com.MsgApp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    // application.properties'den JWT secret key'i alıyoruz
    private final Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token'ın geçerlilik süresini properties'den alıyoruz (milisaniye cinsinden)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Kullanıcı bilgilerinden JWT token oluşturur
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    // Token'dan kullanıcı adını çıkarır
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Token'ın geçerli olup olmadığını kontrol eder
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            // Token imzası geçersiz
            return false;
        } catch (MalformedJwtException ex) {
            // Token formatı hatalı
            return false;
        } catch (ExpiredJwtException ex) {
            // Token süresi dolmuş
            return false;
        } catch (UnsupportedJwtException ex) {
            // Token desteklenmiyor
            return false;
        } catch (IllegalArgumentException ex) {
            // Token boş
            return false;
        }
    }

    // Token'dan Authentication nesnesi oluşturur
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = new CustomUserDetails(getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}