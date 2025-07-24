package com.learntrad.microservices.topup.listener;

import java.util.Optional;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.learntrad.microservices.shared.constant.enumerated.EPaymentStatus;
import com.learntrad.microservices.topup.entity.TopUp;
import com.learntrad.microservices.topup.repository.TopUpRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private final TopUpRepository topUpRepository;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, TopUpRepository topUpRepository) {
        super(listenerContainer);
        this.topUpRepository = topUpRepository;
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("Redis key expired: {}", expiredKey);

        if (expiredKey.contains("topup:expired:")) {
            String topUpId = expiredKey.substring(expiredKey.length() - 36, expiredKey.length());
            Optional<TopUp> optionalTopUp = topUpRepository.findById(topUpId);
            optionalTopUp.ifPresent(topUp -> {
                if (topUp.getPaymentStatus().equals(EPaymentStatus.PENDING)) {
                    topUp.setPaymentStatus(EPaymentStatus.FAILED);
                    topUpRepository.save(topUp);
                    log.info("TopUp {} marked as FAILED by Redis expiration", topUpId);
                }
            });
        }
    }
}
