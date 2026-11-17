package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.domain.model.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;

public interface CambiarEstadoIncidentePort {

    ResultadoIncidente ejecutar(
            CambiarEstadoIncidenteCommand comando
    );
}