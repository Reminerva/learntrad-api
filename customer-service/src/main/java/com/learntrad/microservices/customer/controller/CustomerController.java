package com.learntrad.microservices.customer.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.learntrad.microservices.customer.model.request.CustomerRequest;
import com.learntrad.microservices.customer.model.request.search.SearchCustomerRequest;
import com.learntrad.microservices.customer.model.response.CustomerResponse;
import com.learntrad.microservices.customer.service.intrface.CustomerService;
import com.learntrad.microservices.shared.annotation.RequireRoles;
import com.learntrad.microservices.shared.model.response.CommonResponse;
import com.learntrad.microservices.shared.paging.util.PagingUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.learntrad.microservices.shared.constant.ApiBash;
import com.learntrad.microservices.shared.constant.ConstantBash;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.CUSTOMER)
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<CustomerResponse>> createCustomer(HttpServletRequest authHeader, @Valid @RequestBody CustomerRequest customerRequest) {
        String authHeaderStr = authHeader.getHeader("Authorization");
        CustomerResponse customerResponse = customerService.createCustomer(authHeaderStr, customerRequest);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_CUSTOMER_SUCCESS)
                .data(customerResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @RequireRoles({ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<List<CustomerResponse>>> getAllCustomers(
        @RequestParam(required = false) String fullname,
        @RequestParam(required = false) String address,
        @RequestParam(required = false) LocalDate birthDateMin,
        @RequestParam(required = false) LocalDate birthDateMax,
        @RequestParam(required = false) Double balanceMin,
        @RequestParam(required = false) Double balanceMax,
        @RequestParam(required = false) LocalDateTime createdAtMin,
        @RequestParam(required = false) LocalDateTime createdAtMax,
        @RequestParam(required = false) LocalDateTime updatedAtMin,
        @RequestParam(required = false) LocalDateTime updatedAtMax,
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) Boolean isActive,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam(required = false, defaultValue = "fullname") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction

    ) {
        SearchCustomerRequest searchCustomerRequest = SearchCustomerRequest.builder()
                .fullname(fullname)
                .address(address)
                .birthDateMin(birthDateMin)
                .birthDateMax(birthDateMax)
                .balanceMin(balanceMin)
                .balanceMax(balanceMax)
                .createdAtMin(createdAtMin)
                .createdAtMax(createdAtMax)
                .updatedAtMin(updatedAtMin)
                .updatedAtMax(updatedAtMax)
                .userId(userId)
                .isActive(isActive)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();
        Page<CustomerResponse> customers = customerService.getAllCustomers(searchCustomerRequest);
        CommonResponse<List<CustomerResponse>> response = CommonResponse.<List<CustomerResponse>>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_CUSTOMERS_SUCCESS)
                .data(customers.getContent())
                .paging(PagingUtil.pageToPagingResponse(customers))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @RequireRoles({ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<CustomerResponse>> getCustomerById(HttpServletRequest authHeader, @PathVariable String id) {
        CustomerResponse customer = customerService.getById(id);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_CUSTOMER_SUCCESS)
                .data(customer)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @RequireRoles({ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<CustomerResponse>> updateCustomer(@PathVariable String id,  @Valid @RequestBody CustomerRequest customerRequest) {
        CustomerResponse customer = customerService.updateCustomer(id, customerRequest);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_CUSTOMER_SUCCESS)
                .data(customer)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @RequireRoles({ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<CustomerResponse>> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.DELETE_CUSTOMER_SUCCESS)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<CustomerResponse>> getMe(HttpServletRequest authHeader) {
        String authHeaderStr = authHeader.getHeader("Authorization");
        CustomerResponse customer = customerService.getMe(authHeaderStr);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_CUSTOMER_SUCCESS)
                .data(customer)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<CustomerResponse>> updateMe(HttpServletRequest authHeader,  @Valid @RequestBody CustomerRequest customerRequest) {
        String authHeaderStr = authHeader.getHeader("Authorization");
        CustomerResponse customer = customerService.updateMe(authHeaderStr, customerRequest);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_CUSTOMER_SUCCESS)
                .data(customer)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<CustomerResponse>> deleteMe(HttpServletRequest authHeader) {
        String authHeaderStr = authHeader.getHeader("Authorization");
        customerService.deleteMe(authHeaderStr);
        CommonResponse<CustomerResponse> response = CommonResponse.<CustomerResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.DELETE_CUSTOMER_SUCCESS)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
