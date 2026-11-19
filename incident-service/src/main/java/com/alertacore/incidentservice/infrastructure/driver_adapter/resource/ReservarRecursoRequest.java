package com.alertacore.incidentservice.infrastructure.driver_adapter.resource;

import java.util.UUID;

public record ReservarRecursoRequest(
        UUID incidenteId,
        int duracionReservaMinutos
) {
}