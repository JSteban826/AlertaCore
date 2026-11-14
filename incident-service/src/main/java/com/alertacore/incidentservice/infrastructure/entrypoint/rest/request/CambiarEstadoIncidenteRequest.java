package com.alertacore.incidentservice.infrastructure.entrypoint.rest.request;

import com.alertacore.incidentservice.application.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CambiarEstadoIncidenteRequest(

        @NotNull(
                message = "El nuevo estado es obligatorio"
        )
        EstadoIncidente nuevoEstado,

        @NotBlank(
                message = "El motivo del cambio es obligatorio"
        )
        @Size(
                max = 250,
                message = "El motivo no puede superar los 250 caracteres"
        )
        String motivo
) {

    public CambiarEstadoIncidenteCommand aComando(
            UUID incidenteId
    ) {
        return new CambiarEstadoIncidenteCommand(
                incidenteId,
                nuevoEstado,
                motivo
        );
    }
}