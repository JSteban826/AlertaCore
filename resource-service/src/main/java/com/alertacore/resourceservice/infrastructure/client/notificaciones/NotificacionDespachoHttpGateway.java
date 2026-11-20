package com.alertacore.resourceservice.infrastructure.client.notificaciones;

import com.alertacore.resourceservice.domain.model.gateway.NotificacionDespachoGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class NotificacionDespachoHttpGateway implements NotificacionDespachoGateway {

    private final RestClient cliente;

    public NotificacionDespachoHttpGateway(
            @Value("${notificaciones.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.cliente = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public void notificarDespacho(UUID incidenteId, UUID recursoId) {
        cliente.post()
                .uri("/api/v1/notificaciones/despachos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AvisoDespacho(incidenteId, recursoId))
                .retrieve()
                .toBodilessEntity();
    }

    public record AvisoDespacho(UUID incidenteId, UUID recursoId) {}
}