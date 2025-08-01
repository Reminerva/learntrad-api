package com.learntrad.microservices.trade.controller;

import java.time.Instant;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.learntrad.microservices.shared.annotation.RequireRoles;
import com.learntrad.microservices.shared.constant.ApiBash;
import com.learntrad.microservices.shared.constant.ConstantBash;
import com.learntrad.microservices.shared.constant.enumerated.ETradeType;
import com.learntrad.microservices.shared.model.response.CommonResponse;
import com.learntrad.microservices.shared.paging.util.PagingUtil;
import com.learntrad.microservices.trade.model.request.TradeRequest;
import com.learntrad.microservices.trade.model.request.TradeRequestExecute;
import com.learntrad.microservices.trade.model.request.search.SearchTradeRequest;
import com.learntrad.microservices.trade.model.response.TradeResponse;
import com.learntrad.microservices.trade.service.intrface.TradeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.TRADE)
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @GetMapping("/{id}")
    @RequireRoles({ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<TradeResponse>> getTradeById(@PathVariable String id) {
        TradeResponse tradeResponse = tradeService.getById(id);
        CommonResponse<TradeResponse> response = CommonResponse.<TradeResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_TRADE_SUCCESS)
                .data(tradeResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    @RequireRoles({ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<List<TradeResponse>>> getAllTrades(
        @RequestParam(required = false) String id,
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) Integer lotMin,
        @RequestParam(required = false) Integer lotMax,
        @RequestParam(required = false) Double priceAtMin,
        @RequestParam(required = false) Double priceAtMax,
        @RequestParam(required = false) Double stopLossAtMin,
        @RequestParam(required = false) Double stopLossAtMax,
        @RequestParam(required = false) Double takeProfitAtMin,
        @RequestParam(required = false) Double takeProfitAtMax,
        @RequestParam(required = false) LocalDateTime createdAtMin,
        @RequestParam(required = false) LocalDateTime createdAtMax,
        @RequestParam(required = false) LocalDateTime updatedAtMin,
        @RequestParam(required = false) LocalDateTime updatedAtMax,
        @RequestParam(required = false) Instant tradeAtMin,
        @RequestParam(required = false) Instant tradeAtMax,
        @RequestParam(required = false) String marketDataType,
        @RequestParam(required = false) String tradeStatus,
        @RequestParam(required = false) String tradeType,
        @RequestParam(required = false) Integer closedAtMin,
        @RequestParam(required = false) Integer closedAtMax,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam(required = false, defaultValue = "tradeAt") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        SearchTradeRequest searchTradeRequest = SearchTradeRequest.builder()
                .id(id)
                .userId(userId)
                .lotMin(lotMin)
                .lotMax(lotMax)
                .priceAtMin(priceAtMin)
                .priceAtMax(priceAtMax)
                .stopLossAtMin(stopLossAtMin)
                .stopLossAtMax(stopLossAtMax)
                .takeProfitAtMin(takeProfitAtMin)
                .takeProfitAtMax(takeProfitAtMax)
                .createdAtMin(createdAtMin)
                .createdAtMax(createdAtMax)
                .updatedAtMin(updatedAtMin)
                .updatedAtMax(updatedAtMax)
                .tradeAtMin(tradeAtMin)
                .tradeAtMax(tradeAtMax)
                .marketDataType(marketDataType)
                .tradeStatus(tradeStatus)
                .tradeType(tradeType)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .closedAtMin(closedAtMin)
                .closedAtMax(closedAtMax)
                .build();
        Page<TradeResponse> trades = tradeService.getAllTrades(searchTradeRequest);
        CommonResponse<List<TradeResponse>> response = CommonResponse.<List<TradeResponse>>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_TRADES_SUCCESS)
                .data(trades.getContent())
                .paging(PagingUtil.pageToPagingResponse(trades))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<TradeResponse>> createTrade(HttpServletRequest httpServletRequest, @Valid @RequestBody TradeRequest tradeRequest) {
        if (ETradeType.findByDescription(tradeRequest.getTradeType()).equals(ETradeType.MARKET_EXECUTION_BUY) || ETradeType.findByDescription(tradeRequest.getTradeType()).equals(ETradeType.MARKET_EXECUTION_SELL)) {
            throw new RuntimeException(ApiBash.MARKET_EXECUTION_TRADE_NOT_ALLOWED);
        };
        String authHeader = httpServletRequest.getHeader("Authorization");
        TradeResponse tradeResponse = tradeService.createMine(authHeader, tradeRequest);
        CommonResponse<TradeResponse> response = CommonResponse.<TradeResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_TRADE_SUCCESS)
                .data(tradeResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/market-execute")
    @ResponseStatus(HttpStatus.CREATED)
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<TradeResponse>> createTradeMarketExecute(HttpServletRequest httpServletRequest, @Valid @RequestBody TradeRequestExecute tradeRequestExec) {
        TradeRequest tradeRequest = TradeRequest.builder()
                .marketDataType(tradeRequestExec.getMarketDataType())
                .tradeType(tradeRequestExec.getTradeType())
                .tradeAt(tradeRequestExec.getTradeAt())
                .priceAt(null)
                .lot(tradeRequestExec.getLot())
                .stopLossAt(tradeRequestExec.getStopLossAt())
                .takeProfitAt(tradeRequestExec.getTakeProfitAt())
                .expiredAt(tradeRequestExec.getExpiredAt())
                .build();
        String authHeader = httpServletRequest.getHeader("Authorization");
        TradeResponse tradeResponse = tradeService.createMine(authHeader, tradeRequest);
        CommonResponse<TradeResponse> response = CommonResponse.<TradeResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message(ApiBash.CREATE_TRADE_SUCCESS)
                .data(tradeResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mine/{id}")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<TradeResponse>> getMineTradeById(HttpServletRequest httpServletRequest, @PathVariable String id) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        TradeResponse tradeResponse = tradeService.getMine(authHeader, id);
        CommonResponse<TradeResponse> response = CommonResponse.<TradeResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_TRADE_SUCCESS)
                .data(tradeResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/mine")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<List<TradeResponse>>> getAllMineTrades(
        HttpServletRequest httpServletRequest,
        @RequestParam(required = false) String id,
        @RequestParam(required = false) Integer lotMin,
        @RequestParam(required = false) Integer lotMax,
        @RequestParam(required = false) Double priceAtMin,
        @RequestParam(required = false) Double priceAtMax,
        @RequestParam(required = false) Double stopLossAtMin,
        @RequestParam(required = false) Double stopLossAtMax,
        @RequestParam(required = false) Double takeProfitAtMin,
        @RequestParam(required = false) Double takeProfitAtMax,
        @RequestParam(required = false) LocalDateTime createdAtMin,
        @RequestParam(required = false) LocalDateTime createdAtMax,
        @RequestParam(required = false) LocalDateTime updatedAtMin,
        @RequestParam(required = false) LocalDateTime updatedAtMax,
        @RequestParam(required = false) Instant tradeAtMin,
        @RequestParam(required = false) Instant tradeAtMax,
        @RequestParam(required = false) String marketDataType,
        @RequestParam(required = false) String tradeStatus,
        @RequestParam(required = false) String tradeType,
        @RequestParam(required = false) Integer closedAtMin,
        @RequestParam(required = false) Integer closedAtMax,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size,
        @RequestParam(required = false, defaultValue = "tradeAt") String sortBy,
        @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        SearchTradeRequest searchTradeRequest = SearchTradeRequest.builder()
                .id(id)
                .lotMin(lotMin)
                .lotMax(lotMax)
                .priceAtMin(priceAtMin)
                .priceAtMax(priceAtMax)
                .stopLossAtMin(stopLossAtMin)
                .stopLossAtMax(stopLossAtMax)
                .takeProfitAtMin(takeProfitAtMin)
                .takeProfitAtMax(takeProfitAtMax)
                .createdAtMin(createdAtMin)
                .createdAtMax(createdAtMax)
                .updatedAtMin(updatedAtMin)
                .updatedAtMax(updatedAtMax)
                .tradeAtMin(tradeAtMin)
                .tradeAtMax(tradeAtMax)
                .marketDataType(marketDataType)
                .tradeStatus(tradeStatus)
                .tradeType(tradeType)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .closedAtMin(closedAtMin)
                .closedAtMax(closedAtMax)
                .build();
        Page<TradeResponse> trades = tradeService.getAllMine(authHeader, searchTradeRequest);
        CommonResponse<List<TradeResponse>> response = CommonResponse.<List<TradeResponse>>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.GET_ALL_TRADES_SUCCESS)
                .data(trades.getContent())
                .paging(PagingUtil.pageToPagingResponse(trades))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/mine/{id}")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<TradeResponse>> updateTrade(HttpServletRequest httpServletRequest, @Valid @PathVariable String id, @RequestBody TradeRequest tradeRequest) {
        tradeRequest.setPriceAt(null);
        tradeRequest.setTradeAt(null);
        tradeRequest.setLot(null);
        tradeRequest.setTradeType(null);
        String authHeader = httpServletRequest.getHeader("Authorization");
        TradeResponse tradeResponse = tradeService.updateMine(authHeader, id, tradeRequest);
        CommonResponse<TradeResponse> response = CommonResponse.<TradeResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.UPDATE_TRADE_SUCCESS)
                .data(tradeResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/mine/{id}/cancel")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER})
    public ResponseEntity<CommonResponse<TradeResponse>> cancelTrade(HttpServletRequest httpServletRequest, @PathVariable String id) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        TradeResponse tradeResponse = tradeService.cancelTrade(authHeader, id);
        CommonResponse<TradeResponse> response = CommonResponse.<TradeResponse>builder()
                .status(HttpStatus.OK.value())
                .message(ApiBash.CANCEL_TRADE_SUCCESS)
                .data(tradeResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
