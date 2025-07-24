package com.learntrad.microservices.trade.model.request;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeRequest {
    @Min(value = 0, message = "Lot must be greater than 0")
    @Max(value = 10, message = "Lot must be less than 10")
    @NotNull(message = "Lot is required")
    private Double lot;
    @Min(value = 0, message = "Placed price must be greater than 0")
    @NotNull(message = "Placed price is required")
    private BigDecimal priceAt;
    @Min(value = 0, message = "Stop loss must be greater than 0")
    @NotNull(message = "Stop loss is required")
    private BigDecimal stopLossAt;
    @Min(value = 0, message = "Take profit must be greater than 0")
    @NotNull(message = "Take profit is required")
    private BigDecimal takeProfitAt;
    @NotNull(message = "Trade at is required")
    private Instant tradeAt;
    @NotBlank(message = "Market data type is required")
    private String marketDataType;
    @NotBlank(message = "Trade type is required")
    private String tradeType;
    private LocalDateTime expiredAt;
}
