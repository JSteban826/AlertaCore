package com.alertacore.resourceservice.domain.exception;

public class RecursoNoLiberableException
        extends DominioException {

    public RecursoNoLiberableException(
            String codigo
    ) {

        super(
                "RECURSO_NO_LIBERABLE",
                "El recurso %s no puede liberarse"
                        .formatted(codigo)
        );
    }
}