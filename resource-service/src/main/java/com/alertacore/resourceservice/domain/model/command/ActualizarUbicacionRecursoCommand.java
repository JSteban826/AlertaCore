package com.alertacore.resourceservice.domain.model.command;

import java.util.Objects;
import java.util.UUID;

public record ActualizarUbicacionRecursoCommand(

        UUID recursoId,

        double latitud,

        double longitud

) {

    public ActualizarUbicacionRecursoCommand {

        Objects.requireNonNull(
                recursoId,
                "El recurso es obligatorio"
        );
    }
}