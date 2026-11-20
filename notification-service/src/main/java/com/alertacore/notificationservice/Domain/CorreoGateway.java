package com.alertacore.notificationservice.Domain;

import java.util.UUID;

public interface CorreoGateway {

    void enviarAvisoDespacho(UUID incidenteId, UUID recursoId);
}