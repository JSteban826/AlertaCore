package com.alertacore.resourceservice.infrastructure.entrypoint.rest.request;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BuscarRecursoMasCercanoRequest(

        @NotNull
        UUID incidenteId,

        @NotNull
        TipoRecurso tipoRecurso,

        double latitudIncidente,

        double longitudIncidente,

        double distanciaMaximaKm

) {
}