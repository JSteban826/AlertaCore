package com.alertacore.resourceservice.infrastructure.entrypoint.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LiberarRecursoRequest(

        @NotNull
        UUID incidenteId,

        @NotBlank
        String motivo

) {
}