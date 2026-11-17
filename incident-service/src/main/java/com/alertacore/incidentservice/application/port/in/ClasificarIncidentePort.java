package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.domain.model.command.ClasificarIncidenteCommand;
import com.alertacore.incidentservice.domain.model.ResultadoClasificacion;

public interface ClasificarIncidentePort {

    ResultadoClasificacion ejecutar(
            ClasificarIncidenteCommand comando
    );
}