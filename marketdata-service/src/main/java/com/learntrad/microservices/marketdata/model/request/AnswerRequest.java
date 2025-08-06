package com.learntrad.microservices.marketdata.model.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {
    @NotNull(message = "priceAt is required")
    @Min(value = 0, message = "priceAt must be greater than 0")
    private BigDecimal priceAt;
    @NotNull(message = "takeProfitAt is required")
    @Min(value = 0, message = "takeProfitAt must be greater than 0")
    private BigDecimal takeProfitAt;
    @NotNull(message = "stopLossAt is required")
    @Min(value = 0, message = "stopLossAt must be greater than 0")
    private BigDecimal stopLossAt;
}
