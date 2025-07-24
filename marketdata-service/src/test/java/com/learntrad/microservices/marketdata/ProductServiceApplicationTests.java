package com.learntrad.microservices.marketdata;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;

import io.restassured.RestAssured;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MarketDataServiceApplicationTests {

    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13-alpine");

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        postgresContainer.start();
    }

	@Test
	void shouldCreateMarketData() {
        String marketDataRequest = """
                {
                    "name": "MarketData 1",
                    "description": "Description 1",
                    "price": 10.99
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(marketDataRequest)
                .when()
                .post("api/marketData")
                .then()
                .log()
                .all()
                .statusCode(201)
                .body("name", Matchers.is("MarketData 1"))
                .body("description", Matchers.is("Description 1"))
                .body("price", Matchers.is(10.99F));
    }

}
