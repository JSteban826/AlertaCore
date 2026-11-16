package com.alertacore.resourceservice.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActualizarUbicacionRecursoDto(

        UUID recursoId,

        double latitud,

        double longitud,

        LocalDateTime fechaActualizacion

) {
}