package com.alertacore.resourceservice.application.dto;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecursoDto(
        UUID id,
        String codigo,
        String nombre,
        TipoRecurso tipo,
        EstadoRecurso estado,
        double latitud,
        double longitud,
        LocalDateTime fechaDisponibleDesde,
        LocalDateTime fechaUltimaActualizacion
) {
}