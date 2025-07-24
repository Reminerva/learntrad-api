package com.learntrad.microservices.tradeprocessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learntrad.microservices.tradeprocessor.entity.TradeProcessedUser;

@Repository
public interface TradeProcessedUserRepository extends JpaRepository<TradeProcessedUser, String> {

}
