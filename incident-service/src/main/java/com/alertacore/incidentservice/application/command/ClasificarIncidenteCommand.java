package com.alertacore.incidentservice.application.command;

import java.util.UUID;

public record ClasificarIncidenteCommand(
        UUID incidenteId,
        boolean hayLluviaIntensa,
        boolean accesoDificil
) {

    public ClasificarIncidenteCommand {
        if (incidenteId == null) {
            throw new IllegalArgumentException(
                    "El identificador del incidente es obligatorio"
            );
        }
    }
}