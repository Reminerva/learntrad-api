package com.learntrad.gateway.config;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.learntrad.gateway.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtBlacklistFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String jti = extractJti(token); // Buat fungsi extractJti pakai JWT parser

            Boolean isBlacklisted = redisTemplate.hasKey("blacklist:" + jti);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token is blacklisted");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    public String extractJti(String token) {
        return jwtUtil.getClaims(token).getId();
    }

    public Date extractExpiration(String token) {
        return jwtUtil.getClaims(token).getExpiration();
    }
}