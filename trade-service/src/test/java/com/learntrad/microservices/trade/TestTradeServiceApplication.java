package com.learntrad.microservices.trade;

import org.springframework.boot.SpringApplication;

import com.learntrad.microservices.trade.TradeServiceApplication;

public class TestTradeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(TradeServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
