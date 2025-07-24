package com.learntrad.microservices.marketdata;

import org.springframework.boot.SpringApplication;

import com.learntrad.microservices.marketdata.MarketDataServiceApplication;

public class TestMarketDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(MarketDataServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
