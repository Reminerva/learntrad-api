package com.learntrad.microservices.tradeprocessor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learntrad.microservices.shared.constant.enumerated.ETradeStatus;
import com.learntrad.microservices.tradeprocessor.entity.TradeProcessed;

@Repository
public interface TradeProcessedRepository extends JpaRepository<TradeProcessed, String> {
    List<TradeProcessed> findAllByTradeStatus(ETradeStatus tradeStatus);
}
