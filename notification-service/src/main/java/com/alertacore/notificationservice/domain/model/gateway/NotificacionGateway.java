package com.alertacore.notificationservice.domain.model.gateway;

import java.util.UUID;

public interface NotificacionGateway {

    void guardarDespacho(UUID incidenteId, UUID recursoId);
}