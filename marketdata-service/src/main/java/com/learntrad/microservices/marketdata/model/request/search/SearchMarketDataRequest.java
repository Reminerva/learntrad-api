package com.learntrad.microservices.marketdata.model.request.search;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchMarketDataRequest {
    private Instant timeBucketStartMin;
    private Instant timeBucketStartMax;
    private String timeFrame;

    private String direction;
}
