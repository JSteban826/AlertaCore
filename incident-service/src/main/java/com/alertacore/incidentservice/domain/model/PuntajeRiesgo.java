package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.NivelRiesgo;

public final class PuntajeRiesgo {

    private static final int PUNTAJE_MINIMO = 0;
    private static final int PUNTAJE_MAXIMO = 100;

    private final int valor;

    public PuntajeRiesgo(int valor) {
        if (valor < PUNTAJE_MINIMO || valor > PUNTAJE_MAXIMO) {
            throw new IllegalArgumentException(
                    "El puntaje de riesgo debe estar entre 0 y 100"
            );
        }

        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public NivelRiesgo obtenerNivel() {
        if (valor <= 24) {
            return NivelRiesgo.BAJO;
        }

        if (valor <= 49) {
            return NivelRiesgo.MEDIO;
        }

        if (valor <= 74) {
            return NivelRiesgo.ALTO;
        }

        return NivelRiesgo.CRITICO;
    }

    public int obtenerTiempoMaximoRespuestaMinutos() {
        return switch (obtenerNivel()) {
            case BAJO -> 120;
            case MEDIO -> 60;
            case ALTO -> 30;
            case CRITICO -> 10;
        };
    }
}