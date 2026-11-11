package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.application.command.ClasificarIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoClasificacion;

public interface ClasificarIncidentePort {

    ResultadoClasificacion ejecutar(
            ClasificarIncidenteCommand comando
    );
}