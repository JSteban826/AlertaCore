package com.alertacore.incidentservice.infrastructure.entry_points.rest.request;

import com.alertacore.incidentservice.domain.model.command
        .LiberarRecursoCommand;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record LiberarRecursoRequest(

        @NotBlank(
                message = "El motivo es obligatorio"
        )
        String motivo
) {

    public LiberarRecursoCommand aComando(
            UUID incidenteId,
            UUID recursoId
    ) {
        return new LiberarRecursoCommand(
                incidenteId,
                recursoId,
                motivo
        );
    }
}