package com.learntrad.microservices.trade.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeResponse {
    private String id;
    private String userId;
    private BigDecimal priceAt;
    private BigDecimal stopLossAt;
    private BigDecimal takeProfitAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String tradeAt;
    private Double lot;
    private String marketDataType;
    private String tradeStatus;
    private String tradeType;
    private BigDecimal closedAt;
    private LocalDateTime expiredAt;

}
