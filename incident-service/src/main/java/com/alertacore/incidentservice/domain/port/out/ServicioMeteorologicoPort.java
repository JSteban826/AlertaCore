package com.alertacore.incidentservice.domain.port.out;

import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;

public interface ServicioMeteorologicoPort {

    CondicionMeteorologica consultarCondicionActual(
            double latitud,
            double longitud
    );
}