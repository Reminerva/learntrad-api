package com.learntrad.microservices.marketdata.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import com.learntrad.microservices.shared.constant.DbBash;
import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;
import com.learntrad.microservices.shared.constant.enumerated.ENSize;
import com.learntrad.microservices.shared.constant.enumerated.ETimeFrame;

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
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = DbBash.QUIZ_TABLE)
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "time_bucket_start", nullable = false)
    private Instant timeBucketStart;

    @Column(name = "n_size", nullable = false)
    @Enumerated(EnumType.STRING)
    private ENSize nSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_frame", nullable = false)
    private ETimeFrame timeFrame;

    @Column(name = "market_data_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EMarketDataType marketDataType;

    @Column(name = "vertical_translation", nullable = false)
    private BigDecimal verticalTranslation;

    @Column(name = "scale", nullable = false)
    private BigDecimal scale;

    @Column(name = "result", nullable = true)
    private BigDecimal result;

    @Column(name = "price_at", nullable = true)
    private BigDecimal priceAt;

    @Column(name = "take_profit_at", nullable = true)
    private BigDecimal takeProfitAt;

    @Column(name = "stop_loss_at", nullable = true)
    private BigDecimal stopLossAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;
}
