package com.learntrad.microservices.shared.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtClaim {
    private String userId;
    private List<String> roles;
    private String username;
    private String email;
    private Boolean emailVerified;
}
