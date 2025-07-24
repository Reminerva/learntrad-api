package com.learntrad.microservices.marketdata.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.learntrad.microservices.marketdata.entity.XauusdEntity;

@Repository
public interface XauusdRepository extends JpaRepository<XauusdEntity, Instant>, CustomXauusdRepository, JpaSpecificationExecutor<XauusdEntity> {

    Optional<XauusdEntity> findTopByOrderByTimeBucketStartDesc();
    Optional<XauusdEntity> findByTimeBucketStart(Instant timeBucketStart);

    @Query(value = """
        SELECT
            time_bucket(CAST(:interval AS INTERVAL), time_bucket_start) AS bucket,
            first(open, time_bucket_start) AS open,
            max(high) AS high,
            min(low) AS low,
            last(closed, time_bucket_start) AS closed,
            sum(volume) AS volume
        FROM m_xauusd
        WHERE time_bucket_start BETWEEN :start AND :end
        GROUP BY bucket
        ORDER BY bucket ASC
        """, nativeQuery = true)
    List<Object[]> fetchAggregatedDataAsc(
        @Param("interval") String interval,
        @Param("start") Instant start,
        @Param("end") Instant end
    );

    @Query(value = """
        SELECT
            time_bucket(CAST(:interval AS INTERVAL), time_bucket_start) AS bucket,
            first(open, time_bucket_start) AS open,
            max(high) AS high,
            min(low) AS low,
            last(closed, time_bucket_start) AS closed,
            sum(volume) AS volume
        FROM m_xauusd
        WHERE time_bucket_start BETWEEN :start AND :end
        GROUP BY bucket
        ORDER BY bucket DESC
        """, nativeQuery = true)
    List<Object[]> fetchAggregatedDataDesc(
        @Param("interval") String interval,
        @Param("start") Instant start,
        @Param("end") Instant end
    );

}
