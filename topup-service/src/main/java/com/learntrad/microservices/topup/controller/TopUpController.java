package com.learntrad.microservices.topup.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learntrad.microservices.shared.annotation.RequireRoles;
import com.learntrad.microservices.shared.constant.ApiBash;
import com.learntrad.microservices.shared.constant.ConstantBash;
import com.learntrad.microservices.shared.model.response.CommonResponse;
import com.learntrad.microservices.shared.paging.util.PagingUtil;
import com.learntrad.microservices.topup.model.request.TopUpRequest;
import com.learntrad.microservices.topup.model.request.search.SearchTopUpRequest;
import com.learntrad.microservices.topup.model.response.TopUpResponse;
import com.learntrad.microservices.topup.service.intrface.TopUpService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.TOP_UP)
@RequiredArgsConstructor
public class TopUpController {

    private final TopUpService topUpService;

    @GetMapping("/{id}")
    @RequireRoles({ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<TopUpResponse>> getTopUpById(
        @RequestHeader("Authorization") String authHeader, 
        @PathVariable String id
    ) {
        TopUpResponse topUpResponse = topUpService.getById(authHeader, id);
        CommonResponse<TopUpResponse> response = CommonResponse.<TopUpResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_TOP_UP_SUCCESS)
                .data(topUpResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping()
    @RequireRoles({ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<List<TopUpResponse>>> getAll(
        @RequestParam(required = false) BigDecimal amountMin,
        @RequestParam(required = false) BigDecimal amountMax,
        @RequestParam(required = false) String paymentStatus,
        @RequestParam(required = false) String paymentType,
        @RequestParam(required = false) LocalDateTime expiredAtMin,
        @RequestParam(required = false) LocalDateTime expiredAtMax,
        @RequestParam(required = false) LocalDateTime createdAtMin,
        @RequestParam(required = false) LocalDateTime createdAtMax,
        @RequestParam(required = false) LocalDateTime updatedAtMin,
        @RequestParam(required = false) LocalDateTime updatedAtMax,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction
    ) {

        SearchTopUpRequest request = SearchTopUpRequest.builder()
                .amountMin(amountMin)
                .amountMax(amountMax)
                .paymentStatus(paymentStatus)
                .paymentType(paymentType)
                .expiredAtMin(expiredAtMin)
                .expiredAtMax(expiredAtMax)
                .createdAtMin(createdAtMin)
                .createdAtMax(createdAtMax)
                .updatedAtMin(updatedAtMin)
                .updatedAtMax(updatedAtMax)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();
        Page<TopUpResponse> topUpResponse = topUpService.getAll(request);
        CommonResponse<List<TopUpResponse>> response = CommonResponse.<List<TopUpResponse>>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_TOP_UPS_SUCCESS)
                .data(topUpResponse.getContent())
                .paging(PagingUtil.pageToPagingResponse(topUpResponse))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/me")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<TopUpResponse>> topUpMe(
        @RequestHeader("Authorization") String authHeader, 
        @Valid @RequestBody TopUpRequest topUpRequest
    ) {
        TopUpResponse TopUpResponse = topUpService.topUpMyBalance(authHeader, topUpRequest);
        CommonResponse<TopUpResponse> response = CommonResponse.<TopUpResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_CUSTOMER_SUCCESS)
                .data(TopUpResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mine")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<List<TopUpResponse>>> getAllMine(
        @RequestParam(required = false) BigDecimal amountMin,
        @RequestParam(required = false) BigDecimal amountMax,
        @RequestParam(required = false) String paymentStatus,
        @RequestParam(required = false) String paymentType,
        @RequestParam(required = false) LocalDateTime expiredAtMin,
        @RequestParam(required = false) LocalDateTime expiredAtMax,
        @RequestParam(required = false) LocalDateTime createdAtMin,
        @RequestParam(required = false) LocalDateTime createdAtMax,
        @RequestParam(required = false) LocalDateTime updatedAtMin,
        @RequestParam(required = false) LocalDateTime updatedAtMax,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestHeader("Authorization") String authHeader
    ) {
        SearchTopUpRequest request = SearchTopUpRequest.builder()
                .amountMin(amountMin)
                .amountMax(amountMax)
                .paymentStatus(paymentStatus)
                .paymentType(paymentType)
                .expiredAtMin(expiredAtMin)
                .expiredAtMax(expiredAtMax)
                .createdAtMin(createdAtMin)
                .createdAtMax(createdAtMax)
                .updatedAtMin(updatedAtMin)
                .updatedAtMax(updatedAtMax)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();
        Page<TopUpResponse> topUpResponse = topUpService.getAllMine(authHeader, request);
        CommonResponse<List<TopUpResponse>> response = CommonResponse.<List<TopUpResponse>>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_TOP_UPS_SUCCESS)
                .data(topUpResponse.getContent())
                .paging(PagingUtil.pageToPagingResponse(topUpResponse))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/mine/{id}")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<TopUpResponse>> getMine(
        @RequestHeader("Authorization") String authHeader, 
        @PathVariable String id
    ) {
        TopUpResponse topUpResponse = topUpService.getById(authHeader, id);
        CommonResponse<TopUpResponse> response = CommonResponse.<TopUpResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_TOP_UP_SUCCESS)
                .data(topUpResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("mine/{id}/pay")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<TopUpResponse>> payMine(
        @RequestHeader("Authorization") String authHeader, 
        @PathVariable String id
    ) {
        TopUpResponse topUpResponse = topUpService.payMyTopUp(authHeader, id);
        CommonResponse<TopUpResponse> response = CommonResponse.<TopUpResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.TOP_UP_PAYMENT_SUCCESS)
                .data(topUpResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
