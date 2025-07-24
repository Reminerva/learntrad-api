package com.learntrad.microservices.topup.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.learntrad.microservices.shared.constant.ConstantBash;
import com.learntrad.microservices.shared.constant.DbBash;
import com.learntrad.microservices.shared.constant.enumerated.EPaymentStatus;
import com.learntrad.microservices.shared.constant.enumerated.EPaymentType;
import com.learntrad.microservices.shared.jwt.JwtClaim;
import com.learntrad.microservices.shared.jwt.JwtUtil;
import com.learntrad.microservices.topup.entity.TopUp;
import com.learntrad.microservices.topup.event.BalanceAdjustedEvent;
import com.learntrad.microservices.topup.model.request.TopUpRequest;
import com.learntrad.microservices.topup.model.request.search.SearchTopUpRequest;
import com.learntrad.microservices.topup.model.response.TopUpResponse;
import com.learntrad.microservices.topup.repository.TopUpRepository;
import com.learntrad.microservices.topup.service.intrface.TopUpService;
import com.learntrad.microservices.topup.specification.TopUpSpecification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TopUpServiceImpl implements TopUpService {

    private final TopUpRepository topUpRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, BalanceAdjustedEvent> kafkaTemplateBalanceAdjusted;

    @Value("${topup.expiredin.seconds}")
    private Integer topUpExpiredIn;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TopUpResponse topUpMyBalance(String authHeader, TopUpRequest topUpRequest) {
        try {
            JwtClaim claims = JwtUtil.getClaims(authHeader);

            TopUp topUp = TopUp.builder()
                .userId(claims.getUserId())
                .amount(topUpRequest.getAmount())
                .paymentType(EPaymentType.findByDescription(topUpRequest.getPaymentType()))
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusSeconds(topUpExpiredIn))
                .paymentStatus(EPaymentStatus.PENDING)
                .build();
            log.info("Start - Saving topUp: {}", topUp);
            topUpRepository.save(topUp);
            log.info("End - Saving topUp: {}", topUp);

            log.info("Start - Scheduling topUp expiration: {}", topUp);
            scheduleTopUpExpiration(topUp, topUpExpiredIn);
            log.info("End - Scheduling topUp expiration: {}", topUp);
            return topUp.toTopUpResponse();
        } catch(Exception e) {
            log.error("Error in topUpMyBalance: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TopUpResponse payMyTopUp(String authHeader, String topUpId) {
        try {
            JwtClaim claims = JwtUtil.getClaims(authHeader);
            TopUp topUp = getTopUpById(topUpId);
            if (!topUp.getUserId().equals(claims.getUserId())) {
                throw new RuntimeException(ConstantBash.USER_NOT_AUTHORIZED);
            }
            if (topUp.getPaymentStatus().equals(EPaymentStatus.SUCCESS)) {
                throw new RuntimeException(ConstantBash.TOP_UP_ALREADY_PAID);
            }
            if (topUp.getPaymentStatus().equals(EPaymentStatus.FAILED)) {
                throw new RuntimeException(ConstantBash.TOP_UP_ALREADY_FAILED);
            }
            // nanti disini hit ke 3rd party api
            log.info("Start - Payment topUp: {}", topUp);
            topUp.setPaymentStatus(EPaymentStatus.SUCCESS);
            topUp.setUpdatedAt(LocalDateTime.now());
            log.info("End - Payment topUp: {}", topUp);
            log.info("Start - Saving topUp: {}", topUp);
            topUpRepository.save(topUp);
            log.info("End - Saving topUp: {}", topUp);
            BalanceAdjustedEvent balanceAdjustedEvent = BalanceAdjustedEvent.builder()
                .userId(claims.getUserId())
                .amount(topUp.getAmount())
                .build();
            log.info("Start - Sending balance-adjusted event: {}", topUp);
            kafkaTemplateBalanceAdjusted.send("balance-adjusted", balanceAdjustedEvent);
            log.info("End - Sending balance-adjusted event: {}", topUp);
            return topUp.toTopUpResponse();
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<TopUpResponse> getAll(SearchTopUpRequest request) {
        try {
            if (request.getPage() <= 0) {
                request.setPage(1);
            }
            if (request.getSize() <= 0) {
                request.setSize(10);
            }
            if (request.getCreatedAtMin() != null && request.getCreatedAtMax() != null) {
                if ((request.getCreatedAtMin()).isAfter((request.getCreatedAtMax()))) {
                    throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
                }
            }
            if (request.getUpdatedAtMin() != null && request.getUpdatedAtMax() != null) {
                if ((request.getUpdatedAtMin()).isAfter((request.getUpdatedAtMax()))) {
                    throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
                }
            }
            if (request.getExpiredAtMin() != null && request.getExpiredAtMax() != null) {
                if ((request.getExpiredAtMin()).isAfter((request.getExpiredAtMax()))) {
                    throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
                }
            }
            if (request.getAmountMin() != null && request.getAmountMax() != null) {
                if ((request.getAmountMin()).compareTo((request.getAmountMax())) > 0) {
                    throw new RuntimeException(ConstantBash.MIN_MAX_INVALID);
                }
            }

            Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
            Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
            Specification<TopUp> specification = TopUpSpecification.getSpecification(request);

            log.info("Start - Getting all topUps");
            Page<TopUp> topUps = topUpRepository.findAll(specification, pageable);
            log.info("End - Getting all topUps");
            return topUps.map(TopUp::toTopUpResponse);
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<TopUpResponse> getAllMine(String authHeader, SearchTopUpRequest request) {
        try {
            JwtClaim claims = JwtUtil.getClaims(authHeader);
            request.setUserId(claims.getUserId());
            return getAll(request);
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TopUp getTopUpById(String id) {
        try {
            log.info("Start - Getting topUp by id: {}", id);
            Optional<TopUp> topUpOptional = topUpRepository.findById(id);
            if (topUpOptional.isEmpty()) {
                throw new RuntimeException(DbBash.TOP_UP_NOT_FOUND);
            }
            log.info("End - Getting topUp by id: {}", id);
            return topUpOptional.get();
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TopUpResponse getById(String authHeader, String id) {
        try {
            JwtClaim claims = JwtUtil.getClaims(authHeader);
            log.info("Start - Getting topUp by id: {} as a {}", id, claims.getRoles());
            TopUp topUp = getTopUpById(id);
            if (claims.getRoles().contains(ConstantBash.HAS_ROLE_ADMIN)) {
                log.info("End - Getting topUp by id: {} as a {}", id, claims.getRoles());
                return topUp.toTopUpResponse();
            }
            if (!topUp.getUserId().equals(claims.getUserId())) {
                log.error("Error in getById: {}", ConstantBash.USER_NOT_AUTHORIZED);
                throw new RuntimeException(ConstantBash.USER_NOT_AUTHORIZED);
            }
            log.info("End - Getting topUp by id: {} as a {}", id, claims.getRoles());
            return topUp.toTopUpResponse();
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void scheduleTopUpExpiration(TopUp topUp, Integer expiredIn) {
        String redisKey = "topup:expired:" + topUp.getId();
        log.info("Start - Scheduling topUp expiration: {}", redisKey);
        redisTemplate.opsForValue().set(redisKey, "1", Duration.ofSeconds(expiredIn));
        log.info("End - Scheduling topUp expiration: {}", redisKey);
    }

}
