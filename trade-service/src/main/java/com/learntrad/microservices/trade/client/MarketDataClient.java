package com.learntrad.microservices.trade.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

import com.learntrad.microservices.shared.constant.ApiBash;

public interface MarketDataClient {

    @GetExchange(ApiBash.MARKET_DATA + "/{marketDataType}/{timeBucketStart}")
    String fetchMarketDataByTimeBucketStart(@RequestHeader("Authorization") String authHeader, @PathVariable String marketDataType, @PathVariable String timeBucketStart);

}
