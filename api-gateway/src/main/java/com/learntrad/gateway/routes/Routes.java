package com.learntrad.gateway.routes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import com.learntrad.gateway.config.JwtFilter;
import com.learntrad.microservices.shared.constant.ApiBash;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;

@Configuration
public class Routes {

    @Autowired
    private JwtFilter jwtFilter;

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @Value("${marketdata.service.url}")
    private String marketDataServiceUrl;

    @Value("${trade.service.url}")
    private String tradeServiceUrl;

    @Value("${topup.service.url}")
    private String topUpServiceUrl;

    @Value("${springdoc.swagger-ui.urls[0].url}")
    private String customerSwaggerUrl;

    @Value("${springdoc.swagger-ui.urls[1].url}")
    private String marketDataSwaggerUrl;

    @Value("${springdoc.swagger-ui.urls[2].url}")
    private String tradeSwaggerUrl;

    @Value("${springdoc.swagger-ui.urls[3].url}")
    private String topUpSwaggerUrl;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Value("${springdoc.swagger-ui.urls[4].url}")
    private String authSwaggerUrl;

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute() {
        return route("auth_service")
            .route(RequestPredicates.path(ApiBash.AUTH_API + "/**"), http())
            .before(uri(authServiceUrl))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("authServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authServiceSwaggerRoute() {
        return route("auth_service_swagger")
            .route(RequestPredicates.path(authSwaggerUrl), http())
            .before(uri(authServiceUrl))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("authServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .filter(setPath("/api-docs"))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> customerServiceRoute() {
        return route("customer_service")
            .route(RequestPredicates.path(ApiBash.CUSTOMER + "/**"), http())
            .before(uri(customerServiceUrl))
            .filter(jwtFilter.jwtAuthFilter())
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("customerServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> customerServiceSwaggerRoute() {
        return route("customer_service_swagger")
            .route(RequestPredicates.path(customerSwaggerUrl), http())
            .before(uri(customerServiceUrl))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("customerServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .filter(setPath("/api-docs"))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> marketDataServiceRoute() {
        return route("marketdata_service")
            .route(RequestPredicates.path(ApiBash.MARKET_DATA + "/**"), http())
            .before(uri(marketDataServiceUrl))
            .filter(jwtFilter.jwtAuthFilter())
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("marketDataServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> marketDataServiceSwaggerRoute() {
        return route("marketdata_service_swagger")
            .route(RequestPredicates.path(marketDataSwaggerUrl), http())
            .before(uri(marketDataServiceUrl))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("marketDataServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .filter(setPath("/api-docs"))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> tradeServiceRoute() {
        return route("trade_service")
            .route(RequestPredicates.path(ApiBash.TRADE + "/**"), http())
            .before(uri(tradeServiceUrl))
            .filter(jwtFilter.jwtAuthFilter())
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("tradeServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> tradeServiceSwaggerRoute() {
        return route("trade_service_swagger")
            .route(RequestPredicates.path(tradeSwaggerUrl), http())
            .before(uri(tradeServiceUrl))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("tradeServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .filter(setPath("/api-docs"))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> topUpServiceRoute() {
        return route("topup_service")
            .route(RequestPredicates.path(ApiBash.TOP_UP + "/**"), http())
            .before(uri(topUpServiceUrl))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("topUpServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> topUpServiceSwaggerRoute() {
        return route("topup_service_swagger")
            .route(RequestPredicates.path(topUpSwaggerUrl), http())
            .before(uri(topUpServiceUrl))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("topUpServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
            .filter(setPath("/api-docs"))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
            .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                                        .body("Service Unavailable, Please try again later"))
            .POST("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                                        .body("Service Unavailable, Please try again later"))
            .PUT("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                                        .body("Service Unavailable, Please try again later"))
            .DELETE("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                                        .body("Service Unavailable, Please try again later"))
            .build();
    }

}
