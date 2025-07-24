package com.learntrad.microservices.trade.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learntrad.microservices.shared.constant.ConstantBash;
import com.learntrad.microservices.shared.constant.DbBash;
import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;
import com.learntrad.microservices.shared.constant.enumerated.ETradeStatus;
import com.learntrad.microservices.shared.constant.enumerated.ETradeType;
import com.learntrad.microservices.shared.jwt.JwtClaim;
import com.learntrad.microservices.shared.jwt.JwtUtil;
import com.learntrad.microservices.topup.event.BalanceAdjustedEvent;
import com.learntrad.microservices.trade.client.CustomerClient;
import com.learntrad.microservices.trade.client.MarketDataClient;
import com.learntrad.microservices.trade.entity.Trade;
import com.learntrad.microservices.trade.event.TradeEditedEvent;
import com.learntrad.microservices.trade.event.TradePlacedEvent;
import com.learntrad.microservices.trade.event.TradeStatusUpdatedEvent;
import com.learntrad.microservices.trade.model.request.TradeRequest;
import com.learntrad.microservices.trade.model.request.search.SearchTradeRequest;
import com.learntrad.microservices.trade.model.response.TradeResponse;
import com.learntrad.microservices.trade.repository.TradeRepository;
import com.learntrad.microservices.trade.service.intrface.TradeService;
import com.learntrad.microservices.trade.specification.TradeSpecification;
import com.learntrad.microservices.tradeprocessor.event.TradeProcessedEvent;

