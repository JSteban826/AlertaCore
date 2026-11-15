package com.alertacore.resourceservice.domain.exception;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;

public class TransicionRecursoInvalidaException extends DominioException {

    private static final String CODIGO = "TRANSICION_RECURSO_INVALIDA";

    public TransicionRecursoInvalidaException(
            EstadoRecurso estadoActual,
            EstadoRecurso estadoDestino
    ) {
        super(
                CODIGO, "No se permite cambiar el recurso de %s a %s"
                .formatted(estadoActual, estadoDestino)
        );
    }
}