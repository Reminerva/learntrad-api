package com.learntrad.microservices.topup.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceAdjustedEvent {
    private String userId;
    private BigDecimal amount;
}
