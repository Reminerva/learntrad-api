package com.learntrad.microservices.trade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;
import com.learntrad.microservices.shared.constant.enumerated.ETradeStatus;
import com.learntrad.microservices.trade.entity.Trade;

@Repository
public interface TradeRepository extends JpaRepository<Trade, String>, JpaSpecificationExecutor<Trade> {
    Optional<Trade> findByUserIdAndId(String userId, String id);
    List<Trade> findByUserIdAndTradeStatusAndMarketDataType(String userId, ETradeStatus tradeStatus, EMarketDataType marketDataType);
}
