package com.alertacore.notificationservice.Application;

import com.alertacore.notificationservice.Domain.NotificacionGateway;

import java.util.UUID;

public class RegistrarDespachoUseCase {

    private final NotificacionGateway notificacionGateway;

    public RegistrarDespachoUseCase(NotificacionGateway notificacionGateway) {
        this.notificacionGateway = notificacionGateway;
    }

    public AvisoRecibido ejecutar(UUID incidenteId, UUID recursoId) {
        notificacionGateway.guardarDespacho(incidenteId, recursoId);
        return new AvisoRecibido(incidenteId, recursoId, "RECIBIDO");
    }

    public record AvisoRecibido(
            UUID incidenteId,
            UUID recursoId,
            String estado
    ) {}
}