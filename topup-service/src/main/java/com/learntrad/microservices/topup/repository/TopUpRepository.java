package com.learntrad.microservices.topup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.learntrad.microservices.topup.entity.TopUp;

@Repository
public interface TopUpRepository extends JpaRepository<TopUp, String>, JpaSpecificationExecutor<TopUp> {
    List<TopUp> findByUserId(String userId);
}