import groovy.lang.Tuple2;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeServiceImpl implements TradeService {

    private final MarketDataClient marketDataClient;
    private final CustomerClient customerClient;
    private final TradeRepository tradeRepository;
    private final KafkaTemplate<String, TradePlacedEvent> kafkaTemplatePlaced;
    private final KafkaTemplate<String, TradeEditedEvent> kafkaTemplateEdited;
    private final KafkaTemplate<String, TradeStatusUpdatedEvent> kafkaTemplateStatusUpdated;
    private final KafkaTemplate<String, TradeStatusUpdatedEvent> kafkaTemplateCanceled;
    private final KafkaTemplate<String, BalanceAdjustedEvent> kafkaTemplateBalanceAdjusted;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TradeResponse createMine(String authHeader, TradeRequest tradeRequest) {
        try {

            JwtClaim claims = JwtUtil.getClaims(authHeader);

            Tuple2<String, BigDecimal> customerInfo = getCustomerInfo(authHeader);
            String customerFullname = customerInfo.getV1();
            BigDecimal customerBalance = customerInfo.getV2();
            
            BigDecimal priceNow = getMarketDataPrice(authHeader, tradeRequest);

            if (ETradeType.findByDescription(tradeRequest.getTradeType()).equals(ETradeType.MARKET_EXECUTION_BUY) || 
                ETradeType.findByDescription(tradeRequest.getTradeType()).equals(ETradeType.MARKET_EXECUTION_SELL)
            ) {
                tradeRequest.setPriceAt(priceNow);
            }

            verifyRequest(tradeRequest, priceNow, customerBalance, claims.getUserId());

            log.info("Start - Creating trade");
            Trade trade = Trade.builder()
                .userId(claims.getUserId())
                .lot(tradeRequest.getLot())
                .priceAt(tradeRequest.getPriceAt())
                .stopLossAt(tradeRequest.getStopLossAt())
                .takeProfitAt(tradeRequest.getTakeProfitAt())
                .tradeAt(tradeRequest.getTradeAt())
                .marketDataType(EMarketDataType.findByDescription(tradeRequest.getMarketDataType()))
                .tradeType(ETradeType.findByDescription(tradeRequest.getTradeType()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .closedAt(null)
                .expiredAt(tradeRequest.getExpiredAt())
                .build();

            if (trade.getTradeType().equals(ETradeType.MARKET_EXECUTION_BUY) || trade.getTradeType().equals(ETradeType.MARKET_EXECUTION_SELL)) {
                trade.setTradeStatus(ETradeStatus.RUNNING);
            } else {
                trade.setTradeStatus(ETradeStatus.PENDING);
            }

            log.info("Start - Saving trade: {}", trade);
            TradeResponse tradeResponse = tradeRepository.save(trade).toTradeResponse();
            log.info("End - Saving trade: {}", trade);
            
            TradePlacedEvent tradePlacedEvent = TradePlacedEvent.builder()
                .tradeId(tradeResponse.getId())
                .userId(tradeResponse.getUserId())
                .customerFullname(customerFullname)
                .marketDataType(tradeResponse.getMarketDataType())
                .username(claims.getUsername())
                .email(claims.getEmail())
                .tradeAt(tradeResponse.getTradeAt())
                .priceAt(tradeResponse.getPriceAt())
                .stopLossAt(tradeResponse.getStopLossAt())
                .takeProfitAt(tradeResponse.getTakeProfitAt())
                .tradeType(tradeResponse.getTradeType())
                .lot(tradeResponse.getLot())
                .expiredAt(tradeResponse.getExpiredAt())
                .build();

            log.info("Start - Sending Trade Placed Event {} to Kafka trade-placed", trade.getId());
            kafkaTemplatePlaced.send("trade-placed", tradePlacedEvent);
            log.info("End - Sending Trade Placed Event {} to Kafka trade-placed", trade.getId());
            log.info("End - Creating trade: {}", tradeResponse);
            return tradeResponse;
        } catch (Exception e) {
            if (e.getMessage().contains("message\":\"RUNTIME_ERROR! Customer not found!")) {
                throw new RuntimeException(DbBash.CUSTOMER_NOT_FOUND + " " + ConstantBash.PLEASE_FILL_CUSTOMER_PROFILE);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Trade getTradeById(String id) {
        try {
            log.info("Start - Getting trade by id: {}", id);
            Optional<Trade> tradeOptional = tradeRepository.findById(id);
            if (tradeOptional.isEmpty()) {
                log.error("Error - Trade not found: {}", id);
                throw new RuntimeException(DbBash.TRADE_NOT_FOUND);
            }
            log.info("End - Getting trade by id: {}", id);
            return tradeOptional.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TradeResponse getById(String id) {
        try {
            return getTradeById(id).toTradeResponse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<TradeResponse> getAllTrades(SearchTradeRequest searchTradeRequest) {
        try {
            verifyingSearchRequest(searchTradeRequest);

            log.info("Start - Getting all trades: {}", searchTradeRequest);
            Sort sort = Sort.by(Sort.Direction.fromString(searchTradeRequest.getDirection()), searchTradeRequest.getSortBy());
            Pageable pageable = PageRequest.of(searchTradeRequest.getPage() - 1, searchTradeRequest.getSize(), sort);
            Specification<Trade> specification = TradeSpecification.getSpecification(searchTradeRequest);

            Page<Trade> trades = tradeRepository.findAll(specification, pageable);
            log.info("End - Getting all trades");
            return trades.map(Trade::toTradeResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TradeResponse updateMine(String authHeader, String id, TradeRequest tradeRequest) {
        try {
            Trade trade = getTradeById(id);
            if (!trade.getTradeStatus().equals(ETradeStatus.PENDING) && !trade.getTradeStatus().equals(ETradeStatus.RUNNING)) {
                throw new RuntimeException(DbBash.TRADE_IS_NOT_PENDING_OR_RUNNING);
            }

            JwtClaim claims = JwtUtil.getClaims(authHeader);

            Tuple2<String, BigDecimal> customerInfo = getCustomerInfo(authHeader);
            String customerFullname = customerInfo.getV1();
            BigDecimal customerBalance = customerInfo.getV2();

            if (!trade.getUserId().equals(claims.getUserId())) {
                throw new RuntimeException(ConstantBash.NOT_ALLOWED_TO_UPDATE_TRADE);
            }

            tradeRequest.setPriceAt(trade.getPriceAt());
            tradeRequest.setTradeAt(trade.getTradeAt());
            tradeRequest.setLot(trade.getLot());
            tradeRequest.setTradeType(trade.getTradeType().getDescription());

            verifyRequest(tradeRequest, null, customerBalance, claims.getUserId());

            BigDecimal oldStopLossAt = trade.getStopLossAt();
            BigDecimal oldTakeProfitAt = trade.getTakeProfitAt();
            LocalDateTime oldExpiredAt = trade.getExpiredAt();

            log.info("Start - Updating trade: {}", tradeRequest);
            trade.setStopLossAt(tradeRequest.getStopLossAt());
            trade.setTakeProfitAt(tradeRequest.getTakeProfitAt());
            trade.setExpiredAt(tradeRequest.getExpiredAt());
            trade.setUpdatedAt(LocalDateTime.now());
    
            log.info("Start - Saving trade: {}", trade);
            TradeResponse tradeResponse = tradeRepository.save(trade).toTradeResponse();
            log.info("End - Saving trade: {}", trade);

            TradeEditedEvent tradeEditedEvent = TradeEditedEvent.builder()
                .tradeId(tradeResponse.getId())
                .userId(tradeResponse.getUserId())
                .customerFullname(customerFullname)
                .marketDataType(tradeResponse.getMarketDataType())
                .lot(tradeResponse.getLot())
                .tradeAt(tradeResponse.getTradeAt())
                .username(claims.getUsername())
                .email(claims.getEmail())
                .oldStopLossAt(oldStopLossAt)
                .oldTakeProfitAt(oldTakeProfitAt)
                .newStopLossAt(tradeResponse.getStopLossAt())
                .newTakeProfitAt(tradeResponse.getTakeProfitAt())
                .tradeType(tradeResponse.getTradeType())
                .oldExpiredAt(oldExpiredAt)
                .newExpiredAt(tradeResponse.getExpiredAt())
                .build();

            log.info("Start - Sending Trade Edited Event {} to Kafka trade-edited", trade.getId());
            kafkaTemplateEdited.send("trade-edited", tradeEditedEvent);
            log.info("End - Sending Trade Edited Event {} to Kafka trade-edited", trade.getId());
            log.info("End - Updating trade: {}", tradeRequest);

            return tradeResponse;
        } catch (Exception e) {
            if (e.getMessage().contains("message\":\"RUNTIME_ERROR! Customer not found!")) {
                throw new RuntimeException("Customer not found. Please fill the customer's profile first.");
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public TradeResponse getMine(String authHeader, String id) {
        try {
            JwtClaim claims = JwtUtil.getClaims(authHeader);
            Trade trade = getTradeByIdAndUserId(id, claims);
            return trade.toTradeResponse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<TradeResponse> getAllMine(String authHeader, SearchTradeRequest searchTradeRequest) {
        try {
            JwtClaim claims = JwtUtil.getClaims(authHeader);
            searchTradeRequest.setUserId(claims.getUserId());

            return getAllTrades(searchTradeRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyRequest(TradeRequest tradeRequest, BigDecimal priceNow, BigDecimal customerBalance, String userId) {
        try {
            log.info("Start - Verifying request {} for user {}", tradeRequest, userId);
            if (tradeRequest.getExpiredAt() != null) {
                if (tradeRequest.getExpiredAt().isBefore(LocalDateTime.now())) {
                    throw new RuntimeException(ConstantBash.INVALID_EXPIRED_AT);
                }
            }

            ETradeType tradeType = ETradeType.findByDescription(tradeRequest.getTradeType());
            BigDecimal lossPotential = tradeRequest.getStopLossAt()
                .subtract(tradeRequest.getPriceAt())
                .multiply(BigDecimal.valueOf(EMarketDataType.findByDescription(tradeRequest.getMarketDataType()).getMultiplier()))
                .multiply(BigDecimal.valueOf(tradeRequest.getLot()))
                .abs();
            List<Trade> tradesRunning = tradeRepository.findByUserIdAndTradeStatusAndMarketDataType(userId, ETradeStatus.RUNNING, EMarketDataType.findByDescription(tradeRequest.getMarketDataType()));
            List<Trade> tradesPending = tradeRepository.findByUserIdAndTradeStatusAndMarketDataType(userId, ETradeStatus.PENDING, EMarketDataType.findByDescription(tradeRequest.getMarketDataType()));

            BigDecimal tradesRunningLossPotentialAccumulation = tradesRunning.stream().map(
                trade -> trade.getStopLossAt()
                    .subtract(trade.getPriceAt())
                    .multiply(BigDecimal.valueOf(trade.getMarketDataType().getMultiplier()))
                    .multiply(BigDecimal.valueOf(trade.getLot()))
                    .abs()
            ).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal tradesPendingLossPotentialAccumulation = tradesPending.stream().map(
                trade -> trade.getStopLossAt()
                    .subtract(trade.getPriceAt())
                    .multiply(BigDecimal.valueOf(trade.getMarketDataType().getMultiplier()))
                    .multiply(BigDecimal.valueOf(trade.getLot()))
                    .abs()
            ).reduce(BigDecimal.ZERO, BigDecimal::add);

            if (tradeType.getDescription().contains("BUY")) {
                if (tradeRequest.getTakeProfitAt().compareTo(tradeRequest.getPriceAt()) <= 0 || 
                    tradeRequest.getStopLossAt().compareTo(tradeRequest.getPriceAt()) >= 0
                ) {
                    throw new RuntimeException(ConstantBash.getBuyInvalidMessage(tradeRequest.getPriceAt(), tradeRequest.getStopLossAt(), tradeRequest.getTakeProfitAt()));
                }
            } else if (tradeType.getDescription().contains("SELL")) {
                if (tradeRequest.getTakeProfitAt().compareTo(tradeRequest.getPriceAt()) >= 0 || 
                    tradeRequest.getStopLossAt().compareTo(tradeRequest.getPriceAt()) <= 0
                ) {
                    throw new RuntimeException(ConstantBash.getSellInvalidMessage(tradeRequest.getPriceAt(), tradeRequest.getStopLossAt(), tradeRequest.getTakeProfitAt()));
                }
            }

            if (customerBalance.compareTo(lossPotential.add(tradesRunningLossPotentialAccumulation).add(tradesPendingLossPotentialAccumulation)) < 0) {
                throw new RuntimeException(ConstantBash.getBalanceInvalidMessage(customerBalance, lossPotential.add(tradesRunningLossPotentialAccumulation).add(tradesPendingLossPotentialAccumulation)));
            }

            if (priceNow == null) {
                log.info("End - Verifying request {} for user {}", tradeRequest, userId);
                return;
            }

            switch (tradeType) {
                case BUY_LIMIT:
                    if (priceNow.compareTo(tradeRequest.getPriceAt()) < 0) {
                        throw new RuntimeException(ConstantBash.getBuyLimitPriceAtInvalidMessage(tradeRequest.getPriceAt(), priceNow));
                    }
                    break;
                case SELL_LIMIT:
                    if (priceNow.compareTo(tradeRequest.getPriceAt()) > 0) {
                        throw new RuntimeException(ConstantBash.getSellLimitPriceAtInvalidMessage(tradeRequest.getPriceAt(), priceNow));
                    }
                    break;
                case BUY_STOP:
                    if (priceNow.compareTo(tradeRequest.getPriceAt()) > 0) {
                        throw new RuntimeException(ConstantBash.getBuyStopPriceAtInvalidMessage(tradeRequest.getPriceAt(), priceNow));
                    }
                    break;
                case SELL_STOP:
                    if (priceNow.compareTo(tradeRequest.getPriceAt()) < 0) {
                        throw new RuntimeException(ConstantBash.getSellStopPriceAtInvalidMessage(tradeRequest.getPriceAt(), priceNow));
                    }
                    break;
                default:
                    break;
            }
            log.info("End - Verifying request {} for user {}", tradeRequest, userId);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Tuple2<String, BigDecimal> getCustomerInfo(String authHeader) {
        try {
            log.info("Start - Getting customer info");
            ObjectMapper objectMapper = new ObjectMapper();
            String customerResponse = customerClient.getMe(authHeader);
            JsonNode customerJsonNode = objectMapper.readTree(customerResponse);
            String customerFullname = customerJsonNode.get("data").get("fullname").asText();
            BigDecimal customerBalance = BigDecimal.valueOf(customerJsonNode.get("data").get("balance").asDouble());
            log.info("End - Getting customer info");
            return Tuple2.tuple(customerFullname, customerBalance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal getMarketDataPrice(String authHeader, TradeRequest tradeRequest) {
        try {

            if (!(tradeRequest.getTradeAt().isBefore(Instant.now().plus(Duration.ofMinutes(1)))) || 
                (tradeRequest.getTradeAt().isAfter(Instant.now()))) 
            {
                log.error(ConstantBash.getTradeAtInvalidMessage(tradeRequest.getTradeAt(), Instant.now()));
                throw new RuntimeException(ConstantBash.getTradeAtInvalidMessage(tradeRequest.getTradeAt(), Instant.now()));
            }

            log.info("Start - Getting market data info");
            ObjectMapper objectMapper = new ObjectMapper();
            String marketDataResponse = marketDataClient.fetchMarketDataByTimeBucketStart(
                authHeader,
                EMarketDataType.findByDescription(tradeRequest.getMarketDataType()).getDescription(), 
                tradeRequest.getTradeAt().toString()
            );
            JsonNode jsonNode = objectMapper.readTree(marketDataResponse);
            BigDecimal priceNow = BigDecimal.valueOf(jsonNode.get("data").get("marketData").get(0).get("closed").asDouble());
            log.info("End - Getting market data info");

            return priceNow;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @KafkaListener(topics = "trade-processed")
    public void updateTradeStatus(TradeProcessedEvent tradeProcessedEvent) {
        log.info("Receiving message from trade-processed topic: {}", tradeProcessedEvent);
        Trade trade = getTradeById(tradeProcessedEvent.getId());

        log.info("Start - Updating trade: {}", trade);
        trade.setPriceAt(tradeProcessedEvent.getPriceAt());
        trade.setStopLossAt(tradeProcessedEvent.getStopLossAt());
        trade.setTakeProfitAt(tradeProcessedEvent.getTakeProfitAt());
        trade.setMarketDataType(tradeProcessedEvent.getMarketDataType());
        trade.setTradeStatus(tradeProcessedEvent.getTradeStatus());
        trade.setTradeType(tradeProcessedEvent.getTradeType());
        trade.setClosedAt(tradeProcessedEvent.getClosedAt());
        log.info("End - Updating trade: {}", trade);

        log.info("Start - Saving updated trade to t_trade: {}", trade);
        TradeResponse tradeResponse = tradeRepository.save(trade).toTradeResponse();
        log.info("End - Saving updated trade to t_trade: {}", trade);

        TradeStatusUpdatedEvent tradeStatusUpdatedEvent = TradeStatusUpdatedEvent.builder()
            .tradeId(tradeResponse.getId())
            .userId(tradeResponse.getUserId())
            .customerFullname(tradeProcessedEvent.getCustomerFullname())
            .tradeAt(tradeResponse.getTradeAt())
            .marketDataType(tradeResponse.getMarketDataType())
            .username(tradeProcessedEvent.getUsername())
            .email(tradeProcessedEvent.getEmail())
            .lot(tradeResponse.getLot())
            .priceAt(tradeResponse.getPriceAt())
            .stopLossAt(tradeResponse.getStopLossAt())
            .takeProfitAt(tradeResponse.getTakeProfitAt())
            .tradeType(tradeResponse.getTradeType())
            .tradeStatus(tradeResponse.getTradeStatus())
            .closedAt(tradeResponse.getClosedAt())
            .build();

        BigDecimal profitLoss = profitLossCounter(tradeProcessedEvent.getPriceAt(), tradeProcessedEvent.getClosedAt());
        if (profitLoss != null) {
            BalanceAdjustedEvent balanceAdjustedEvent = BalanceAdjustedEvent.builder()
                .userId(tradeStatusUpdatedEvent.getUserId())
                .amount(profitLoss)
                .build();
            log.info("Start - Sending balance-adjusted event: {}", balanceAdjustedEvent);
            kafkaTemplateBalanceAdjusted.send("balance-adjusted", balanceAdjustedEvent);
            log.info("End - Sent balance-adjusted event: {}", balanceAdjustedEvent);
        }

        log.info("Start - Sending trade-status-updated event: {}", tradeStatusUpdatedEvent);
        kafkaTemplateStatusUpdated.send("trade-status-updated", tradeStatusUpdatedEvent);
        log.info("End - Sent trade-status-updated event: {}", tradeStatusUpdatedEvent);

    }

    @Override
    public TradeResponse cancelTrade(String authHeader, String id) {
        try {
            JwtClaim claims = JwtUtil.getClaims(authHeader);
            Tuple2<String, BigDecimal> customerInfo = getCustomerInfo(authHeader);

            log.info("Start - Canceling trade by id {} for user {}", id, claims.getUserId());
            Trade trade = getTradeByIdAndUserId(id, claims);
            if (!trade.getTradeStatus().equals(ETradeStatus.PENDING) && !trade.getTradeStatus().equals(ETradeStatus.RUNNING)) {
                throw new RuntimeException(DbBash.TRADE_IS_NOT_PENDING_OR_RUNNING);
            }
            trade.setTradeStatus(ETradeStatus.CANCELED);
            TradeResponse tradeResponse = tradeRepository.save(trade).toTradeResponse();

            TradeStatusUpdatedEvent tradeStatusUpdatedEvent = TradeStatusUpdatedEvent.builder()
                .tradeId(tradeResponse.getId())
                .userId(tradeResponse.getUserId())
                .customerFullname(customerInfo.getV1())
                .tradeAt(tradeResponse.getTradeAt())
                .marketDataType(tradeResponse.getMarketDataType())
                .username(claims.getUsername())
                .email(claims.getEmail())
                .lot(tradeResponse.getLot())
                .priceAt(tradeResponse.getPriceAt())
                .stopLossAt(tradeResponse.getStopLossAt())
                .takeProfitAt(tradeResponse.getTakeProfitAt())
                .tradeType(tradeResponse.getTradeType())
                .tradeStatus(tradeResponse.getTradeStatus())
                .closedAt(tradeResponse.getClosedAt())
                .build();

            log.info("Start - Sending Trade Edited Event {} to Kafka trade-canceled", trade.getId());
            kafkaTemplateCanceled.send("trade-canceled", tradeStatusUpdatedEvent);
            log.info("End - Sending Trade Edited Event {} to Kafka trade-canceled", trade.getId());
            log.info("End - Canceling trade by id {} for user {}", id, claims.getUserId());

            return tradeResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal profitLossCounter(BigDecimal priceNow, BigDecimal closedAt) {
        log.info("Start - Calculating profit loss: {} - {}", priceNow, closedAt);
        BigDecimal profitLoss = BigDecimal.ZERO;
        if (closedAt == null) {
            return null;
        }
        if (closedAt.compareTo(priceNow) > 0) {
            profitLoss = closedAt.subtract(priceNow);
        } else if (closedAt.compareTo(priceNow) < 0) {
            profitLoss = priceNow.subtract(closedAt);
        }
        log.info("End - Calculating profit loss: {} - {}", priceNow, closedAt);
        return profitLoss;
    }

    private void verifyingSearchRequest(SearchTradeRequest searchTradeRequest) {
        log.info("Start - Verifying search request: {}", searchTradeRequest);
        if (searchTradeRequest.getPage() <= 0) {
            searchTradeRequest.setPage(1);
        }
        if (searchTradeRequest.getSize() <= 0) {
            searchTradeRequest.setSize(10);
        }
        if (searchTradeRequest.getCreatedAtMin() != null && searchTradeRequest.getCreatedAtMax() != null) {
            if ((searchTradeRequest.getCreatedAtMin()).isAfter((searchTradeRequest.getCreatedAtMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchTradeRequest.getUpdatedAtMin() != null && searchTradeRequest.getUpdatedAtMax() != null) {
            if ((searchTradeRequest.getUpdatedAtMin()).isAfter((searchTradeRequest.getUpdatedAtMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchTradeRequest.getPriceAtMin() != null && searchTradeRequest.getPriceAtMax() != null) {
            if ((searchTradeRequest.getPriceAtMin()) > ((searchTradeRequest.getPriceAtMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchTradeRequest.getStopLossAtMin() != null && searchTradeRequest.getStopLossAtMax() != null) {
            if ((searchTradeRequest.getStopLossAtMin()) > ((searchTradeRequest.getStopLossAtMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchTradeRequest.getTakeProfitAtMin() != null && searchTradeRequest.getTakeProfitAtMax() != null) {
            if ((searchTradeRequest.getTakeProfitAtMin()) > ((searchTradeRequest.getTakeProfitAtMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchTradeRequest.getTradeAtMin() != null && searchTradeRequest.getTradeAtMax() != null) {
            if ((searchTradeRequest.getTradeAtMin()).isAfter((searchTradeRequest.getTradeAtMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchTradeRequest.getLotMin() != null && searchTradeRequest.getLotMax() != null) {
            if ((searchTradeRequest.getLotMin()) > ((searchTradeRequest.getLotMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchTradeRequest.getClosedAtMin() != null && searchTradeRequest.getClosedAtMax() != null) {
            if ((searchTradeRequest.getClosedAtMin()) > ((searchTradeRequest.getClosedAtMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchTradeRequest.getExpiredAtMin() != null && searchTradeRequest.getExpiredAtMax() != null) {
            if ((searchTradeRequest.getExpiredAtMin()).isAfter((searchTradeRequest.getExpiredAtMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        log.info("End - Verifying search request: {}", searchTradeRequest);
    }

    private Trade getTradeByIdAndUserId(String id, JwtClaim claims) {
        log.info("Start - Getting trade by id {} for user {}", id, claims.getUserId());
        Optional<Trade> tradeOptional = tradeRepository.findByUserIdAndId(claims.getUserId(), id);
        if (tradeOptional.isEmpty()) {
            throw new RuntimeException(DbBash.TRADE_NOT_FOUND);
        }
        log.info("End - Getting trade by id {} for user {}", id, claims.getUserId());
        return tradeOptional.get();
    }

}
