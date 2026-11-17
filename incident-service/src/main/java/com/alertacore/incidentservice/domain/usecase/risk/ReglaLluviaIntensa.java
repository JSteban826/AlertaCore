package com.alertacore.incidentservice.domain.usecase.risk;

import com.alertacore.incidentservice.domain.model.ContextoRiesgo;

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