package com.learntrad.microservices.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
@OpenAPIDefinition(
    servers = {
        @Server(url = "http://localhost:9000", description = "API Gateway")
    }
)
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceAPI() {
        return new OpenAPI()
                    .info(new Info().title("Auth Service API")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0")))
                    .externalDocs(new ExternalDocumentation()
                        .description("Auth Service External Documentation")
                        .url("https://auth-service-learntrad-dummy.com/docs"));
    }

}
