package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.application.dto.ResultadoIncidente;

import java.util.UUID;

public interface ConsultarIncidentePort {

    ResultadoIncidente ejecutar(UUID incidenteId);
}