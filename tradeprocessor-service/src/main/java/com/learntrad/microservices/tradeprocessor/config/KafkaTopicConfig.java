package com.learntrad.microservices.tradeprocessor.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic tradeProcessedTopic() {
        return new NewTopic("trade-processed", 1, (short) 1);
    }
}
