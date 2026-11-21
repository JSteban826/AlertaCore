package com.alertacore.notificationservice.domain;

import java.util.UUID;

public interface NotificacionGateway {

    void guardarDespacho(UUID incidenteId, UUID recursoId);
}