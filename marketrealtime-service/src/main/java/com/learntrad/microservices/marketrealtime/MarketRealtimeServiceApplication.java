package com.learntrad.microservices.marketrealtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.learntrad.microservices"})
public class MarketRealtimeServiceApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("TWELVE_DATA_API_KEY", dotenv.get("TWELVE_DATA_API_KEY"));
		SpringApplication.run(MarketRealtimeServiceApplication.class, args);
	}

}
