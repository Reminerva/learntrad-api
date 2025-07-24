package com.learntrad.microservices.trade.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@RequiredArgsConstructor
public class ObservationConfig {

    private final KafkaTemplate kafkaTemplate;
    private final ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory;

    @PostConstruct
    public void setObservationForKafkaTemplate() {
        kafkaListenerContainerFactory.getContainerProperties().setObservationEnabled(true);
        kafkaTemplate.setObservationEnabled(true);
    }

    @Bean
    ObservedAspect observedAspect(ObservationRegistry registry) {
        return new ObservedAspect(registry);
    }
}