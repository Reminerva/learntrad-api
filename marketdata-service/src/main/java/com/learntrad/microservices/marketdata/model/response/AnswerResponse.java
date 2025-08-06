package com.learntrad.microservices.marketdata.model.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {
    private BigDecimal result;
    private BigDecimal priceAt;
    private BigDecimal takeProfitAt;
    private BigDecimal stopLossAt;
    private Long dataCount;
    private List<?> answerMarketData;
}
