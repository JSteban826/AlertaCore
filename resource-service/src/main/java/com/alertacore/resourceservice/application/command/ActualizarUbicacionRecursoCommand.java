package com.alertacore.resourceservice.application.command;

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