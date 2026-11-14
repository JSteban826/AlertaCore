package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.application.dto.RiesgoMeteorologicoResultado;

import java.util.UUID;

public interface ConsultarRiesgoMeteorologicoPort {

    RiesgoMeteorologicoResultado ejecutar(
            UUID incidenteId
    );
}