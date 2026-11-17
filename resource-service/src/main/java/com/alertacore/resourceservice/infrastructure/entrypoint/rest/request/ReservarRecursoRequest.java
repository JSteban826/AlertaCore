package com.alertacore.resourceservice.infrastructure.entrypoint.rest.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReservarRecursoRequest(

        @NotNull
        UUID incidenteId,

        @Min(1)
        int duracionReservaMinutos

) {
}