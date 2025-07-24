package com.learntrad.microservices.tradeprocessor.service.intrface;

import com.learntrad.microservices.marketrealtime.event.RealtimeDataEvent;
import com.learntrad.microservices.trade.event.TradeEditedEvent;
import com.learntrad.microservices.trade.event.TradePlacedEvent;
import com.learntrad.microservices.trade.event.TradeStatusUpdatedEvent;
import com.learntrad.microservices.tradeprocessor.event.TradeProcessedEvent;

public interface TradeProcessorService {
    void listenTradePlaced(TradePlacedEvent tradePlacedEvent);
    void listenTradeEdited(TradeEditedEvent tradeEditedEvent);
    void listenRealtimeData(RealtimeDataEvent newRealtimeDataEvent);
    void sendTradeProcessedEvent(TradeProcessedEvent tradeProcessedEvent, Boolean shouldDelete);
    void listenTradeCanceled(TradeStatusUpdatedEvent tradeStatusUpdatedEvent);
}
