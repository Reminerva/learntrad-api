package com.learntrad.microservices.customer.model.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerRequest {

    @NotBlank(message = "fullname is required")
    private String fullname;
    @NotBlank(message = "address is required")
    private String address;
    @NotNull(message = "birthDate is required")
    private LocalDate birthDate;

}

