package com.learntrad.microservices.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customerServiceAPI() {
        return new OpenAPI()
                    .info(new Info().title("Customer Service API")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0")))
                    .externalDocs(new ExternalDocumentation()
                        .description("Customer Service External Documentation")
                        .url("https://customer-service-learntrad-dummy.com/docs"));
    }

}
