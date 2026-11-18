package com.alertacore.incidentservice.infrastructure.entry_points.rest.request;

import com.alertacore.incidentservice.domain.model.command.ClasificarIncidenteCommand;

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
