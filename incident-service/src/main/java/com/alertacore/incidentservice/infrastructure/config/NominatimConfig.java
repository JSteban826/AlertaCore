package com.alertacore.incidentservice.infrastructure.config;

import com.alertacore.incidentservice.domain.port.out.ServicioGeocodificacionPort;
import com.alertacore.incidentservice.infrastructure.client.nominatim.NominatimAdapter;
import com.alertacore.incidentservice.infrastructure.client.nominatim.ServicioGeocodificacionCacheado;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class NominatimConfig {

    private static final Duration TIMEOUT_CONEXION =
            Duration.ofSeconds(3);

    private static final Duration TIMEOUT_LECTURA =
            Duration.ofSeconds(5);

    @Bean("nominatimRestClient")
    public RestClient nominatimRestClient() {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(
                TIMEOUT_CONEXION
        );

        requestFactory.setReadTimeout(
                TIMEOUT_LECTURA
        );

        return RestClient.builder()
                .baseUrl(
                        "https://nominatim.openstreetmap.org"
                )
                .requestFactory(requestFactory)
                .defaultHeader(
                        HttpHeaders.USER_AGENT,
                        "AlertaCore-IncidentService/1.0 "
                                + "(contacto: equipo-alertacore)"
                )
                .defaultHeader(
                        HttpHeaders.ACCEPT_LANGUAGE,
                        "es-CO,es;q=0.9"
                )
                .build();
    }

    @Bean("nominatimAdapter")
    public ServicioGeocodificacionPort nominatimAdapter(
            @Qualifier("nominatimRestClient")
            RestClient restClient
    ) {
        return new NominatimAdapter(restClient);
    }

    @Bean
    @Primary
    public ServicioGeocodificacionPort
    servicioGeocodificacionPort(
            @Qualifier("nominatimAdapter")
            ServicioGeocodificacionPort adaptador
    ) {

        return new ServicioGeocodificacionCacheado(
                adaptador
        );
    }
}