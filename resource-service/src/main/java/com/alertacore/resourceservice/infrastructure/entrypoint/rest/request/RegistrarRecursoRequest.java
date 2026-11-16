package com.alertacore.resourceservice.infrastructure.entrypoint.rest.request;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarRecursoRequest(

        @NotBlank
        String codigo,

        @NotBlank
        String nombre,

        @NotNull
        TipoRecurso tipo,

        double latitud,

        double longitud

) {
}