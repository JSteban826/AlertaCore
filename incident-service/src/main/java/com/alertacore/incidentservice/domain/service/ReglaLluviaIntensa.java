package com.alertacore.incidentservice.domain.service;

import java.util.Objects;

public final class ReglaLluviaIntensa
        implements ReglaRiesgo {

    private static final int PUNTOS_LLUVIA_INTENSA = 15;

    @Override
    public int calcularPuntos(ContextoRiesgo contexto) {
        Objects.requireNonNull(
                contexto,
                "El contexto de riesgo es obligatorio"
        );

        return contexto.hayLluviaIntensa()
                ? PUNTOS_LLUVIA_INTENSA
                : 0;
    }
}