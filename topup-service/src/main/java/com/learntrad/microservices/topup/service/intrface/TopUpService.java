package com.learntrad.microservices.topup.service.intrface;

import org.springframework.data.domain.Page;

import com.learntrad.microservices.topup.entity.TopUp;
import com.learntrad.microservices.topup.model.request.TopUpRequest;
import com.learntrad.microservices.topup.model.request.search.SearchTopUpRequest;
import com.learntrad.microservices.topup.model.response.TopUpResponse;

public interface TopUpService {

    TopUpResponse topUpMyBalance(String authHeader, TopUpRequest topUpRequest);
    TopUpResponse payMyTopUp(String authHeader, String topUpId);
    Page<TopUpResponse> getAll(SearchTopUpRequest request);
    Page<TopUpResponse> getAllMine(String authHeader, SearchTopUpRequest request);
    TopUp getTopUpById(String id);
    TopUpResponse getById(String authHeader, String id);
    void scheduleTopUpExpiration(TopUp topUp, Integer expiredIn);

}
