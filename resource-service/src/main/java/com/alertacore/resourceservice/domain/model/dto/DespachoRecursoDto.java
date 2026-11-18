package com.alertacore.resourceservice.domain.model.dto;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;

import java.time.LocalDateTime;
import java.util.UUID;

public record DespachoRecursoDto(

        UUID recursoId,

        UUID incidenteId,

        EstadoRecurso estado,

        LocalDateTime fechaDespacho

) {
}