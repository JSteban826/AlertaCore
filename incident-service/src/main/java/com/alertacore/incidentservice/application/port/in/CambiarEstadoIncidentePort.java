package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.application.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;

public interface CambiarEstadoIncidentePort {

    ResultadoIncidente ejecutar(
            CambiarEstadoIncidenteCommand comando
    );
}