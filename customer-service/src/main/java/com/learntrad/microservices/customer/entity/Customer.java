package com.learntrad.microservices.customer.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.learntrad.microservices.shared.constant.DbBash;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = DbBash.CUSTOMER_TABLE)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String fullname;
    @Column
    private String address;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column
    private BigDecimal balance;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;
    @Column(name = "is_active")
    private Boolean isActive;

}
