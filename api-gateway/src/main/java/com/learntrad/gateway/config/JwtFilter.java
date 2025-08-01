package com.learntrad.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException; // Import ini
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learntrad.gateway.client.AuthClient;

@Configuration
public class JwtFilter {

    private final AuthClient authClient;
    private final ObjectMapper mapper;

    public JwtFilter(AuthClient authClient, ObjectMapper mapper) {
        this.authClient = authClient;
        this.mapper = mapper;
    }

    @Bean
    public HandlerFilterFunction<ServerResponse, ServerResponse> jwtAuthFilter() {
        return (request, next) -> {
            String authHeader = request.headers().firstHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
            }

            try {
                String responseBody = authClient.validateToken(authHeader);

                JsonNode responseJsonNode = mapper.readTree(responseBody);
                boolean isValid = responseJsonNode.has("data") && "true".equals(responseJsonNode.get("data").asText());

                if (!isValid) {
                    return ServerResponse.status(HttpStatus.UNAUTHORIZED).body("Token invalid: " + responseBody);
                }

                return next.handle(request);

            } catch (WebClientResponseException e) {
                return ServerResponse.status(HttpStatus.UNAUTHORIZED).body("Token invalid: " + e.getResponseBodyAsString());
            } catch (Exception e) {
                e.printStackTrace();
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during token validation.");
            }
        };
    }
}