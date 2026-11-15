package com.alertacore.incidentservice.domain.port.out;

import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;

public interface ServicioGeocodificacionPort {

    ResultadoGeocodificacion geocodificar(
            String direccion
    );
}