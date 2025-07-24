package com.learntrad.microservices.customer.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.learntrad.microservices.customer.entity.Customer;
import com.learntrad.microservices.customer.model.request.CustomerRequest;
import com.learntrad.microservices.customer.model.request.search.SearchCustomerRequest;
import com.learntrad.microservices.customer.model.response.CustomerResponse;
import com.learntrad.microservices.customer.repository.CustomerRepository;
import com.learntrad.microservices.customer.service.intrface.CustomerService;
import com.learntrad.microservices.customer.specification.CustomerSpecification;
import com.learntrad.microservices.shared.jwt.JwtClaim;
import com.learntrad.microservices.shared.jwt.JwtUtil;
import com.learntrad.microservices.topup.event.BalanceAdjustedEvent;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.learntrad.microservices.shared.constant.ConstantBash;
import com.learntrad.microservices.shared.constant.DbBash;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomerResponse createCustomer(String authHeader, CustomerRequest customerRequest) {
        try {
            JwtClaim claims = JwtUtil.getClaims(authHeader);
            log.info("Start - Creating customer: {}", customerRequest);

            Customer customer = Customer.builder()
                .fullname(customerRequest.getFullname())
                .address(customerRequest.getAddress())
                .birthDate(customerRequest.getBirthDate())
                .balance(BigDecimal.valueOf(5000.0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .userId(claims.getUserId())
                .isActive(true)
                .build();

            log.info("Start - Saving customer: {}", customer);
            customerRepository.save(customer);
            log.info("End - Saving customer: {}", customer);
            log.info("End - Creating customer: {}", customerRequest);
            return toCustomerResponse(customer);
        } catch(Exception e) {
            log.error("Error creating customer: {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Customer getCustomerById(String id) {
        try {
            log.info("Start - Getting customer by id: {}", id);
            Optional<Customer> customer = customerRepository.findById(id);
            if (customer.isEmpty()) {
                log.error(DbBash.CUSTOMER_NOT_FOUND);
                throw new RuntimeException(DbBash.CUSTOMER_NOT_FOUND);
            }
            log.info("End - Getting customer by id: {}", customer.get());
            return customer.get();
        } catch(Exception e) {
            log.error("Error getting customer: {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CustomerResponse getById(String id) {
        try {
            Customer customer = getCustomerById(id);
            return toCustomerResponse(customer);
        } catch(Exception e) {
            log.error("Error getting customer: {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<CustomerResponse> getAllCustomers(SearchCustomerRequest searchCustomerRequest) {
        try {
            log.info("Start - Getting customers");
            verifySearchRequest(searchCustomerRequest);

            Sort sort = Sort.by(Sort.Direction.fromString(searchCustomerRequest.getDirection()), searchCustomerRequest.getSortBy());
            Pageable pageable = PageRequest.of(searchCustomerRequest.getPage() - 1, searchCustomerRequest.getSize(), sort);
            Specification<Customer> specification = CustomerSpecification.getSpecification(searchCustomerRequest);

            Page<Customer> customerPage = customerRepository.findAll(specification, pageable);
            log.info("End - Getting customers: {}");
            return customerPage.map(this::toCustomerResponse);

        } catch(Exception e) {
            log.error("Error getting customers: {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomerResponse updateCustomer(String id, CustomerRequest customerRequest) {
        try {
            log.info("Start - Updating customer: {}", customerRequest);
            Customer customer = getCustomerById(id);
            customer.setFullname(customerRequest.getFullname());
            customer.setAddress(customerRequest.getAddress());
            customer.setBirthDate(customerRequest.getBirthDate());
            // customer.setBalance(customerRequest.getBalance());
            customer.setUpdatedAt(LocalDateTime.now());

            log.info("Start - Saving customer: {}", customerRequest);
            customerRepository.save(customer);
            log.info("End - Saving customer: {}", customerRequest);
            log.info("End - Updating customer: {}", customerRequest);
            return toCustomerResponse(customer);
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteCustomer(String id) {
        try {
            log.info("Start - Deleting customer");
            Customer customer = getCustomerById(id);
            customer.setIsActive(false);
            log.info("Start - Saving customer: {}", customer);
            customerRepository.save(customer);
            log.info("End - Saving customer: {}", customer);
            log.info("End - Deleting customer");
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CustomerResponse getMe(String authHeader) {
        try {
            JwtClaim claims = JwtUtil.getClaims(authHeader);
            Customer customer = getCustomerByUserId(claims.getUserId());
            if (customer.getIsActive() == false) {
                log.error(DbBash.CUSTOMER_HAS_BEEN_DELETED);
                throw new RuntimeException(DbBash.CUSTOMER_HAS_BEEN_DELETED);
            }
            return toCustomerResponse(customer);
        } catch(Exception e) {
            log.error("Error getting customer: {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomerResponse updateMe(String authHeader, CustomerRequest customerRequest) {
        try {
            CustomerResponse customer = getMe(authHeader);
            CustomerResponse customerUpdated = updateCustomer(customer.getId(), customerRequest);
            return customerUpdated;
        } catch(Exception e) {
            log.error("Error updating customer: {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteMe(String authHeader) {
        try {
            log.info("Start - Deleting customer");
            CustomerResponse customer = getMe(authHeader);
            deleteCustomer(customer.getId());
            log.info("End - Deleting customer");
        } catch(Exception e) {
            log.error("Error deleting customer: {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    @KafkaListener(topics = "balance-adjusted")
    public void updateMyBalance(BalanceAdjustedEvent balanceAdjustedEvent) {
        try {
            log.info("Received balance adjusted event from balance-adjusted topic");
            log.info("Start - Updating customer balance: {}", balanceAdjustedEvent);
            Customer customer = getCustomerByUserId(balanceAdjustedEvent.getUserId());
            if (customer.getIsActive() == false) {
                log.error(DbBash.CUSTOMER_HAS_BEEN_DELETED);
                throw new RuntimeException(DbBash.CUSTOMER_HAS_BEEN_DELETED);
            }
            BigDecimal amount = balanceAdjustedEvent.getAmount();
            customer.setBalance(customer.getBalance().add(amount));
            log.info("Start - Saving customer: {}", customer);
            customerRepository.save(customer);
            log.info("End - Saving customer: {}", customer);
            log.info("End - Updating customer balance: {}", balanceAdjustedEvent);
        } catch(Exception e) {
            log.error("Error updating customer balance: {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Customer getCustomerByUserId(String userId) {
        try {
            log.info("Start - Getting customer by user id: {}", userId);
            Optional<Customer> customer = customerRepository.findByUserId(userId);
            if (customer.isEmpty()) {
                log.error(DbBash.CUSTOMER_NOT_FOUND);
                throw new RuntimeException(DbBash.CUSTOMER_NOT_FOUND);
            }
            log.info("End - Getting customer by user id: {}", customer.get());
            return customer.get();
        } catch(Exception e) {
            log.error("Error getting customer: {}", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private CustomerResponse toCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
            .id(customer.getId())
            .fullname(customer.getFullname())
            .address(customer.getAddress())
            .birthDate(customer.getBirthDate().toString())
            .balance(customer.getBalance())
            .createdAt(customer.getCreatedAt().toString())
            .updatedAt(customer.getUpdatedAt().toString())
            .userId(customer.getUserId())
            .isActive(customer.getIsActive())
            .build();
    }

    private void verifySearchRequest(SearchCustomerRequest searchCustomerRequest) {
        log.info("Verifying search request: {}", searchCustomerRequest);
        if (searchCustomerRequest.getPage() <= 0) {
            searchCustomerRequest.setPage(1);
        }
        if (searchCustomerRequest.getSize() <= 0) {
            searchCustomerRequest.setSize(10);
        }
        if (searchCustomerRequest.getBirthDateMin() != null && searchCustomerRequest.getBirthDateMax() != null) {
            if ((searchCustomerRequest.getBirthDateMin()).isAfter((searchCustomerRequest.getBirthDateMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchCustomerRequest.getBalanceMin() != null && searchCustomerRequest.getBalanceMax() != null) {
            if ((searchCustomerRequest.getBalanceMin()) > ((searchCustomerRequest.getBalanceMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchCustomerRequest.getCreatedAtMin() != null && searchCustomerRequest.getCreatedAtMax() != null) {
            if ((searchCustomerRequest.getCreatedAtMin()).isAfter((searchCustomerRequest.getCreatedAtMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        if (searchCustomerRequest.getUpdatedAtMin() != null && searchCustomerRequest.getUpdatedAtMax() != null) {
            if ((searchCustomerRequest.getUpdatedAtMin()).isAfter((searchCustomerRequest.getUpdatedAtMax()))) {
                throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
            }
        }
        log.info("Search request verified successfully: {}", searchCustomerRequest);
    }

}
