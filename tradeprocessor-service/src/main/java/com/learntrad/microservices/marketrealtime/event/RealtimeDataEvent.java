package com.learntrad.microservices.marketrealtime.event;

import java.math.BigDecimal;
import java.time.Instant;

import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealtimeDataEvent {
    private Instant time;
    private BigDecimal price;
    private EMarketDataType marketDataType;
}
