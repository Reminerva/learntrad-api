package com.learntrad.microservices.customer.service.intrface;

import org.springframework.data.domain.Page;

import com.learntrad.microservices.customer.entity.Customer;
import com.learntrad.microservices.customer.model.request.CustomerRequest;
import com.learntrad.microservices.customer.model.request.search.SearchCustomerRequest;
import com.learntrad.microservices.customer.model.response.CustomerResponse;
import com.learntrad.microservices.topup.event.BalanceAdjustedEvent;

public interface CustomerService {

    CustomerResponse createCustomer(String authHeader,CustomerRequest customerRequest);
    CustomerResponse getById(String id);
    Customer getCustomerById(String id);
    Page<CustomerResponse> getAllCustomers(SearchCustomerRequest searchCustomerRequest);
    CustomerResponse updateCustomer(String id, CustomerRequest customerRequest);
    void deleteCustomer(String id);

    CustomerResponse getMe(String authHeader);
    CustomerResponse updateMe(String authHeader, CustomerRequest customerRequest);
    void deleteMe(String authHeader);

    void updateMyBalance(BalanceAdjustedEvent balanceAdjustedEvent);

}
