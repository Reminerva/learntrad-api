package com.learntrad.microservices.tradeprocessor.service.intrface;

import com.learntrad.microservices.tradeprocessor.entity.TradeProcessedUser;

public interface TradeProcessorUserService {

    TradeProcessedUser createTradeProcessedUser(TradeProcessedUser tradeProcessedUser);
    TradeProcessedUser updateTradeProcessedUser(TradeProcessedUser tradeProcessedUser, String id);
    TradeProcessedUser getTradeProcessedUserById(String userId);

}
