package com.learntrad.microservices.customer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic balanceAdjusted() {
        return new NewTopic("balance-adjusted", 1, (short) 1);
    }
}
