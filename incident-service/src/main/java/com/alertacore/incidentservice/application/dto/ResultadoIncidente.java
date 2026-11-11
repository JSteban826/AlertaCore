package com.alertacore.incidentservice.application.dto;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResultadoIncidente(
        UUID id,
        TipoIncidente tipo,
        String descripcion,
        EstadoIncidente estado,
        NivelRiesgo nivelRiesgo,
        int cantidadPersonasAfectadas,
        boolean hayPersonasHeridas,
        double latitud,
        double longitud,
        String direccion,
        LocalDateTime fechaReporte,
        LocalDateTime fechaUltimaActualizacion
) {

    public static ResultadoIncidente desde(
            Incidente incidente
    ) {
        return new ResultadoIncidente(
                incidente.getId(),
                incidente.getTipo(),
                incidente.getDescripcion(),
                incidente.getEstado(),
                incidente.obtenerNivelRiesgo(),
                incidente.getCantidadPersonasAfectadas(),
                incidente.hayPersonasHeridas(),
                incidente.getUbicacion().getLatitud(),
                incidente.getUbicacion().getLongitud(),
                incidente.getUbicacion().getDireccion(),
                incidente.getFechaReporte(),
                incidente.getFechaUltimaActualizacion()
        );
    }
}