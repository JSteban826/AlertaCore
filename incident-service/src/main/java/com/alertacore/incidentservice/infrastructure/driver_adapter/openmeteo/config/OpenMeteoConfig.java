package com.alertacore.incidentservice.infrastructure.driver_adapter.openmeteo.config;

import com.alertacore.incidentservice.domain.model.gateway.ServicioMeteorologicoGateway;
import com.alertacore.incidentservice.infrastructure.driver_adapter.openmeteo.OpenMeteoAdapter;
import com.alertacore.incidentservice.infrastructure.driver_adapter.openmeteo.ServicioMeteorologicoCacheado;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import com.alertacore.incidentservice.infrastructure.driver_adapter.openmeteo.ServicioMeteorologicoResiliente;

import java.time.Duration;

@Configuration
@EnableCaching
public class OpenMeteoConfig {

    private static final Duration TIMEOUT_CONEXION =
            Duration.ofSeconds(3);

    private static final Duration TIMEOUT_LECTURA =
            Duration.ofSeconds(5);

    @Bean("openMeteoRestClient")
    public RestClient openMeteoRestClient() {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(
                TIMEOUT_CONEXION
        );

        requestFactory.setReadTimeout(
                TIMEOUT_LECTURA
        );

        return RestClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager =
                new CaffeineCacheManager(
                        "condicionesMeteorologicas",
                        "geocodificaciones"
                );

        cacheManager.setCacheSpecification(
                "maximumSize=500,expireAfterWrite=10m"
        );

        return cacheManager;
    }

    @Bean("openMeteoAdapter")
    public ServicioMeteorologicoGateway openMeteoAdapter(
            @Qualifier("openMeteoRestClient")
            RestClient restClient
    ) {
        return new OpenMeteoAdapter(
                restClient
        );
    }

    @Bean("servicioMeteorologicoResiliente")
    public ServicioMeteorologicoGateway servicioMeteorologicoResiliente(
            @Qualifier("openMeteoAdapter")
            ServicioMeteorologicoGateway openMeteoAdapter
    ) {
        return new ServicioMeteorologicoResiliente(
                openMeteoAdapter
        );
    }

    @Bean("servicioMeteorologicoCacheado")
    @Primary
    public ServicioMeteorologicoGateway servicioMeteorologicoCacheado(
            @Qualifier("servicioMeteorologicoResiliente")
            ServicioMeteorologicoGateway servicioResiliente
    ) {
        return new ServicioMeteorologicoCacheado(
                servicioResiliente
        );
    }

}