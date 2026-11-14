package com.alertacore.incidentservice.infrastructure.entrypoint.rest.request;

import com.alertacore.incidentservice.application.command.ClasificarIncidenteCommand;

import java.util.UUID;

public record ClasificarIncidenteRequest(
        boolean hayLluviaIntensa,
        boolean accesoDificil
) {

    public ClasificarIncidenteCommand aComando(
            UUID incidenteId
    ) {
        return new ClasificarIncidenteCommand(
                incidenteId,
                hayLluviaIntensa,
                accesoDificil
        );
    }
}
