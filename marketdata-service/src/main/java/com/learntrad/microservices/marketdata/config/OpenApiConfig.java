package com.learntrad.microservices.marketdata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productServiceAPI() {
        return new OpenAPI()
                    .info(new Info().title("Market Data Service API")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0")))
                    .externalDocs(new ExternalDocumentation()
                        .description("Market Data Service External Documentation")
                        .url("https://product-service-learntrad-dummy.com/docs"));
    }

}
