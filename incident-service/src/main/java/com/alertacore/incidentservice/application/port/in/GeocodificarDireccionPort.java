package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.domain.model.command.GeocodificarDireccionCommand;
import com.alertacore.incidentservice.domain.model.GeocodificacionResultado;

public interface GeocodificarDireccionPort {

    GeocodificacionResultado ejecutar(
            GeocodificarDireccionCommand comando
    );
}