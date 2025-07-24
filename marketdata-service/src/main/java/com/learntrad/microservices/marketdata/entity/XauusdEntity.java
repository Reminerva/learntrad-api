package com.learntrad.microservices.marketdata.entity;

import com.learntrad.microservices.shared.constant.DbBash;
import com.learntrad.microservices.shared.entity.OhlcBaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = DbBash.XAUUSD_TABLE)
public class XauusdEntity extends OhlcBaseEntity {
}
