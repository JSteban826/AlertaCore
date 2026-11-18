package com.alertacore.incidentservice.infrastructure
        .driver_adapter.resource.config;


import com.alertacore.incidentservice.domain.model.gateway.ServicioRecursosGateway;
import com.alertacore.incidentservice.infrastructure.driver_adapter.resource.ResourceServiceHttpAdapter;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ResourceServiceConfig {

    @Bean("resourceServiceRestClient")
    public RestClient resourceServiceRestClient(
            @Value(
                    "${services.resource.base-url:"
                            + "http://localhost:8082}"
            )
            String baseUrl
    ) {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(
                Duration.ofSeconds(3)
        );

        requestFactory.setReadTimeout(
                Duration.ofSeconds(5)
        );

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public ServicioRecursosGateway servicioRecursosGateway(
            @Qualifier("resourceServiceRestClient")
            RestClient restClient
    ) {
        return new ResourceServiceHttpAdapter(
                restClient
        );
    }

}