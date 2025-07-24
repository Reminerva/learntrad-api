package com.learntrad.microservices.shared.client.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;
import com.learntrad.microservices.shared.entity.OhlcBaseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TwelveDataClient {

    @Value("${marketdata.twelvedata.base-url}")
    private String baseUrl;

    @Value("${marketdata.twelvedata.apikey}")
    private String apiKey;

    private RestClient restClient = RestClient.create();

    public BigDecimal fetchRealtimePrice(EMarketDataType symbol) {
        if (symbol.getExchangeName() == null || symbol.getExchangeName().isEmpty() || symbol.getExchangeName().equals("")) throw new RuntimeException("Symbol's exchange name is null or empty");
        String uri = UriComponentsBuilder.newInstance()
            .scheme("https")
            .host("api.twelvedata.com")
            .path("/price")
            .queryParam("symbol", symbol.getExchangeName())
            .queryParam("apikey", apiKey)
            .build()
            .toUriString();

        TwelveDataPriceResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(TwelveDataPriceResponse.class);

        log.info("Response: {}", response);

        return new BigDecimal(response.price());
    }

    public <T extends OhlcBaseEntity> List<T> fetchHistorical1MinData(
            Instant from,
            Instant to,
            EMarketDataType symbol,
            Class<T> entityClassName
    ) {
        if (to.isAfter(Instant.now())) {
            to = Instant.now();
        };
        String uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.twelvedata.com")
                .path("/time_series")
                .queryParam("symbol", symbol.getExchangeName())
                .queryParam("interval", "1min")
                .queryParam("apikey", apiKey)
                .queryParam("start_date", from.toString().replace("T", " ").replace("Z", "")) // format: 2024-06-25T00:00:00Z
                .queryParam("end_date", to.toString().replace("T", " ").replace("Z", ""))
                .queryParam("timezone", "Africa/Accra")
                .queryParam("format", "JSON")
                .build()
                .toUriString();

        log.info("Hitting URL: {}", uri);

        TimeSeriesResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(TimeSeriesResponse.class);

        if (response == null || response.values() == null) return List.of();

        return response.values().stream()
            .map(v -> {
                try {
                    T entity = entityClassName.getDeclaredConstructor().newInstance();
                    entity.setTimeBucketStart(Instant.parse(v.datetime().replace(" ", "T") + "Z"));
                    entity.setOpen(new BigDecimal(v.open()));
                    entity.setHigh(new BigDecimal(v.high()));
                    entity.setLow(new BigDecimal(v.low()));
                    entity.setClosed(new BigDecimal(v.close()));
                    entity.setVolume(v.volume() == null ? 0 : Long.parseLong(v.volume()));
                    return entity;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate entity: " + entityClassName.getName(), e);
                }
            })
            .toList();
    }

    private record TwelveDataPriceResponse(String price) {}

    private record TimeSeriesResponse(
            String status,
            List<TimeSeriesValue> values
    ) {}

    private record TimeSeriesValue(
            String datetime,
            String open,
            String high,
            String low,
            String close,
            String volume
    ) {}

}
