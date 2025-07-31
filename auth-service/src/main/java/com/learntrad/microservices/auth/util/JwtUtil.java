package com.learntrad.microservices.auth.util;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.net.URI;
import java.net.URL;
import java.security.PublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final PublicKey publicKey;

    public JwtUtil(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) throws Exception {
        try {
            URI uri = URI.create(issuerUri + "/protocol/openid-connect/certs");
            URL jwksUrl = uri.toURL();
            JWKSet jwkSet = JWKSet.load(jwksUrl.openStream()); // auto-close karena stream
            JWK jwk = jwkSet.getKeys().get(0);
            RSAKey rsaKey = (RSAKey) jwk;
            this.publicKey = rsaKey.toRSAPublicKey();
        } catch (Exception e) {
            throw e;
        }
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                        .setSigningKey(publicKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
        } catch (Exception e) {
            throw e;
        }
    }
}
