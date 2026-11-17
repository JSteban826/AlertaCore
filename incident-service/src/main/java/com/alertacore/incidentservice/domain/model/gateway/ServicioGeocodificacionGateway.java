package com.alertacore.incidentservice.domain.model.gateway;

import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;

public interface ServicioGeocodificacionGateway {

    ResultadoGeocodificacion geocodificar(
            String direccion
    );
}