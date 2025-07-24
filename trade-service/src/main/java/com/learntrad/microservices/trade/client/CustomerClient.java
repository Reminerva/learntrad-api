package com.learntrad.microservices.trade.client;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

public interface CustomerClient {

    @GetExchange("/api/customer/me")
    String getMe(@RequestHeader("Authorization") String authHeader);

}
