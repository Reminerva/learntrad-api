package com.learntrad.microservices.trade.model.request.search;

import java.time.Instant;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchTradeRequest {

    private String id;
    private String userId;
    private Integer lotMin;
    private Integer lotMax;
    private Double priceAtMin;
    private Double priceAtMax;
    private Double stopLossAtMin;
    private Double stopLossAtMax;
    private Double takeProfitAtMin;
    private Double takeProfitAtMax;
    private LocalDateTime createdAtMin;
    private LocalDateTime createdAtMax;
    private LocalDateTime updatedAtMin;
    private LocalDateTime updatedAtMax;
    private Instant tradeAtMin;
    private Instant tradeAtMax;
    private String marketDataType;
    private String tradeStatus;
    private String tradeType;
    private Integer closedAtMin;
    private Integer closedAtMax;
    private LocalDateTime expiredAtMin;
    private LocalDateTime expiredAtMax;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
