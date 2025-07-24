package com.learntrad.microservices.customer.model.request.search;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCustomerRequest {

    private String fullname;
    private String address;
    private LocalDate birthDateMin;
    private LocalDate birthDateMax;
    private Double balanceMin;
    private Double balanceMax;
    private LocalDateTime createdAtMin;
    private LocalDateTime createdAtMax;
    private LocalDateTime updatedAtMin;
    private LocalDateTime updatedAtMax;
    private String userId;
    private Boolean isActive;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
