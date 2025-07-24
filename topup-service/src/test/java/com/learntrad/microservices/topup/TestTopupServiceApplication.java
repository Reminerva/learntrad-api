package com.learntrad.microservices.topup;

import org.springframework.boot.SpringApplication;

public class TestTopupServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(TopupServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
