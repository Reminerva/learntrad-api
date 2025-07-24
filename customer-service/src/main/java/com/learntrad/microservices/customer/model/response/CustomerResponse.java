package com.learntrad.microservices.customer.model.response;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerResponse {

    private String id;
    private String fullname;
    private String address;
    private String birthDate;
    private BigDecimal balance;
    private String createdAt;
    private String updatedAt;
    private String userId;
    private Boolean isActive;
}
