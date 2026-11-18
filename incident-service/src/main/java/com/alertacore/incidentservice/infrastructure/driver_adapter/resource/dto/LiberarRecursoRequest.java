package com.alertacore.incidentservice.infrastructure
        .driver_adapter.resource.dto;

import java.util.UUID;

public record LiberarRecursoRequest(
        UUID incidenteId,
        String motivo
) {
}