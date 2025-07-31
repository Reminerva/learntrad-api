package com.learntrad.gateway.client;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;

import com.learntrad.microservices.shared.constant.ApiBash;

public interface AuthClient {

    @PostExchange(ApiBash.AUTH_API + "/validate-token")
    String validateToken(@RequestHeader("Authorization") String authHeader);

}