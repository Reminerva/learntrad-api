package com.learntrad.microservices.auth.service.impl;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.learntrad.microservices.auth.model.request.LoginRequest;
import com.learntrad.microservices.auth.model.request.RefreshTokenRequest;
import com.learntrad.microservices.auth.model.request.RegisterRequest;
import com.learntrad.microservices.auth.model.response.TokenResponse;
import com.learntrad.microservices.auth.service.intrfcae.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.url}")
    private String keycloakUrl;

    @Value("${keycloak.client.id}")
    private String clientId;

    @Value("${keycloak.client.secret}")
    private String clientSecret;

    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate;

    @Override
    public void register(RegisterRequest request) {
        try {
            log.info("Start - Registering user: {}", request.getUsername());

            String accessToken = getAdminAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, Object> user = new HashMap<>();
            user.put("username", request.getUsername());
            user.put("email", request.getEmail());
            user.put("enabled", true);
            user.put("credentials", List.of(Map.of(
                "type", "password",
                "value", request.getPassword(),
                "temporary", false
            )));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(user, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                keycloakUrl + "/admin/realms/" + realm + "/users",
                entity,
                String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to register user. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to register user.");
            }

            log.info("End - User registered: {}", request.getUsername());
        } catch (Exception e) {
            log.error("End - Failed to register user: {}", request.getUsername(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        try {
            String accessToken = getAdminAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBearerAuth(accessToken);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("username", request.getUsername());
            body.add("password", request.getPassword());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            log.info("Start - Logging in user: {}", request.getUsername());
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token", entity, Map.class
            );

            if (response.getStatusCode().isError()) {
                log.error("End - Failed to login user: {}", request.getUsername());
                throw new RuntimeException("Failed to login user: " + request.getUsername() + ", " + response.getBody() + ", " + response.getStatusCode());
            }

            log.info("End - User logged in: {}, response: {}", request.getUsername(), response.getBody());

            Map<String, String> token = response.getBody();
            return new TokenResponse((String) token.get("access_token"), (String) token.get("refresh_token"));
        } catch (Exception e) {
            log.error("Failed to login user: {}", request.getUsername(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return;
            }
            if (redisTemplate.opsForValue().get(token) != null) {
                return;
            }
            log.info("Start - Logging out user: {}", token);
            redisTemplate.opsForValue().set(token, "logout", Duration.ofMinutes(15));
            log.info("End - User logged out: {}", token);
        } catch (Exception e) {
            log.error("Failed to logout user: {}", token, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest refreshToken) {
        try {
            if (refreshToken == null || refreshToken.getRefreshToken().isEmpty()) {
                log.error("End - Failed to refresh token: {}", refreshToken);
                throw new RuntimeException("Failed to refresh token: " + refreshToken);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "refresh_token");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("refresh_token", refreshToken.getRefreshToken());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            log.info("Start - Refreshing token: {}", refreshToken);
            ResponseEntity<Map> response = restTemplate
                    .postForEntity(keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token", request, Map.class);

            if (response.getStatusCode().isError()) {
                log.error("End - Failed to refresh token. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                if (response.getBody() != null && response.getBody().containsKey("error_description")) {
                    throw new RuntimeException("Failed to refresh token: " + response.getBody().get("error_description"));
                } else {
                    throw new RuntimeException("Failed to refresh token. HTTP Status: " + response.getStatusCode());
                }
            }

            log.info("End - Token refreshed: {}, response: {}", refreshToken.getRefreshToken(), response.getBody());

            Map body = response.getBody();
            return new TokenResponse((String) body.get("access_token"), (String) body.get("refresh_token"));
        } catch (HttpClientErrorException.BadRequest e) {
            log.error("End - Failed to refresh token: Bad Request. Error: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to refresh token: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("End - Failed to refresh token: {}", refreshToken, e);
            throw new RuntimeException(e);
        }
    }

    private String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId); // Menggunakan clientId dari @Value
        body.add("client_secret", clientSecret); // Menggunakan clientSecret dari @Value

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                // Jika ingin mendapatkan token admin dari realm 'master' (standar untuk admin-cli):
                // keycloakUrl + "/realms/master/protocol/openid-connect/token",
                //
                // Jika ingin mendapatkan token admin dari realm yang dispesifikasikan oleh 'realm' variable:
                keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                requestEntity,
                Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                if (response.getBody() != null && response.getBody().containsKey("access_token")) {
                    log.info("Successfully obtained admin access token for realm: {}", realm);
                    return (String) response.getBody().get("access_token");
                } else {
                    log.error("Failed to get admin access token: 'access_token' not found in response body.");
                    throw new RuntimeException("Failed to get admin access token: 'access_token' not found.");
                }
            } else {
                log.error("Failed to get admin access token. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to get admin access token. HTTP Status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Failed to get admin access token: 401 Unauthorized. Please check client_id, client_secret, and Keycloak URL/Realm configuration. Error: {}", e.getMessage());
            throw new RuntimeException("Failed to get admin access token: Unauthorized. Check Keycloak client_id, client_secret, and realm configuration.", e);
        } catch (Exception e) {
            log.error("Failed to get admin access token due to an unexpected error. Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get admin access token due to an unexpected error.", e);
        }
    }

    @Override
    public Boolean validateToken(String authHeader) {
        log.info("Start - Validating token: {}", authHeader);
        String token = redisTemplate.opsForValue().get(authHeader);
        if (token != null) {
            log.info("End - Token is invalid: {}", authHeader);
            return false;
        }
        log.info("End - Token is valid: {}", authHeader);
        return true;
    }

}
