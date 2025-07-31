package com.learntrad.microservices.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final String[] swaggerEndpoints = { "/api-docs/**", "/api-docs", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs**", "/swagger-resources/**", "/aggregate/**", "/actuator/prometheus", "/api/auth/**" };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(swaggerEndpoints)
                .permitAll()
                .anyRequest().authenticated())
            .csrf(csrf -> csrf.disable())
            .build();
    }

}
