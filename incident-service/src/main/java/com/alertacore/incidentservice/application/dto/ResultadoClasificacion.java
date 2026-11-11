package com.alertacore.incidentservice.application.dto;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.model.Incidente;

import java.util.UUID;

public record ResultadoClasificacion(
        UUID incidenteId,
        int puntaje,
        NivelRiesgo nivelRiesgo,
        int tiempoMaximoRespuestaMinutos,
        EstadoIncidente estado
) {

    public static ResultadoClasificacion desde(
            Incidente incidente
    ) {
        return new ResultadoClasificacion(
                incidente.getId(),
                incidente.getPuntajeRiesgo().getValor(),
                incidente.obtenerNivelRiesgo(),
                incidente.obtenerTiempoMaximoRespuestaMinutos(),
                incidente.getEstado()
        );
    }
}