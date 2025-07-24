package com.learntrad.microservices.tradeprocessor.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.learntrad.microservices.shared.constant.DbBash;
import com.learntrad.microservices.tradeprocessor.entity.TradeProcessedUser;
import com.learntrad.microservices.tradeprocessor.repository.TradeProcessedUserRepository;
import com.learntrad.microservices.tradeprocessor.service.intrface.TradeProcessorUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeProcessorUserServiceImpl implements TradeProcessorUserService {

    private final TradeProcessedUserRepository tradeProcessedUserRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TradeProcessedUser createTradeProcessedUser(TradeProcessedUser tradeProcessedUser) {
        try {
            log.info("Start - Creating trade processed user {}", tradeProcessedUser);
            TradeProcessedUser tradeProcessedUserCreated = tradeProcessedUserRepository.save(tradeProcessedUser);
            log.info("End - Creating trade processed user {}", tradeProcessedUser);
            return tradeProcessedUserCreated;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TradeProcessedUser updateTradeProcessedUser(TradeProcessedUser tradeProcessedUser, String id) {
        try {
            TradeProcessedUser tradeProcessedUserById = getTradeProcessedUserById(id);
            if (tradeProcessedUserById == null) {
                throw new RuntimeException(DbBash.TRADE_PROCESSED_USER_NOT_FOUND);
            }
            tradeProcessedUserById.setCustomerFullname(tradeProcessedUser.getCustomerFullname());
            tradeProcessedUserById.setEmail(tradeProcessedUser.getEmail());
            tradeProcessedUserById.setUsername(tradeProcessedUser.getUsername());
            log.info("Start - Updating trade processed user {}", tradeProcessedUserById);
            TradeProcessedUser tradeProcessedUserUpdated = tradeProcessedUserRepository.save(tradeProcessedUserById);
            log.info("End - Updating trade processed user {}", tradeProcessedUserById);
            return tradeProcessedUserUpdated;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public TradeProcessedUser getTradeProcessedUserById(String userId) {
        try {
            log.info("Start - Getting trade processed user by id {}", userId);
            Optional<TradeProcessedUser> tradeProcessedUserOptional = tradeProcessedUserRepository.findById(userId);
            if (tradeProcessedUserOptional.isEmpty()) {
                return null;
            }
            TradeProcessedUser tradeProcessedUser = tradeProcessedUserOptional.get();
            log.info("End - Getting trade processed user by id {}", userId);
            return tradeProcessedUser;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    

}
