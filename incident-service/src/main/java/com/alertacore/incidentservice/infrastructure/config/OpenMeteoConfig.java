package com.alertacore.incidentservice.infrastructure.config;

import com.alertacore.incidentservice.domain.port.out.ServicioMeteorologicoPort;
import com.alertacore.incidentservice.infrastructure.client.openmeteo.OpenMeteoAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenMeteoConfig {

    @Bean("openMeteoRestClient")
    public RestClient openMeteoRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .build();
    }

    @Bean
    public ServicioMeteorologicoPort servicioMeteorologicoPort(
            @Qualifier("openMeteoRestClient")
            RestClient restClient
    ) {
        return new OpenMeteoAdapter(
                restClient
        );
    }
}