package com.alertacore.incidentservice.domain.exception;

import java.util.UUID;

public class RecursoNoDisponibleException
        extends RuntimeException {

    public RecursoNoDisponibleException(
            UUID recursoId
    ) {
        super(
                "El recurso no se encuentra disponible: "
                        + recursoId
        );
    }
}