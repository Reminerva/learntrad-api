package com.learntrad.microservices.auth.config;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.learntrad.microservices.auth.util.JwtUtil;

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
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String jti = extractJti(token);

                Boolean isBlacklisted = redisTemplate.hasKey("blacklist:" + jti);
                if (Boolean.TRUE.equals(isBlacklisted)) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token is blacklisted");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":500,\"message\":\"" + e.getMessage() + "\",\"data\":null}");
        }
    }

    public String extractJti(String token) {
        try {
            return jwtUtil.getClaims(token).getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Date extractExpiration(String token) {
        try {
            return jwtUtil.getClaims(token).getExpiration();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}