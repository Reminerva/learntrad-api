package com.learntrad.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("CORS_FRONTEND_URL", dotenv.get("CORS_FRONTEND_URL"));
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
