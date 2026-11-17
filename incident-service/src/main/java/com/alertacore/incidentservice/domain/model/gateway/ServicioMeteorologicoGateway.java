package com.alertacore.incidentservice.domain.model.gateway;

import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;

public interface ServicioMeteorologicoGateway {

    CondicionMeteorologica consultarCondicionActual(
            double latitud,
            double longitud
    );
}