package com.alertacore.incidentservice.domain.gateway;

import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;

public interface ServicioGeocodificacionPort {

    ResultadoGeocodificacion geocodificar(
            String direccion
    );
}