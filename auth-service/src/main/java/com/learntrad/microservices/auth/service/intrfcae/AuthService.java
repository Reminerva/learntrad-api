package com.learntrad.microservices.auth.service.intrfcae;

import com.learntrad.microservices.auth.model.request.LoginRequest;
import com.learntrad.microservices.auth.model.request.RefreshTokenRequest;
import com.learntrad.microservices.auth.model.request.RegisterRequest;
import com.learntrad.microservices.auth.model.response.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request);
    void register(RegisterRequest request);
    void logout(String token);
    TokenResponse refreshToken(RefreshTokenRequest refreshToken);
    Boolean validateToken(String authHeader);
}
