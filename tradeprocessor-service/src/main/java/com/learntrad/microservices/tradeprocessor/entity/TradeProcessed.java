package com.learntrad.microservices.tradeprocessor.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.learntrad.microservices.shared.constant.DbBash;
import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;
import com.learntrad.microservices.shared.constant.enumerated.ETradeStatus;
import com.learntrad.microservices.shared.constant.enumerated.ETradeType;
import com.learntrad.microservices.tradeprocessor.event.TradeProcessedEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = DbBash.TRADE_PROCESSED_TABLE)
public class TradeProcessed {

    @Id
    private String id;

    @Column(name = "price_at")
    private BigDecimal priceAt;

    @Column(name = "stop_loss_at")
    private BigDecimal stopLossAt;

    @Column(name = "take_profit_at")
    private BigDecimal takeProfitAt;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private TradeProcessedUser tradeProcessedUser;

    public TradeProcessedEvent toTradeProcessedEvent() {
        return TradeProcessedEvent.builder()
                .id(id)
                .priceAt(priceAt)
                .stopLossAt(stopLossAt)
                .takeProfitAt(takeProfitAt)
                .marketDataType(marketDataType)
                .tradeStatus(tradeStatus)
                .tradeType(tradeType)
                .closedAt(closedAt)
                .userId(tradeProcessedUser.getUserId())
                .username(tradeProcessedUser.getUsername())
                .email(tradeProcessedUser.getEmail())
                .customerFullname(tradeProcessedUser.getCustomerFullname())
                .build();
    }
}
