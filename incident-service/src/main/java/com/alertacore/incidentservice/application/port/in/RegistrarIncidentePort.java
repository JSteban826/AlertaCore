package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.domain.model.command.RegistrarIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;

public interface RegistrarIncidentePort {

    ResultadoIncidente ejecutar(
            RegistrarIncidenteCommand comando
    );
}