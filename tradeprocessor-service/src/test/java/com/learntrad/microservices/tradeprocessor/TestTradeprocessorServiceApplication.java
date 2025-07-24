package com.learntrad.microservices.tradeprocessor;

import org.springframework.boot.SpringApplication;

public class TestTradeprocessorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(TradeprocessorServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
