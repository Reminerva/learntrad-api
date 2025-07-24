package com.learntrad.microservices.tradeprocessor.event;

import java.math.BigDecimal;

import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;
import com.learntrad.microservices.shared.constant.enumerated.ETradeStatus;
import com.learntrad.microservices.shared.constant.enumerated.ETradeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeProcessedEvent {
    private String id;
    private BigDecimal priceAt;
    private BigDecimal stopLossAt;
    private BigDecimal takeProfitAt;
    private EMarketDataType marketDataType;
    private ETradeStatus tradeStatus;
    private ETradeType tradeType;
    private BigDecimal closedAt;
    private String userId;
    private String username;
    private String email;
    private String customerFullname;
}
