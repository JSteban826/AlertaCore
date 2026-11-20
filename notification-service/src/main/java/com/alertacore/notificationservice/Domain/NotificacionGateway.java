package com.alertacore.notificationservice.Domain;

import java.util.UUID;

public interface NotificacionGateway {

    void guardarDespacho(UUID incidenteId, UUID recursoId);
}