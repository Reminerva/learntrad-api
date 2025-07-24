package com.learntrad.microservices.marketdata.bootstrap;

import com.learntrad.microservices.marketdata.entity.XauusdEntity;
import com.learntrad.microservices.shared.client.external.TwelveDataClient;
import com.learntrad.microservices.marketdata.repository.XauusdRepository;
import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class XauusdMarketDataBootstrapLoader {

    private final XauusdRepository xauusdRepository;
    private final TwelveDataClient twelveDataClient;

    @PostConstruct
    public void fetchXauusdMissingDataOnStartup() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        Instant latestTime = xauusdRepository.findTopByOrderByTimeBucketStartDesc()
                .map(XauusdEntity::getTimeBucketStart)
                .orElse(now); // fallback jika DB kosong

        long minutesGap = (now.getEpochSecond() - latestTime.getEpochSecond()) / 60;
        if (minutesGap < 1) {
            log.info("Database is already up-to-date.");
            return;
        }
        if (minutesGap >= minutesGap * 60 * 24 * 14) {
            log.info("Fetching too many minute(s) from {} to {}. Due to the limit of free Twelve Data API, 14 days is the maximum allowed.", latestTime, now);
            minutesGap = minutesGap * 60 * 24 * 14;
        }

        log.info("Fetching {} missing minute(s) from {} to {}", minutesGap, latestTime, now);

        long batchSize = 5000;
        int batchCount = (int) Math.ceil((double) minutesGap / batchSize);
        Integer totalDataFetched = 0;
        for (int i = 0; i < batchCount; i++) {

            Instant start = latestTime.plus(i * batchSize + 1 + i, ChronoUnit.MINUTES);
            Instant end = start.plus(batchSize, ChronoUnit.MINUTES);

            List<XauusdEntity> historicalData = twelveDataClient.fetchHistorical1MinData(start, end, EMarketDataType.XAUUSD, XauusdEntity.class);
            log.info("Fetched {} rows from historical fetch from {} to {}.", historicalData.size(), start, end);
            xauusdRepository.saveAll(historicalData);
            log.info("f {} rows from historical fetch.", historicalData.size());
            totalDataFetched += historicalData.size();
        }
        log.info("Minutes gap: {}. Total data fetched: {}", minutesGap, totalDataFetched );

    }
}
