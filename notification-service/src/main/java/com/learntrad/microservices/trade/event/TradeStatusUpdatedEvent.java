package com.learntrad.microservices.trade.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeStatusUpdatedEvent {
    private String tradeId;
    private String userId;
    private String customerFullname;
    private String tradeAt;
    private String marketDataType;
    private String username;
    private String email;
    private Double lot;
    private BigDecimal priceAt;
    private BigDecimal stopLossAt;
    private BigDecimal takeProfitAt;
    private String tradeType;
    private String tradeStatus;
    private BigDecimal closedAt;
    private LocalDateTime expiredAt;
}
