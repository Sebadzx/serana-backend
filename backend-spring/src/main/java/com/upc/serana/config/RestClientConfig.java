package com.upc.serana.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Issue 6: Reemplaza WebClientConfig (stack reactivo WebFlux) por RestClientConfig
 * con RestClient síncrono (Spring 6.1 / Boot 3.2). Elimina la dependencia de
 * spring-boot-starter-webflux y la inconsistencia de .block() en contexto servlet.
 *
 * El timeout se configura a nivel de la fábrica HTTP para garantizar que las
 * llamadas al microservicio Python no bloqueen los hilos de Tomcat indefinidamente.
 */
@Configuration
public class RestClientConfig {

    @Value("${ml-service.url:http://localhost:8001/api/v1}")
    private String mlServiceUrl;

    @Value("${ml-service.timeout-seconds:4}")
    private int timeoutSeconds;

    @Bean
    public RestClient mlRestClient() {
        Duration timeout = Duration.ofSeconds(timeoutSeconds);
        ClientHttpRequestFactory factory = ClientHttpRequestFactories.get(
                ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(timeout)
                        .withReadTimeout(timeout)
        );

        return RestClient.builder()
                .baseUrl(mlServiceUrl)
                .requestFactory(factory)
                .build();
    }
}

