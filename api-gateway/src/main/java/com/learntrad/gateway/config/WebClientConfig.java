package com.learntrad.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.learntrad.gateway.client.AuthClient;

import io.micrometer.observation.ObservationRegistry;
import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {

    @Value("${auth.service.url}")
    private String authServiceUrl;

    private final ObservationRegistry registry;

    public WebClientConfig(ObservationRegistry registry) {
        this.registry = registry;
    }

    @Bean
    public AuthClient authClient() {
        WebClient webClient = WebClient.builder()
            .baseUrl(authServiceUrl)
            .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
            .observationRegistry(registry)
            .build();

        var webClientAdapter = WebClientAdapter.create(webClient);
        var proxyFactory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();
        return proxyFactory.createClient(AuthClient.class);
    }

    private HttpClient getHttpClient() {
        return HttpClient.create(ConnectionProvider.newConnection())
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
            .responseTimeout(java.time.Duration.ofMillis(10_000));
    }
}
