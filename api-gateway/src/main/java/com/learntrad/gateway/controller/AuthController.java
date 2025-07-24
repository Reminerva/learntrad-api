package com.learntrad.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learntrad.gateway.config.JwtBlacklistFilter;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtBlacklistFilter jwtBlacklistFilter;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String jti = jwtBlacklistFilter.extractJti(token);
        Date expiration = jwtBlacklistFilter.extractExpiration(token); // ambil waktu exp dari token

        Duration ttl = Duration.between(Instant.now(), expiration.toInstant());
        redisTemplate.opsForValue().set("blacklist:" + jti, "true", ttl);

        return ResponseEntity.ok("Logged out");
    }

}