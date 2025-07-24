package com.learntrad.microservices.marketrealtime.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic realtimeDataTopic() {
        return new NewTopic("realtime-data", 1, (short) 1);
    }

}
