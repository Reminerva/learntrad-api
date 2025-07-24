package com.learntrad.microservices.trade.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic tradePlacedTopic() {
        return new NewTopic("trade-placed", 1, (short) 1);
    }

    @Bean
    public NewTopic tradeEditedTopic() {
        return new NewTopic("trade-edited", 1, (short) 1);
    }

    @Bean
    public NewTopic tradeStatusUpdateTopic() {
        return new NewTopic("trade-status-update", 1, (short) 1);
    }

    @Bean
    public NewTopic tradeCanceled() {
        return new NewTopic("trade-canceled", 1, (short) 1);
    }

    @Bean
    public NewTopic balanceAdjusted() {
        return new NewTopic("balance-adjusted", 1, (short) 1);
    }
}
