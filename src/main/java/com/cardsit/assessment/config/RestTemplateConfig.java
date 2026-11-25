package com.cardsit.assessment.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * HTTP client configuration for outbound (3rd-party) calls.
 *
 * <p>Provides a shared {@link RestTemplate} bean used by {@code ExternalApiService}.
 * Timeouts prevent hanging threads if JSONPlaceholder (or a future partner API) is slow.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * @param builder Spring Boot builder (already wired with message converters, etc.)
     * @return RestTemplate with 5s connect / 10s read timeouts
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }
}
