package com.learntrad.microservices.marketdata.controller;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learntrad.microservices.marketdata.model.request.search.SearchMarketDataRequest;
import com.learntrad.microservices.marketdata.model.response.MarketDataResponse;
import com.learntrad.microservices.marketdata.service.intrface.MarketDataService;
import com.learntrad.microservices.shared.annotation.RequireRoles;
import com.learntrad.microservices.shared.constant.ApiBash;
import com.learntrad.microservices.shared.constant.ConstantBash;
import com.learntrad.microservices.shared.constant.enumerated.ETimeFrame;
import com.learntrad.microservices.shared.model.response.CommonResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.MARKET_DATA)
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataService marketdataService;

    @GetMapping("/{marketDataType}/tick")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER, ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<MarketDataResponse>> fetchMarketDataTick(@PathVariable("marketDataType") String marketDataType) {
        CommonResponse<MarketDataResponse> response = CommonResponse.<MarketDataResponse>builder()
                .message(ApiBash.FETCH_TICK_SUCCESS)
                .status(HttpStatus.OK.value())
                .data(marketdataService.fetchMarketDataTick(marketDataType))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{marketDataType}")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER, ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<MarketDataResponse>> fetchMarketDataRange(
        @PathVariable("marketDataType") String marketDataType,
        @RequestParam(required = false) String timeBucketStartMin,
        @RequestParam(required = false) String timeBucketStartMax,
        @RequestParam(required = false, defaultValue = "1m") String timeFrame,
        @RequestParam(required = false, defaultValue = "DESC") String direction
    ) {
        try {
            ETimeFrame timeFrameEnum = ETimeFrame.findByDescription(timeFrame);
            Instant timeBucketStartMaxInstant = timeBucketStartMax != null ? 
                timeFrameEnum.truncatedBasedOnTimeFrame(Instant.parse(timeBucketStartMax)) : 
                timeFrameEnum.truncatedBasedOnTimeFrame(Instant.now());

            Instant timeBucketStartMinInstant = timeBucketStartMin != null ? 
                timeFrameEnum.truncatedBasedOnTimeFrame(Instant.parse(timeBucketStartMin)) : 
                timeFrameEnum.truncatedBasedOnTimeFrame(timeFrameEnum.setDefaultTimeBucketStartMin(timeBucketStartMaxInstant));

            SearchMarketDataRequest request = SearchMarketDataRequest.builder()
                    .timeBucketStartMin(timeBucketStartMinInstant)
                    .timeBucketStartMax(timeBucketStartMaxInstant)
                    .direction(direction)
                    .timeFrame(timeFrame)
                    .build();

            MarketDataResponse marketDataResponse = marketdataService.fetchMarketData(marketDataType, request);

            CommonResponse<MarketDataResponse> response = CommonResponse.<MarketDataResponse>builder()
                    .message(ApiBash.FETCH_RANGE_SUCCESS)
                    .status(HttpStatus.OK.value())
                    .data(marketDataResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        

    }

    @GetMapping("/{marketDataType}/{timeBucketStart}")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER, ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<MarketDataResponse>> fetchMarketDataByTimeBucketStart(
        @PathVariable("marketDataType") String marketDataType,
        @PathVariable("timeBucketStart") String timeBucketStart
    ) {
        CommonResponse<MarketDataResponse> response = CommonResponse.<MarketDataResponse>builder()
                .message(ApiBash.FETCH_BY_TIME_BUCKET_SUCCESS)
                .status(HttpStatus.OK.value())
                .data(marketdataService.fetchMarketDataByTimeBucketStart(marketDataType, timeBucketStart))
                .build();
        return ResponseEntity.ok(response);
    }

}
