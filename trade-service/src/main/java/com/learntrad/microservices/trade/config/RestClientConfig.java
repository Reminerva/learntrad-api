package com.learntrad.microservices.trade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.learntrad.microservices.trade.client.CustomerClient;
import com.learntrad.microservices.trade.client.MarketDataClient;

import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @Value("${marketdata.service.url}")
    private String marketDataServiceUrl;

    private final ObservationRegistry registry;

    @Bean
    public CustomerClient customerClient() {
        RestClient restClient = RestClient.builder()
            .baseUrl(customerServiceUrl)
            .requestFactory(getClientRequirestFactory())
            .observationRegistry(registry)
            .build();

        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(CustomerClient.class);
    }

    @Bean
    public MarketDataClient marketDataClient() {
        RestClient restClient = RestClient.builder()
            .baseUrl(marketDataServiceUrl)
            .requestFactory(getClientRequirestFactory())
            .observationRegistry(registry)
            .build();

        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(MarketDataClient.class);
    }

    private ClientHttpRequestFactory getClientRequirestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(10_000);
        return factory;
    }

}
