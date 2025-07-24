package com.learntrad.microservices.topup.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.learntrad.microservices.shared.constant.enumerated.EPaymentStatus;
import com.learntrad.microservices.shared.constant.enumerated.EPaymentType;
import com.learntrad.microservices.topup.entity.TopUp;
import com.learntrad.microservices.topup.model.request.search.SearchTopUpRequest;

import jakarta.persistence.criteria.Predicate;

public class TopUpSpecification {

    private TopUpSpecification() {}

    public static Specification<TopUp> getSpecification(SearchTopUpRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), request.getUserId()));
            }
            if (request.getAmountMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), request.getAmountMin()));
            }

            if (request.getAmountMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), request.getAmountMax()));
            }

            if (request.getPaymentStatus() != null) {
                predicates.add(cb.equal(root.get("paymentStatus"), EPaymentStatus.findByDescription(request.getPaymentStatus())));
            }

            if (request.getPaymentType() != null) {
                predicates.add(cb.equal(root.get("paymentType"), EPaymentType.findByDescription(request.getPaymentType())));
            }

            if (request.getExpiredAtMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expiredAt"), request.getExpiredAtMin()));
            }

            if (request.getExpiredAtMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expiredAt"), request.getExpiredAtMax()));
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

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
