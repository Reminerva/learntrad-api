package com.learntrad.microservices.trade.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.learntrad.microservices.shared.constant.ApiBash;

public class MarketDataClientStub {

    public static void stubMarketDataCall(String productId) {
        stubFor(get(urlEqualTo(ApiBash.MARKET_DATA + "/" + productId))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                        {
                            "id": "a",
                            "name": "a"
                        }
                        """)));
    }
}
