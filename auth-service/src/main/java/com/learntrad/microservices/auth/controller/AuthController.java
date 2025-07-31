package com.learntrad.microservices.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learntrad.microservices.auth.model.request.LoginRequest;
import com.learntrad.microservices.auth.model.request.RefreshTokenRequest;
import com.learntrad.microservices.auth.model.request.RegisterRequest;
import com.learntrad.microservices.auth.model.response.TokenResponse;
import com.learntrad.microservices.auth.service.intrfcae.AuthService;
import com.learntrad.microservices.shared.constant.ApiBash;
import com.learntrad.microservices.shared.model.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RequiredArgsConstructor
@RequestMapping(ApiBash.AUTH_API)
@RestController
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<CommonResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        System.out.println("AAAA");
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(CommonResponse.<TokenResponse>builder()
            .data(response)
            .status(HttpStatus.OK.value()).message(ApiBash.LOGIN_SUCCESS)
            .build());
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<TokenResponse>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(CommonResponse.<TokenResponse>builder()
            .data(null)
            .status(HttpStatus.OK.value()).message(ApiBash.REGISTER_SUCCESS)
            .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<TokenResponse>> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.ok(CommonResponse.<TokenResponse>builder()
            .data(null)
            .status(HttpStatus.OK.value()).message(ApiBash.LOGOUT_SUCCESS)
            .build());
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<CommonResponse<TokenResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(CommonResponse.<TokenResponse>builder()
            .data(response)
            .status(HttpStatus.OK.value()).message(ApiBash.REFRESH_TOKEN_SUCCESS)
            .build());
    }

    @PostMapping("/validate-token")
    public ResponseEntity<CommonResponse<Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
        boolean isValid = authService.validateToken(authHeader);
        return ResponseEntity.ok(CommonResponse.<Boolean>builder()
            .data(isValid)
            .status(HttpStatus.OK.value()).message(isValid ? ApiBash.TOKEN_VALID : ApiBash.TOKEN_INVALID)
            .build());
    }
}
