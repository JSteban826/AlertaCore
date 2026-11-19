package com.alertacore.incidentservice.infrastructure.driver_adapter.resource;

import java.util.UUID;

public record DespacharRecursoRequest(
        UUID incidenteId,
        String responsable
) {
}