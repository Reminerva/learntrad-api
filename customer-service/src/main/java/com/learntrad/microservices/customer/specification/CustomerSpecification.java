package com.learntrad.microservices.customer.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.learntrad.microservices.customer.entity.Customer;
import com.learntrad.microservices.customer.model.request.search.SearchCustomerRequest;

import jakarta.persistence.criteria.Predicate;

public class CustomerSpecification {

    private CustomerSpecification() {}

    public static Specification<Customer> getSpecification(SearchCustomerRequest request) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getFullname() != null) {
                predicates.add(cb.like(cb.lower(root.get("fullname")), "%" + request.getFullname().toLowerCase() + "%"));
            }

            if (request.getAddress() != null) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + request.getAddress().toLowerCase() + "%"));
            }

            if (request.getBirthDateMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("birthDate"), request.getBirthDateMin()));
            }

            if (request.getBirthDateMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("birthDate"), request.getBirthDateMax()));
            }

            if (request.getBalanceMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("balance"), request.getBalanceMin()));
            }

            if (request.getBalanceMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("balance"), request.getBalanceMax()));
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

            if (request.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), request.getUserId()));
            }

            if (request.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.getIsActive()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
