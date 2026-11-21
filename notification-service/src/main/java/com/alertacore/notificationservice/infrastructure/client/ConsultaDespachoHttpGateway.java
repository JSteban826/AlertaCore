package com.alertacore.notificationservice.infrastructure.client;

import com.alertacore.notificationservice.domain.model.gateway.ConsultaDespachoGateway;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.UUID;

public class ConsultaDespachoHttpGateway
        implements ConsultaDespachoGateway {

    private final RestClient incidentClient;
    private final RestClient resourceClient;

    public ConsultaDespachoHttpGateway(
            String incidentBaseUrl,
            String resourceBaseUrl
    ) {
        this.incidentClient = crearCliente(incidentBaseUrl);
        this.resourceClient = crearCliente(resourceBaseUrl);
    }

    @Override
    public IncidenteDetalle consultarIncidente(UUID incidenteId) {
        IncidenteDetalle resultado = incidentClient.get()
                .uri("/api/v1/incidentes/{id}", incidenteId)
                .retrieve()
                .body(IncidenteDetalle.class);

        if (resultado == null) {
            throw new IllegalStateException(
                    "Incident Service devolvió una respuesta vacía"
            );
        }

        return resultado;
    }

    @Override
    public RecursoDetalle consultarRecurso(UUID recursoId) {
        RecursoDetalle resultado = resourceClient.get()
                .uri("/api/v1/recursos/{id}", recursoId)
                .retrieve()
                .body(RecursoDetalle.class);

        if (resultado == null) {
            throw new IllegalStateException(
                    "Resource Service devolvió una respuesta vacía"
            );
        }

        return resultado;
    }

    private static RestClient crearCliente(String baseUrl) {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}