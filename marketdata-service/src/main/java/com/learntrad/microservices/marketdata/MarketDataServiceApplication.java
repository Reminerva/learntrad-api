package com.learntrad.microservices.marketdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(scanBasePackages = "com.learntrad.microservices")
public class MarketDataServiceApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("TWELVE_DATA_API_KEY", dotenv.get("TWELVE_DATA_API_KEY"));
		SpringApplication.run(MarketDataServiceApplication.class, args);
	}

}
