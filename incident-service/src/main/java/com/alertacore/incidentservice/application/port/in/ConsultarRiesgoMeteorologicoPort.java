package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.domain.model.RiesgoMeteorologicoResultado;

import java.util.UUID;

public interface ConsultarRiesgoMeteorologicoPort {

    RiesgoMeteorologicoResultado ejecutar(
            UUID incidenteId
    );
}