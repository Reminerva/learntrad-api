package com.learntrad.microservices.marketdata.repository;

import java.math.BigDecimal;
import java.time.Instant;

public interface CustomXauusdRepository {

    void upsertOhlcTick(Instant bucketStart, BigDecimal price);
}
