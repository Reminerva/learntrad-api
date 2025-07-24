package com.learntrad.microservices.trade.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.learntrad.microservices.shared.constant.ApiBash;

public class CustomerClientStub {

    public static void stubCustomerCall(String customerId) {
        stubFor(get(urlEqualTo(ApiBash.CUSTOMER + "/" + customerId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                        {
                            "id": "a",
                            "fullname": "a",
                            "address": "a",
                            "birthDate": "a",
                            "balance": 10.0,
                            "userId": "a"
                        }
                        """)));
    }
}
