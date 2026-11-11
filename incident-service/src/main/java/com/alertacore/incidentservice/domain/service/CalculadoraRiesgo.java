package com.alertacore.incidentservice.domain.service;

import com.alertacore.incidentservice.domain.model.PuntajeRiesgo;

import java.util.List;
import java.util.Objects;

public final class CalculadoraRiesgo {

    private static final int PUNTAJE_MAXIMO = 100;

    private final List<ReglaRiesgo> reglas;

    public CalculadoraRiesgo(List<ReglaRiesgo> reglas) {
        Objects.requireNonNull(
                reglas,
                "La lista de reglas es obligatoria"
        );

        if (reglas.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe existir al menos una regla de riesgo"
            );
        }

        if (reglas.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "La lista de reglas no puede contener valores nulos"
            );
        }

        this.reglas = List.copyOf(reglas);
    }

    public PuntajeRiesgo calcular(ContextoRiesgo contexto) {
        Objects.requireNonNull(
                contexto,
                "El contexto de riesgo es obligatorio"
        );

        int puntajeTotal = reglas.stream()
                .mapToInt(
                        regla -> regla.calcularPuntos(contexto)
                )
                .sum();

        int puntajeLimitado = Math.min(
                puntajeTotal,
                PUNTAJE_MAXIMO
        );

        return new PuntajeRiesgo(puntajeLimitado);
    }
}