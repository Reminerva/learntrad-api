package com.learntrad.microservices.trade;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;

import com.learntrad.microservices.shared.constant.ApiBash;
import com.learntrad.microservices.trade.stubs.CustomerClientStub;
import com.learntrad.microservices.trade.stubs.MarketDataClientStub;

import io.restassured.RestAssured;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class TradeServiceApplicationTests {

    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13-alpine");

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        postgres.start();
    }

	@Test
	void shouldCreateTrade() {
        String request = """
                {
                    "customerId": "a",
                    "priceAt": 10.0,
                    "stopLossAt": 9.0,
                    "takeProfitAt": 12,
                    "transactedAt": "2020-12-12T22:00:00",
                    "productName": "a",
                    "productId": "a",
                    "tradeStatus": "pending",
                    "tradeType": "buy stop"
                }
            """;

        CustomerClientStub.stubCustomerCall("a");
        MarketDataClientStub.stubMarketDataCall("a");

        RestAssured.given()
            .contentType("application/json")
            .body(request)
            .when()
            .post(ApiBash.TRADE)
            .then()
            .statusCode(201)
            .log().all()
            .body("id", Matchers.notNullValue())
            .body("customerId", Matchers.is("a"))
            .body("priceAt", Matchers.is(10.0F))
            .body("stopLossAt", Matchers.is(9.0F))
            .body("takeProfitAt", Matchers.is(12.0F))
            .body("transactedAt", Matchers.is("2020-12-12T22:00:00"))
            .body("productName", Matchers.is("a"))
            .body("productId", Matchers.is("a"))
            .body("tradeStatus", Matchers.is("pending"))
            .body("tradeType", Matchers.is("buy stop"));
	}

}
