package com.learntrad.microservices.topup.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.learntrad.microservices.shared.constant.DbBash;
import com.learntrad.microservices.shared.constant.enumerated.EPaymentStatus;
import com.learntrad.microservices.shared.constant.enumerated.EPaymentType;
import com.learntrad.microservices.topup.model.response.TopUpResponse;

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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = DbBash.TOP_UP_TABLE)
public class TopUp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EPaymentStatus paymentStatus;

    @Column(name = "payment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EPaymentType paymentType;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    public TopUpResponse toTopUpResponse() {
        return TopUpResponse.builder()
                .id(id)
                .userId(userId)
                .amount(amount)
                .paymentStatus(paymentStatus.getDescription())
                .paymentType(paymentType.getDescription())
                .expiredAt(expiredAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
