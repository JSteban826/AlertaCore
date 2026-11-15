package com.alertacore.incidentservice.application.dto;

import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;

public record GeocodificacionResultado(
        String direccionConsultada,
        String direccionNormalizada,
        double latitud,
        double longitud,
        String fuente,
        String atribucion
) {

    public static GeocodificacionResultado desde(
            ResultadoGeocodificacion resultado
    ) {
        return new GeocodificacionResultado(
                resultado.direccionConsultada(),
                resultado.direccionNormalizada(),
                resultado.latitud(),
                resultado.longitud(),
                resultado.fuente(),
                resultado.atribucion()
        );
    }
}