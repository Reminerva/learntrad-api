package com.learntrad.microservices.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(
    servers = {
        @Server(url = "http://localhost:9000", description = "API Gateway")
    }
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customerServiceAPI() {
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )
            .info(new Info().title("Customer Service API")
                .version("1.0.0")
                .license(new License().name("Apache 2.0")))
            .externalDocs(new ExternalDocumentation()
                .description("Customer Service External Documentation")
                .url("https://customer-service-learntrad-dummy.com/docs"))
            ;
    }

}
