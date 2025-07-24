package com.learntrad.microservices.tradeprocessor.entity;

import java.util.List;

import com.learntrad.microservices.shared.constant.DbBash;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = DbBash.TRADE_PROCESSED_USER_TABLE)
public class TradeProcessedUser {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "customer_fullname")
    private String customerFullname;

    @OneToMany(mappedBy = "tradeProcessedUser")
    private List<TradeProcessed> tradeProcessed;

}
