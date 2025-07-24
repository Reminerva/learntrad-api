package com.learntrad.microservices.trade.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import com.learntrad.microservices.shared.constant.DbBash;
import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;
import com.learntrad.microservices.shared.constant.enumerated.ETradeStatus;
import com.learntrad.microservices.shared.constant.enumerated.ETradeType;
import com.learntrad.microservices.trade.model.response.TradeResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = DbBash.TRADE_TABLE)
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "lot")
    private Double lot;

    @Column(name = "price_at")
    private BigDecimal priceAt;

    @Column(name = "stop_loss_at")
    private BigDecimal stopLossAt;

    @Column(name = "take_profit_at")
    private BigDecimal takeProfitAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "trade_at")
    private Instant tradeAt;

    @Column(name = "market_data_type")
    @Enumerated(EnumType.STRING)
    private EMarketDataType marketDataType;

    @Column(name = "trade_status")
    @Enumerated(EnumType.STRING)
    private ETradeStatus tradeStatus;

    @Column(name = "trade_type")
    @Enumerated(EnumType.STRING)
    private ETradeType tradeType;

    @Column(name = "closed_at", nullable = true)
    private BigDecimal closedAt;

    @Column(name = "expired_at", nullable = true)
    private LocalDateTime expiredAt;

    public TradeResponse toTradeResponse() {
        return TradeResponse.builder()
                .id(id)
                .userId(userId)
                .priceAt(priceAt)
                .stopLossAt(stopLossAt)
                .lot(lot)
                .takeProfitAt(takeProfitAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .tradeAt(tradeAt.toString())
                .marketDataType(marketDataType.getDescription())
                .tradeStatus(tradeStatus.getDescription())
                .tradeType(tradeType.getDescription())
                .closedAt(closedAt)
                .expiredAt(expiredAt)
                .build();
    }

}
