package com.learntrad.microservices.marketrealtime.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.learntrad.microservices.marketrealtime.event.RealtimeDataEvent;
import com.learntrad.microservices.marketrealtime.service.intrface.RealtimeDataService;
import com.learntrad.microservices.shared.client.external.TwelveDataClient;
import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealtimeDataServiceImpl implements RealtimeDataService {

    private final TwelveDataClient twelveDataClient;
    private final KafkaTemplate<String, RealtimeDataEvent> kafkaRealtimeDataEventTemplate;

    private BigDecimal getLatestPrice(EMarketDataType marketDataType) {
        return twelveDataClient.fetchRealtimePrice(marketDataType);
    }

    @Scheduled(fixedRate = 20000)
    @Override
    public void fetchAndSaveLatestTick() {
        Instant now = Instant.now();

        Instant bucketStartNow = now.truncatedTo(ChronoUnit.MINUTES); // awal menit saat ini

        log.info("Start - Get latest price");
        BigDecimal price = getLatestPrice(EMarketDataType.XAUUSD);
        // BigDecimal price = BigDecimal.valueOf(3329.46);
        // BigDecimal price = BigDecimal.valueOf(4000);

        log.info("End - Get latest price - time: {}, bucketStart: {}, price: {}", now, bucketStartNow, price);

        log.info("Start - Sending kafka message. topic: realtime-data, message: {}", new RealtimeDataEvent(bucketStartNow, price, EMarketDataType.XAUUSD));
        kafkaRealtimeDataEventTemplate.send("realtime-data", new RealtimeDataEvent(bucketStartNow, price, EMarketDataType.XAUUSD));
        log.info("End - Sending kafka message. topic: realtime-data, message: {}", new RealtimeDataEvent(bucketStartNow, price, EMarketDataType.XAUUSD));

    }
}
