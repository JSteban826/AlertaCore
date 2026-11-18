package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.enums.TipoRecurso;

import java.time.LocalDateTime;
import java.util.UUID;

public record AsignacionRecursoResultado(
        UUID incidenteId,
        UUID recursoId,
        UUID reservaId,
        String codigoRecurso,
        TipoRecurso tipoRecurso,
        EstadoRecurso estadoRecurso,
        EstadoIncidente estadoIncidente,
        double distanciaKm,
        int tiempoEstimadoLlegadaMinutos,
        LocalDateTime fechaReserva,
        LocalDateTime fechaVencimiento
) {
}