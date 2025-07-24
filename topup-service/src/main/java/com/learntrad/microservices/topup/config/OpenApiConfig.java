package com.learntrad.microservices.topup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI topupServiceAPI() {
        return new OpenAPI()
                    .info(new Info().title("Top Up Service API")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0")))
                    .externalDocs(new ExternalDocumentation()
                        .description("Top Up Service External Documentation")
                        .url("https://topup-service-learntrad-dummy.com/docs"));
    }

}
