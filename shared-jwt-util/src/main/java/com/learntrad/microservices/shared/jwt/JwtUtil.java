package com.learntrad.microservices.shared.jwt;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JwtClaim getClaims(String token) {
        try {
            if (token == null || token.isEmpty()) {
                throw new RuntimeException("Token is null or empty");
            }
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String[] chunks = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));

            Map<String, Object> map = mapper.readValue(payload, new TypeReference<>() {});

            String userId = (String) map.get("sub");
            String username = (String) map.get("preferred_username");
            String email = (String) map.get("email");
            Boolean emailVerified = (Boolean) map.get("email_verified");

            Map<String, Object> realmAccess = (Map<String, Object>) map.get("realm_access");
            List<String> roles = (List<String>) realmAccess.get("roles");

            return JwtClaim.builder()
                    .userId(userId)
                    .username(username)
                    .email(email)
                    .emailVerified(emailVerified)
                    .roles(roles)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT format or missing fields", e);
        }
    }
}
