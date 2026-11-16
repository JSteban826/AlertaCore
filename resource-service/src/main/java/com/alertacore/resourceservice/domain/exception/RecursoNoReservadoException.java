package com.alertacore.resourceservice.domain.exception;

public class RecursoNoReservadoException
        extends DominioException {

    public RecursoNoReservadoException(String codigo) {

        super(
                "RECURSO_NO_RESERVADO",
                "El recurso %s no se encuentra reservado"
                        .formatted(codigo)
        );
    }
}