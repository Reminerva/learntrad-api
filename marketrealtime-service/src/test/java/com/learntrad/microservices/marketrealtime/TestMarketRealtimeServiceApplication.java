package com.learntrad.microservices.marketrealtime;

import org.springframework.boot.SpringApplication;

public class TestMarketRealtimeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(MarketRealtimeServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
