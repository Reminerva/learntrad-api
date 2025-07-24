package com.learntrad.microservices.customer;

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
class CustomerServiceApplicationTests {

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
	void shouldCreateCustomer() {
        String request = """
                {
                    "fullname" : "a",
                    "address" : "a",
                    "birthDate" : "2020-01-01"
                }
            """;

        RestAssured.given()
            .contentType("application/json")
            .body(request)
            .header("Authorization", "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJBdG4xVzBjcVVxSGVWOTlSa0NkLTNac3JPVFpyeVAyZlk3Z3VLcGd2VGhRIn0.eyJleHAiOjE3NTA4MzE2MjMsImlhdCI6MTc1MDgzMTMyMywianRpIjoiMGNjZTEyMzEtY2E5Yi00MzhmLTgyOTYtMGFjNzRlOTliNmVlIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgxL3JlYWxtcy9zcHJpbmctbGVhcm50cmFkLW1pY3Jvc2VydmljZXMtc2VjdXJpdHktcmVhbG0iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNDZiMGIzMjUtY2JlOS00ZjI0LWI4NTYtMGExYTBiZWRiYTZiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic3ByaW5nLWxlYXJudHJhZC1taWNyb3NlcnZpY2VzLWNsaWVudC1pZCIsInNlc3Npb25fc3RhdGUiOiJhZjcwMTIxYy1iMGRjLTQwNDgtYTgzNS03ZjBkZjM0YTQ5NjYiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXNwcmluZy1sZWFybnRyYWQtbWljcm9zZXJ2aWNlcy1zZWN1cml0eS1yZWFsbSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJjdXN0b21lciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6ImFmNzAxMjFjLWIwZGMtNDA0OC1hODM1LTdmMGRmMzRhNDk2NiIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlciIsImVtYWlsIjoiYXNkZmFzZGZAZ21haWwuY29tIn0.cBsp6A65rI9TaWvrASSdifjuDEMkV6JP57p6J4FCq9ihXQrONN_WKBsAOZlE6wSqAMvcFpVZ8ywv80BQI6KOOlrzK_XdsYY2w03YV3cijEgZlDgX9tuiziyyio0ocRFnUWkVDrVpGgLXJP0Ey66W7f1ENe2TmRW4_NFMsS7FVjfZmMK9sXIJc9PIkjJo3uLr2U2jumwNDLM5tJMT28_ZLNKwa54oxkM8-bKDjMVn4NcULnQZPK-VK1QQ44TZSiWxqSphTX4HQt3Io4FGCoHzUxtdEn1Lc24eszraBz9uNModZ-DP39T9tGY4dka9B3NfOZDWqMikeaD859D1gsOBUg")
            .when()
            .post("api/customer")
            .then()
            .statusCode(201)
            .body("data.fullname", Matchers.is("a"))
            .body("data.address", Matchers.is("a"))
            .body("data.birthDate", Matchers.is("2020-01-01"))
            .body("data.balance", Matchers.is(0.0F))
            .body("data.userId", Matchers.is("46b0b325-cbe9-4f24-b856-0a1a0bedba6b"));
	}

}
