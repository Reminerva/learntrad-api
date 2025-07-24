package com.learntrad.microservices.marketdata.service.intrface;

import com.learntrad.microservices.marketdata.model.request.search.SearchMarketDataRequest;
import com.learntrad.microservices.marketdata.model.response.MarketDataResponse;
import com.learntrad.microservices.marketrealtime.event.RealtimeDataEvent;

public interface MarketDataService {

    MarketDataResponse fetchMarketData(String marketDataType, SearchMarketDataRequest request);
    MarketDataResponse fetchMarketDataTick(String marketDataType);
    MarketDataResponse fetchMarketDataByTimeBucketStart(String marketDataType, String timeBucketStart);
    void saveLatestTick(RealtimeDataEvent realtimeDataEvent);

}
