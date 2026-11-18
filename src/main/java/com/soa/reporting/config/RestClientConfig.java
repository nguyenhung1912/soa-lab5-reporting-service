package com.soa.reporting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration class for HTTP RestClient bean.
 */
@Configuration
public class RestClientConfig {

    /**
     * Creates a RestClient bean for service calls.
     *
     * @return new RestClient instance
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}
