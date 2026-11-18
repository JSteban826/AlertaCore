package com.alertacore.resourceservice.domain.model.dto;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;

import java.time.LocalDateTime;
import java.util.UUID;

public record LiberarRecursoDto(

        UUID recursoId,

        EstadoRecurso estado,

        LocalDateTime fechaLiberacion

) {
}