package com.learntrad.microservices.trade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI tradeServiceAPI() {
        return new OpenAPI()
                    .info(new Info().title("Trade Service API")
                        .version("1.0.0")
                        .description("API for Trade Service")
                        .license(new License().name("Apache 2.0")))
                    .externalDocs(new io.swagger.v3.oas.models.ExternalDocumentation()
                        .description("External Documentation")
                        .url("https://trade-service-dummy-url/docs"));
    }

}
