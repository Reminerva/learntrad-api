package com.learntrad.microservices.shared.aspect;

import com.learntrad.microservices.shared.annotation.RequireRoles;
import com.learntrad.microservices.shared.jwt.JwtClaim;
import com.learntrad.microservices.shared.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RequireRolesAspect {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    @Before("@annotation(requireRoles)")
    public void checkRoles(RequireRoles requireRoles) {
        String authHeader = request.getHeader("Authorization");
        JwtClaim claims = jwtUtil.getClaims(authHeader);

        List<String> userRoles = claims.getRoles();
        List<String> requiredRoles = List.of(requireRoles.value());

        boolean authorized = userRoles.stream().anyMatch(requiredRoles::contains);

        if (!authorized) {
            log.error("Access denied. Required roles: {}", requiredRoles);
            throw new RuntimeException("Access denied");
        }
    }
}
