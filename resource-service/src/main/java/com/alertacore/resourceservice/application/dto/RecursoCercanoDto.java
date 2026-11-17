package com.alertacore.resourceservice.application.dto;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;

import java.util.UUID;

public record RecursoCercanoDto(
        UUID recursoId,
        String codigo,
        TipoRecurso tipo,
        double distanciaKm,
        int tiempoEstimadoLlegadaMinutos,
        EstadoRecurso estado
) {
}