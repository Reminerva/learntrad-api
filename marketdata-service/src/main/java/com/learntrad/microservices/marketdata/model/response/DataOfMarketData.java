package com.learntrad.microservices.marketdata.model.response;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataOfMarketData {
    private Instant timeBucketStart;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal closed;
    private Long volume;

    public static DataOfMarketData toMarketData(Object[] row) {
        return DataOfMarketData.builder()
            .timeBucketStart((Instant) row[0])
            .open((BigDecimal) row[1])
            .high((BigDecimal) row[2])
            .low((BigDecimal) row[3])
            .closed((BigDecimal) row[4])
            .volume(((Number) row[5]).longValue())
            .build();
    }
}
