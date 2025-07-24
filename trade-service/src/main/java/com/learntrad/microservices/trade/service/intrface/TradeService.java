package com.learntrad.microservices.trade.service.intrface;

import org.springframework.data.domain.Page;

import com.learntrad.microservices.trade.entity.Trade;
import com.learntrad.microservices.trade.model.request.TradeRequest;
import com.learntrad.microservices.trade.model.request.search.SearchTradeRequest;
import com.learntrad.microservices.trade.model.response.TradeResponse;
import com.learntrad.microservices.tradeprocessor.event.TradeProcessedEvent;

public interface TradeService {

    Trade getTradeById(String id);
    Page<TradeResponse> getAllTrades(SearchTradeRequest searchTradeRequest);
    TradeResponse getById(String id);
    
    TradeResponse getMine(String authHeader, String id);
    Page<TradeResponse> getAllMine(String authHeader, SearchTradeRequest searchTradeRequest);
    TradeResponse updateMine(String authHeader, String id, TradeRequest tradeRequest);
    TradeResponse createMine(String authHeader, TradeRequest tradeRequest);

    void updateTradeStatus(TradeProcessedEvent tradeProcessedEvent);
    TradeResponse cancelTrade(String authHeader, String id);

}
