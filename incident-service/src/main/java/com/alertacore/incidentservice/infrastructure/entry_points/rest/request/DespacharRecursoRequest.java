package com.alertacore.incidentservice.infrastructure
        .entry_points.rest.request;

import com.alertacore.incidentservice.domain.model.command
        .DespacharRecursoCommand;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record DespacharRecursoRequest(

        @NotBlank(
                message = "El responsable es obligatorio"
        )
        String responsable
) {

    public DespacharRecursoCommand aComando(
            UUID incidenteId,
            UUID recursoId
    ) {
        return new DespacharRecursoCommand(
                incidenteId,
                recursoId,
                responsable
        );
    }
}