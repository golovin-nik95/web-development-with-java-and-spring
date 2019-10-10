package com.griddynamics.ngolovin.store.auth.jwt;

import com.griddynamics.ngolovin.store.auth.domain.UserEntity;
import com.griddynamics.ngolovin.store.auth.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserService userService;

    @Value("${store.jwt.secret}")
    private String jwtSecret;

    @Value("${store.jwt.token-ttl}")
    private long jwtTokenTtl;

    public String generateToken(Authentication authentication) {
        String username = ((User) authentication.getPrincipal()).getUsername();
        Long userId = userService.getUserByEmail(username)
                .map(UserEntity::getId)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtTokenTtl);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            log.error("Token validation error", e);
        }

        return false;
    }
}
