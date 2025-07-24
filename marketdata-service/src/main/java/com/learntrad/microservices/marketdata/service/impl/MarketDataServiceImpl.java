package com.learntrad.microservices.marketdata.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;
import com.learntrad.microservices.shared.constant.enumerated.ETimeFrame;
import com.learntrad.microservices.marketdata.entity.XauusdEntity;
import com.learntrad.microservices.marketdata.model.request.search.SearchMarketDataRequest;
import com.learntrad.microservices.marketdata.model.response.DataOfMarketData;
import com.learntrad.microservices.marketdata.model.response.MarketDataResponse;
import com.learntrad.microservices.marketdata.repository.XauusdRepository;
import com.learntrad.microservices.marketdata.service.intrface.MarketDataService;
import com.learntrad.microservices.marketrealtime.event.RealtimeDataEvent;
import com.learntrad.microservices.shared.constant.ConstantBash;
import com.learntrad.microservices.shared.constant.DbBash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketDataServiceImpl implements MarketDataService{

    private final XauusdRepository xauusdRepository;

    @Override
    @KafkaListener(topics = "${kafka.topic.xauusd}")
    public void saveLatestTick(RealtimeDataEvent realtimeDataEvent) {

        log.info("Received message from realtime-data topic: {}", realtimeDataEvent);

        switch (realtimeDataEvent.getMarketDataType()) {
            case EMarketDataType.XAUUSD:
                log.info("Start - Inserting new data to: {}, bucketStart: {}, price: {}", realtimeDataEvent.getMarketDataType(), realtimeDataEvent.getTime(), realtimeDataEvent.getPrice());
                xauusdRepository.upsertOhlcTick(realtimeDataEvent.getTime(), realtimeDataEvent.getPrice());
                log.info("End - Inserting new data to: {}, bucketStart: {}, price: {}", realtimeDataEvent.getMarketDataType(), realtimeDataEvent.getTime(), realtimeDataEvent.getPrice());
            default:
                break;
        }
    }

    @Override
    public MarketDataResponse fetchMarketData(String marketDataType, SearchMarketDataRequest searchMarketDataRequest) {
        try {
            EMarketDataType marketDataTypeEnum = EMarketDataType.findByDescription(marketDataType);

            if (searchMarketDataRequest.getTimeBucketStartMax() != null && searchMarketDataRequest.getTimeBucketStartMin() != null) {
                if (searchMarketDataRequest.getTimeBucketStartMin().isAfter(searchMarketDataRequest.getTimeBucketStartMax())) {
                    throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
                }
            }

            switch (marketDataTypeEnum) {
                case XAUUSD:
                    log.info("Start - Fetching data for: {}, bucketStartMin: {}, bucketStartMax: {}, timeFrame: {}", 
                        marketDataTypeEnum, 
                        searchMarketDataRequest.getTimeBucketStartMin(),
                        searchMarketDataRequest.getTimeBucketStartMax(),
                        ETimeFrame.findByDescription(searchMarketDataRequest.getTimeFrame())
                    );

                    List<Object[]> dataFetched;
                    if (searchMarketDataRequest.getDirection().equalsIgnoreCase("asc")) {
                        dataFetched = xauusdRepository.fetchAggregatedDataAsc(
                            ETimeFrame.findByDescription(searchMarketDataRequest.getTimeFrame()).toPostgresInterval(),
                            searchMarketDataRequest.getTimeBucketStartMin(),
                            searchMarketDataRequest.getTimeBucketStartMax()
                        );
                    } else if (searchMarketDataRequest.getDirection().equalsIgnoreCase("desc")) {
                        dataFetched = xauusdRepository.fetchAggregatedDataDesc(
                            ETimeFrame.findByDescription(searchMarketDataRequest.getTimeFrame()).toPostgresInterval(),
                            searchMarketDataRequest.getTimeBucketStartMin(),
                            searchMarketDataRequest.getTimeBucketStartMax()
                        );
                    } else {
                        throw new UnsupportedOperationException(ConstantBash.INVALID_DIRECTION);
                    }
                    List<DataOfMarketData> entities = dataFetched.stream().map(DataOfMarketData::toMarketData).toList();
                    log.info("End - Fetching data for: {}, bucketStartMin: {}, bucketStartMax: {}, timeFrame: {}",
                        marketDataTypeEnum,
                        searchMarketDataRequest.getTimeBucketStartMin(),
                        searchMarketDataRequest.getTimeBucketStartMax(),
                        ETimeFrame.findByDescription(searchMarketDataRequest.getTimeFrame())
                    );
                    return toMarketDataResponse(
                        entities, 
                        searchMarketDataRequest,
                        marketDataTypeEnum
                    );
                default:
                    throw new UnsupportedOperationException(ConstantBash.INVALID_ENUM + marketDataType);
            }
        } catch (Exception e) {
            log.error("Error while fetching data for: {}, bucketStartMin: {}, bucketStartMax: {}", marketDataType, searchMarketDataRequest.getTimeBucketStartMin(), searchMarketDataRequest.getTimeBucketStartMax(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public MarketDataResponse fetchMarketDataTick(String marketDataType) {
        try {
            EMarketDataType marketDataTypeEnum = EMarketDataType.findByDescription(marketDataType);
            log.info("Start - Fetching latest tick for: {}", marketDataTypeEnum);
            Optional<XauusdEntity> latestTick = xauusdRepository.findTopByOrderByTimeBucketStartDesc();
            if (latestTick.isEmpty()) {
                throw new RuntimeException(DbBash.LATEST_TICK_NOT_FOUND);
            }
            log.info("End - Fetching latest tick for: {}", marketDataTypeEnum);
            SearchMarketDataRequest request = SearchMarketDataRequest.builder()
                    .timeBucketStartMin(latestTick.get().getTimeBucketStart())
                    .timeBucketStartMax(latestTick.get().getTimeBucketStart())
                    .build();
            return switch (marketDataTypeEnum) {
                case XAUUSD ->  toMarketDataResponse(
                    List.of(latestTick.get()), 
                    request, 
                    marketDataTypeEnum
                    );
                default -> throw new UnsupportedOperationException(ConstantBash.INVALID_ENUM + marketDataType);
            };
        } catch (Exception e) {
            log.error("Error while fetching latest tick for: {}", marketDataType, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public MarketDataResponse fetchMarketDataByTimeBucketStart(String marketDataType, String timeBucketStart) {
        try {

            log.info("Start - Fetching data for: {}, timeBucketStart: {}", marketDataType, timeBucketStart);

            Instant instantTimeBucketStart = Instant.parse(timeBucketStart).truncatedTo(ChronoUnit.MINUTES);
            switch (EMarketDataType.findByDescription(marketDataType)) {
                case XAUUSD:
                    Optional<XauusdEntity> latestTick = xauusdRepository.findByTimeBucketStart(instantTimeBucketStart);
                    if (latestTick.isEmpty()) {
                        throw new RuntimeException(DbBash.LATEST_TICK_NOT_FOUND);
                    }
                    SearchMarketDataRequest request = SearchMarketDataRequest.builder()
                        .timeBucketStartMin(latestTick.get().getTimeBucketStart())
                        .timeBucketStartMax(latestTick.get().getTimeBucketStart())
                        .build();
                    log.info("End - Fetching data for: {}, timeBucketStart: {}", marketDataType, timeBucketStart);
                    return toMarketDataResponse(
                        List.of(latestTick.get()), 
                        request, 
                        EMarketDataType.findByDescription(marketDataType)
                    );
                default:
                    throw new UnsupportedOperationException(ConstantBash.INVALID_ENUM + marketDataType);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MarketDataResponse toMarketDataResponse(List<?> data, SearchMarketDataRequest request, EMarketDataType marketDataType) {
        return MarketDataResponse.builder()
                .dataCount((long) data.size())
                .timeBucketStartMin(request.getTimeBucketStartMin())
                .timeBucketEndMax(request.getTimeBucketStartMax())
                .marketDataType(marketDataType)
                .marketData(data)
                .build();
    }

}
