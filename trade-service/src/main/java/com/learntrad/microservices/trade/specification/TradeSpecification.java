package com.learntrad.microservices.trade.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.learntrad.microservices.trade.entity.Trade;
import com.learntrad.microservices.trade.model.request.search.SearchTradeRequest;

import jakarta.persistence.criteria.Predicate;

public class TradeSpecification {

    private TradeSpecification() {}

    public static Specification<Trade> getSpecification(SearchTradeRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), request.getUserId()));
            }

            if (request.getPriceAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("priceAt"), request.getPriceAtMin()));
            }

            if (request.getPriceAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("priceAt"), request.getPriceAtMax()));
            }

            if (request.getStopLossAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("stopLossAt"), request.getStopLossAtMin()));
            }

            if (request.getStopLossAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("stopLossAt"), request.getStopLossAtMax()));
            }

            if (request.getTakeProfitAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("takeProfitAt"), request.getTakeProfitAtMin()));
            }

            if (request.getTakeProfitAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("takeProfitAt"), request.getTakeProfitAtMax()));
            }

            if (request.getCreatedAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getCreatedAtMin()));
            }

            if (request.getCreatedAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.getCreatedAtMax()));
            }

            if (request.getUpdatedAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), request.getUpdatedAtMin()));
            }

            if (request.getUpdatedAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), request.getUpdatedAtMax()));
            }

            if (request.getTradeAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tradeAt"), request.getTradeAtMin()));
            }

            if (request.getTradeAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tradeAt"), request.getTradeAtMax()));
            }

            if (request.getMarketDataType() != null) {
                predicates.add(cb.like(root.get("marketDataType"), "%" + request.getMarketDataType() + "%"));
            }

            if (request.getTradeStatus() != null) {
                predicates.add(cb.equal(root.get("tradeStatus"), request.getTradeStatus()));
            }

            if (request.getLotMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lot"), request.getLotMin()));
            }

            if (request.getLotMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lot"), request.getLotMax()));
            }

            if (request.getTradeType() != null) {
                predicates.add(cb.equal(root.get("tradeType"), request.getTradeType()));
            }

            if (request.getClosedAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("closedAt"), request.getClosedAtMin()));
            }

            if (request.getClosedAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("closedAt"), request.getClosedAtMax()));
            }

            if (request.getExpiredAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expiredAt"), request.getExpiredAtMin()));
            }

            if (request.getExpiredAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expiredAt"), request.getExpiredAtMax()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
