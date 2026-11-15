package com.alertacore.incidentservice.application.port.in;

import com.alertacore.incidentservice.application.command.GeocodificarDireccionCommand;
import com.alertacore.incidentservice.application.dto.GeocodificacionResultado;

public interface GeocodificarDireccionPort {

    GeocodificacionResultado ejecutar(
            GeocodificarDireccionCommand comando
    );
}