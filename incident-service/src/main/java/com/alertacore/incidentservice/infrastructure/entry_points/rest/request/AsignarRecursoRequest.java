package com.alertacore.incidentservice.infrastructure
        .entry_points.rest.request;

import com.alertacore.incidentservice.domain.enums.TipoRecurso;
import com.alertacore.incidentservice.domain.model.command
        .AsignarRecursoCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AsignarRecursoRequest(

        @NotNull(
                message = "El tipo de recurso es obligatorio"
        )
        TipoRecurso tipoRecurso,

        @Positive(
                message = "La distancia maxima debe ser mayor que cero"
        )
        double distanciaMaximaKm,

        @Min(
                value = 1,
                message = "La duracion de la reserva debe ser mayor que cero"
        )
        int duracionReservaMinutos
) {

    public AsignarRecursoCommand aComando(
            UUID incidenteId
    ) {
        return new AsignarRecursoCommand(
                incidenteId,
                tipoRecurso,
                distanciaMaximaKm,
                duracionReservaMinutos
        );
    }
}