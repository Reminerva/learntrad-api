package com.learntrad.microservices.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class OhlcBaseEntity {

    @Id
    @Column(name = "time_bucket_start", nullable = false)
    private Instant timeBucketStart;

    @Column(nullable = false)
    private BigDecimal open;

    @Column(nullable = false)
    private BigDecimal high;

    @Column(nullable = false)
    private BigDecimal low;

    @Column(nullable = false)
    private BigDecimal closed;

    @Column(nullable = false)
    private Long volume;

    // Getter and Setter
    public Instant getTimeBucketStart() {
        return timeBucketStart;
    }

    public void setTimeBucketStart(Instant timeBucketStart) {
        this.timeBucketStart = timeBucketStart;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClosed() {
        return closed;
    }

    public void setClosed(BigDecimal closed) {
        this.closed = closed;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}
