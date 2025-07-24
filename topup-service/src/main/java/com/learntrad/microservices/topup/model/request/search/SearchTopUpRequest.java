package com.learntrad.microservices.topup.model.request.search;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchTopUpRequest {

    private String userId;
    private BigDecimal amountMin;
    private BigDecimal amountMax;
    private String paymentStatus;
    private String paymentType;
    private LocalDateTime expiredAtMin;
    private LocalDateTime expiredAtMax;
    private LocalDateTime createdAtMin;
    private LocalDateTime createdAtMax;
    private LocalDateTime updatedAtMin;
    private LocalDateTime updatedAtMax;
    
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;

}
