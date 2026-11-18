package com.alertacore.resourceservice.domain.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActualizarUbicacionRecursoDto(

        UUID recursoId,

        double latitud,

        double longitud,

        LocalDateTime fechaActualizacion

) {
}