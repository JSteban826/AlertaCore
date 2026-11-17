package com.alertacore.incidentservice.domain.usecase.risk;

import com.alertacore.incidentservice.domain.model.ContextoRiesgo;

import java.util.Objects;

public final class ReglaPersonasHeridas
        implements ReglaRiesgo {

    private static final int PUNTOS_PERSONAS_HERIDAS = 40;

    @Override
    public int calcularPuntos(ContextoRiesgo contexto) {
        Objects.requireNonNull(
                contexto,
                "El contexto de riesgo es obligatorio"
        );

        return contexto.hayPersonasHeridas()
                ? PUNTOS_PERSONAS_HERIDAS
                : 0;
    }
}