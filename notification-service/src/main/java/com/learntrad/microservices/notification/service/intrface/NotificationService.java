package com.learntrad.microservices.notification.service.intrface;

import com.learntrad.microservices.trade.event.TradeEditedEvent;
import com.learntrad.microservices.trade.event.TradePlacedEvent;
import com.learntrad.microservices.trade.event.TradeStatusUpdatedEvent;

public interface NotificationService {

    void listenTradePlaced(TradePlacedEvent event);
    void listenTradeEdited(TradeEditedEvent event);
    void listenTradeStatusUpdated(TradeStatusUpdatedEvent event);
    void listenTradeCanceled(TradeStatusUpdatedEvent event);
}
