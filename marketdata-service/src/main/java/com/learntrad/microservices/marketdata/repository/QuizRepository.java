package com.learntrad.microservices.marketdata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learntrad.microservices.marketdata.entity.QuizEntity;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, String> {
    Optional<QuizEntity> findByUserIdAndId(String userId, String id);
    List<QuizEntity> findAllByUserId(String userId);
}
