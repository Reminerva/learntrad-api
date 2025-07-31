package com.learntrad.microservices.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(scanBasePackages = "com.learntrad.microservices")
public class AuthServiceApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("CLIENT_ID", dotenv.get("CLIENT_ID"));
        System.setProperty("CLIENT_SECRET", dotenv.get("CLIENT_SECRET"));
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
