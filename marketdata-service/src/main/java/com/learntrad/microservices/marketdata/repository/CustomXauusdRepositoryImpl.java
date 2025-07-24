package com.learntrad.microservices.marketdata.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class CustomXauusdRepositoryImpl implements CustomXauusdRepository {

    private final EntityManager em;

    @Override
    @Transactional
    public void upsertOhlcTick(Instant bucketStart, BigDecimal price) {
        Query query = em.createNativeQuery("""
            INSERT INTO m_xauusd (
                time_bucket_start, open, high, low, closed, volume
            ) VALUES (
                :bucketStart, :price, :price, :price, :price, 0
            )
            ON CONFLICT (time_bucket_start)
            DO UPDATE SET
                closed = EXCLUDED.closed,
                high = GREATEST(m_xauusd.high, EXCLUDED.high),
                low = LEAST(m_xauusd.low, EXCLUDED.low),
                volume = m_xauusd.volume
        """);
        query.setParameter("bucketStart", bucketStart);
        query.setParameter("price", price);
        query.executeUpdate();
    }
}
