package com.learntrad.microservices.tradeprocessor.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.learntrad.microservices.marketrealtime.event.RealtimeDataEvent;
import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;
import com.learntrad.microservices.shared.constant.enumerated.ETradeStatus;
import com.learntrad.microservices.shared.constant.enumerated.ETradeType;
import com.learntrad.microservices.trade.event.TradeEditedEvent;
import com.learntrad.microservices.trade.event.TradePlacedEvent;
import com.learntrad.microservices.trade.event.TradeStatusUpdatedEvent;
import com.learntrad.microservices.tradeprocessor.entity.TradeProcessed;
import com.learntrad.microservices.tradeprocessor.entity.TradeProcessedUser;
import com.learntrad.microservices.tradeprocessor.event.TradeProcessedEvent;
import com.learntrad.microservices.tradeprocessor.repository.TradeProcessedRepository;
import com.learntrad.microservices.tradeprocessor.service.intrface.TradeProcessorService;
import com.learntrad.microservices.tradeprocessor.service.intrface.TradeProcessorUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeProcessorServiceImpl implements TradeProcessorService {

    private final TradeProcessedRepository tradeProcessedRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, TradeProcessedEvent> kafkaTemplate;
    private final TradeProcessorUserService tradeProcessorUserService;

    @Override
    @KafkaListener(topics = "trade-placed")
    @Transactional(rollbackOn = Exception.class)
    public void listenTradePlaced(TradePlacedEvent tradePlacedEvent) {
        try {
            log.info("Received trade placed event from trade-plcaed topic {}", tradePlacedEvent);
            TradeProcessed tradeProcessed = TradeProcessed.builder()
                .id(tradePlacedEvent.getTradeId())
                .priceAt(tradePlacedEvent.getPriceAt())
                .stopLossAt(tradePlacedEvent.getStopLossAt())
                .takeProfitAt(tradePlacedEvent.getTakeProfitAt())
                .marketDataType(EMarketDataType.findByDescription(tradePlacedEvent.getMarketDataType()))
                .tradeType(ETradeType.findByDescription(tradePlacedEvent.getTradeType()))
                .expiredAt(tradePlacedEvent.getExpiredAt())
                .build();

            TradeProcessedUser tradeProcessedUser = tradeProcessorUserService.getTradeProcessedUserById(tradePlacedEvent.getUserId());
            if (tradeProcessedUser == null) {
                tradeProcessedUser = tradeProcessorUserService.createTradeProcessedUser(TradeProcessedUser.builder()
                    .userId(tradePlacedEvent.getUserId())
                    .customerFullname(tradePlacedEvent.getCustomerFullname())
                    .email(tradePlacedEvent.getEmail())
                    .username(tradePlacedEvent.getUsername())
                    .build());
            } else {
                if (!tradeProcessedUser.getCustomerFullname().equals(tradePlacedEvent.getCustomerFullname())) {
                    tradeProcessedUser.setCustomerFullname(tradePlacedEvent.getCustomerFullname());
                    tradeProcessedUser = tradeProcessorUserService.updateTradeProcessedUser(tradeProcessedUser, tradePlacedEvent.getUserId());
                } else if (!tradeProcessedUser.getEmail().equals(tradePlacedEvent.getEmail())) {
                    tradeProcessedUser.setEmail(tradePlacedEvent.getEmail());
                    tradeProcessedUser = tradeProcessorUserService.updateTradeProcessedUser(tradeProcessedUser, tradePlacedEvent.getUserId());
                } else if (!tradeProcessedUser.getUsername().equals(tradePlacedEvent.getUsername())) {
                    tradeProcessedUser.setUsername(tradePlacedEvent.getUsername());
                    tradeProcessedUser = tradeProcessorUserService.updateTradeProcessedUser(tradeProcessedUser, tradePlacedEvent.getUserId());
                }
            }
            tradeProcessed.setTradeProcessedUser(tradeProcessedUser);
            
            if (tradeProcessed.getTradeType().equals(ETradeType.MARKET_EXECUTION_BUY) || tradeProcessed.getTradeType().equals(ETradeType.MARKET_EXECUTION_SELL)) {
                tradeProcessed.setTradeStatus(ETradeStatus.RUNNING);
            } else {
                tradeProcessed.setTradeStatus(ETradeStatus.PENDING);
            }

            log.info("Start - Saving new trade processed {}", tradeProcessed);
            tradeProcessedRepository.saveAndFlush(tradeProcessed);
            log.info("End - Saving new trade processed {}", tradeProcessed);
        } catch (Exception e) {
            log.error("Error saving new trade {}", tradePlacedEvent, e);
        }
    }
    @Override
    @KafkaListener(topics = "trade-edited")
    @Transactional(rollbackOn = Exception.class)
    public void listenTradeEdited(TradeEditedEvent tradeEditedEvent) {
        try {
            log.info("Received trade edited event from trade-edited topic {}", tradeEditedEvent);
            log.info("Start - Getting trade processed by id {}", tradeEditedEvent.getTradeId());
            Optional<TradeProcessed> tradeProcessedOptional = tradeProcessedRepository.findById(tradeEditedEvent.getTradeId());

            if (!tradeProcessedOptional.isPresent()) {
                log.error("Trade processed not found {}", tradeEditedEvent.getTradeId());
                return;
            }

            TradeProcessed tradeProcessed = tradeProcessedOptional.get();
            log.info("End - Getting trade processed by id {}", tradeEditedEvent.getTradeId());
            if (!tradeProcessed.getTradeStatus().equals(ETradeStatus.RUNNING) && !tradeProcessed.getTradeStatus().equals(ETradeStatus.PENDING)) {
                log.error("Trade processed is not running or pending {}", tradeEditedEvent.getTradeId());
                return;
            }

            TradeProcessedUser tradeProcessedUser = tradeProcessorUserService.getTradeProcessedUserById(tradeEditedEvent.getUserId());
            if (tradeProcessedUser == null) {
                tradeProcessedUser = tradeProcessorUserService.createTradeProcessedUser(TradeProcessedUser.builder()
                    .userId(tradeEditedEvent.getUserId())
                    .customerFullname(tradeEditedEvent.getCustomerFullname())
                    .email(tradeEditedEvent.getEmail())
                    .username(tradeEditedEvent.getUsername())
                    .build());
            } else {
                if (!tradeProcessedUser.getCustomerFullname().equals(tradeEditedEvent.getCustomerFullname())) {
                    tradeProcessedUser.setCustomerFullname(tradeEditedEvent.getCustomerFullname());
                    tradeProcessedUser = tradeProcessorUserService.updateTradeProcessedUser(tradeProcessedUser, tradeEditedEvent.getUserId());
                } else if (!tradeProcessedUser.getEmail().equals(tradeEditedEvent.getEmail())) {
                    tradeProcessedUser.setEmail(tradeEditedEvent.getEmail());
                    tradeProcessedUser = tradeProcessorUserService.updateTradeProcessedUser(tradeProcessedUser, tradeEditedEvent.getUserId());
                } else if (!tradeProcessedUser.getUsername().equals(tradeEditedEvent.getUsername())) {
                    tradeProcessedUser.setUsername(tradeEditedEvent.getUsername());
                    tradeProcessedUser = tradeProcessorUserService.updateTradeProcessedUser(tradeProcessedUser, tradeEditedEvent.getUserId());
                }
            }
            tradeProcessed.setTradeProcessedUser(tradeProcessedUser);

            tradeProcessed.setStopLossAt(tradeEditedEvent.getNewStopLossAt());
            tradeProcessed.setTakeProfitAt(tradeEditedEvent.getNewTakeProfitAt());
            tradeProcessed.setExpiredAt(tradeEditedEvent.getNewExpiredAt());

            log.info("Start - Saving updated trade processed {}", tradeProcessed);
            tradeProcessedRepository.save(tradeProcessed);
            log.info("End - Saving updated trade processed {}", tradeProcessed);

        } catch (Exception e) {
            log.error("Error updating trade {}", tradeEditedEvent, e);
        }
    }
    @Override
    @KafkaListener(topics = "realtime-data")
    @Transactional(rollbackOn = Exception.class)
    public void listenRealtimeData(RealtimeDataEvent newRealtimeDataEvent) {
        try {
            log.info("Received realtime data event from realtime-data topic {}", newRealtimeDataEvent);

            if (redisTemplate.opsForValue().get(newRealtimeDataEvent.getMarketDataType().getDescription()) == null) {
                redisTemplate.opsForValue().set(newRealtimeDataEvent.getMarketDataType().getDescription(), newRealtimeDataEvent.getPrice().toString());
            }

            RealtimeDataEvent oldRealtimeDataEvent = RealtimeDataEvent.builder()
                .marketDataType(newRealtimeDataEvent.getMarketDataType())
                .price(new BigDecimal(redisTemplate.opsForValue().get(newRealtimeDataEvent.getMarketDataType().getDescription())))
                .build();

            LocalDateTime now = LocalDateTime.now();

            tradeProcessedRepository.findAllByTradeStatus(ETradeStatus.PENDING).stream().forEach(tradeProcessed -> {
                if (tradeProcessed.getExpiredAt() != null) {
                    if (tradeProcessed.getExpiredAt().isBefore(now)) {
                        tradeProcessed.setTradeStatus(ETradeStatus.EXPIRED);
                        log.info("Start - Updating trade processed (EXPIRED) {}", tradeProcessed);
                        tradeProcessedRepository.save(tradeProcessed);
                        log.info("End - Updating trade processed (EXPIRED) {}", tradeProcessed);
                        sendTradeProcessedEvent(tradeProcessed.toTradeProcessedEvent(), true);
                    }
                }
                else if (oldRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getPriceAt()) <= 0 && 
                    newRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getPriceAt()) >= 0
                    ) {
                    log.info("Start - Updating trade processed RUNNING {}", tradeProcessed);

                    tradeProcessed.setTradeStatus(ETradeStatus.RUNNING);
                    tradeProcessedRepository.save(tradeProcessed);
                    log.info("End - Updating trade processed RUNNING {}", tradeProcessed);

                    sendTradeProcessedEvent(tradeProcessed.toTradeProcessedEvent(), false);
                }
                else if (oldRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getPriceAt()) >= 0 && 
                    newRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getPriceAt()) <= 0
                    ) {
                    log.info("Start - Updating trade processed RUNNING {}", tradeProcessed);

                    tradeProcessed.setTradeStatus(ETradeStatus.RUNNING);
                    tradeProcessedRepository.save(tradeProcessed);
                    log.info("End - Updating trade processed RUNNING {}", tradeProcessed);

                    sendTradeProcessedEvent(tradeProcessed.toTradeProcessedEvent(), false);
                }
            });

            tradeProcessedRepository.findAllByTradeStatus(ETradeStatus.RUNNING).stream().forEach(tradeProcessed -> {
                if (tradeProcessed.getExpiredAt() != null) {
                    if (tradeProcessed.getExpiredAt().isBefore(now)) {
                        tradeProcessed.setTradeStatus(ETradeStatus.EXPIRED);
                        // tradeProcessedRepository.save(tradeProcessed);
                        log.info("Trade processed updated successfully to EXPIRED {}", tradeProcessed);
                        sendTradeProcessedEvent(tradeProcessed.toTradeProcessedEvent(), true);
                    }
                }
                if (oldRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getTakeProfitAt()) <= 0 && 
                    newRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getTakeProfitAt()) >= 0
                ) {

                    log.info("Start - Updating RUNNING trade processed PROFIT {}", tradeProcessed);

                    tradeProcessed.setTradeStatus(ETradeStatus.PROFIT);
                    tradeProcessed.setClosedAt(tradeProcessed.getTakeProfitAt());
                    tradeProcessedRepository.save(tradeProcessed);
                    log.info("End - Updating RUNNING trade processed PROFIT {}", tradeProcessed);

                    sendTradeProcessedEvent(tradeProcessed.toTradeProcessedEvent(), true);
                }
                else if (oldRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getTakeProfitAt()) >= 0 && 
                    newRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getTakeProfitAt()) <= 0
                ) {

                    log.info("Start - Updating RUNNING trade processed PROFIT {}", tradeProcessed);

                    tradeProcessed.setTradeStatus(ETradeStatus.PROFIT);
                    tradeProcessed.setClosedAt(tradeProcessed.getTakeProfitAt());
                    tradeProcessedRepository.save(tradeProcessed);
                    log.info("End - Updating RUNNING trade processed PROFIT {}", tradeProcessed);

                    sendTradeProcessedEvent(tradeProcessed.toTradeProcessedEvent(), true);
                }
                else if (oldRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getStopLossAt()) <= 0 && 
                    newRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getStopLossAt()) >= 0
                ) {

                    log.info("Start - Updating RUNNING trade processed LOSS {}", tradeProcessed);

                    tradeProcessed.setTradeStatus(ETradeStatus.LOSS);
                    tradeProcessed.setClosedAt(tradeProcessed.getStopLossAt());
                    tradeProcessedRepository.save(tradeProcessed);
                    log.info("End - Updating RUNNING trade processed LOSS {}", tradeProcessed);

                    sendTradeProcessedEvent(tradeProcessed.toTradeProcessedEvent(), true);
                }
                else if (oldRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getStopLossAt()) >= 0 && 
                    newRealtimeDataEvent.getPrice().compareTo(tradeProcessed.getStopLossAt()) <= 0
                ) {

                    log.info("Start - Updating RUNNING trade processed LOSS {}", tradeProcessed);

                    tradeProcessed.setTradeStatus(ETradeStatus.LOSS);
                    tradeProcessed.setClosedAt(tradeProcessed.getStopLossAt());
                    tradeProcessedRepository.save(tradeProcessed);
                    log.info("End - Updating RUNNING trade processed LOSS {}", tradeProcessed);
                    
                    sendTradeProcessedEvent(tradeProcessed.toTradeProcessedEvent(), true);
                }
            });

            log.info("Start - Updating realtime data {}", newRealtimeDataEvent);
            redisTemplate.opsForValue().set(newRealtimeDataEvent.getMarketDataType().getDescription(), newRealtimeDataEvent.getPrice().toString());
            log.info("End - Updating realtime data {}", newRealtimeDataEvent);
        } catch (Exception e) {
            log.error("Error processing trade {}", newRealtimeDataEvent, e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void sendTradeProcessedEvent(TradeProcessedEvent tradeProcessedEvent, Boolean shouldDelete) {
        try {
            log.info("Start - Sending trade processed event {}", tradeProcessedEvent);
            kafkaTemplate.send("trade-processed", tradeProcessedEvent);
            log.info("End - Sending trade processed event{}", tradeProcessedEvent);

            if (shouldDelete) {
                log.info("Start - Deleting trade processed {}", tradeProcessedEvent.getId());
                tradeProcessedRepository.deleteById(tradeProcessedEvent.getId());
                log.info("End - Deleting trade processed {}", tradeProcessedEvent.getId());
            }
        } catch (Exception e) {
            log.error("Error sending trade processed event {}", tradeProcessedEvent, e);
        }
    }

    @Override
    @KafkaListener(topics = "trade-canceled")
    public void listenTradeCanceled(TradeStatusUpdatedEvent tradeStatusUpdatedEvent) {
        try {
            log.info("Received trade canceled event from trade-canceled topic {}", tradeStatusUpdatedEvent);
            log.info("Start - Getting trade processed by id {}", tradeStatusUpdatedEvent.getTradeId());
            Optional<TradeProcessed> tradeProcessedOptional = tradeProcessedRepository.findById(tradeStatusUpdatedEvent.getTradeId());

            if (!tradeProcessedOptional.isPresent()) {
                log.error("Trade processed not found {}", tradeStatusUpdatedEvent.getTradeId());
                return;
            }
            
            TradeProcessed tradeProcessed = tradeProcessedOptional.get();
            log.info("End - Getting trade processed by id {}", tradeStatusUpdatedEvent.getTradeId());

            if (!tradeProcessed.getTradeStatus().equals(ETradeStatus.RUNNING) && !tradeProcessed.getTradeStatus().equals(ETradeStatus.PENDING)) {
                log.error("Trade processed is not running or pending {}", tradeStatusUpdatedEvent.getTradeId());
                return;
            }
            
            if (tradeProcessed.getTradeStatus().equals(ETradeStatus.PENDING)) {
                tradeProcessed.setTradeStatus(ETradeStatus.findByDescription(tradeStatusUpdatedEvent.getTradeStatus()));
                log.info("Start - Saving updated trade processed {}", tradeProcessed);
                tradeProcessedRepository.save(tradeProcessed);
                log.info("End - Saving updated trade processed {}", tradeProcessed);
            }
            
            if (tradeProcessed.getTradeStatus().equals(ETradeStatus.RUNNING)) {
                tradeProcessed.setTradeStatus(ETradeStatus.findByDescription(tradeStatusUpdatedEvent.getTradeStatus()));

                RealtimeDataEvent realtimeDataEvent = RealtimeDataEvent.builder()
                    .marketDataType(EMarketDataType.findByDescription(tradeStatusUpdatedEvent.getMarketDataType()))
                    .price(new BigDecimal(redisTemplate.opsForValue().get(tradeStatusUpdatedEvent.getMarketDataType())))
                    .build();

                tradeProcessed.setClosedAt(realtimeDataEvent.getPrice());

                log.info("Start - Saving updated trade processed {}", tradeProcessed);
                tradeProcessedRepository.save(tradeProcessed);
                log.info("End - Saving updated trade processed {}", tradeProcessed);

                sendTradeProcessedEvent(tradeProcessed.toTradeProcessedEvent(), true);
            }

        } catch (Exception e) {
            log.error("Error updating trade {}", tradeStatusUpdatedEvent, e);
        }
    }

}
