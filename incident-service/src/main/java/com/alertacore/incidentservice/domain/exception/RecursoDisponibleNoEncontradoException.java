package com.alertacore.incidentservice.domain.exception;

import com.alertacore.incidentservice.domain.enums.TipoRecurso;

public class RecursoDisponibleNoEncontradoException
        extends RuntimeException {

    public RecursoDisponibleNoEncontradoException(
            TipoRecurso tipoRecurso
    ) {
        super(
                "No se encontro un recurso disponible de tipo "
                        + tipoRecurso
        );
    }
}