package com.learntrad.microservices.marketdata.model.response;

import java.time.Instant;
import java.util.List;

import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketDataResponse {
    private Instant timeBucketStartMin;
    private Instant timeBucketEndMax;
    private EMarketDataType marketDataType;
    private Long dataCount;
    private List<?> marketData;
}
