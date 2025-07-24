package com.learntrad.microservices.topup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.learntrad.microservices")
public class TopupServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TopupServiceApplication.class, args);
	}

}
