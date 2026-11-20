package com.alertacore.resourceservice.domain.model.gateway;

import java.util.UUID;

public interface NotificacionDespachoGateway {

    void notificarDespacho(UUID incidenteId, UUID recursoId);
}