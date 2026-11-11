package com.alertacore.incidentservice.domain.service;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReglaCantidadAfectadosTest {

    private final ReglaCantidadAfectados regla =
            new ReglaCantidadAfectados();

    @Test
    void debeSumarVeintePuntosSiHayMasDeDiezAfectados() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.INUNDACION,
                11,
                false,
                false,
                false
        );

        int resultado = regla.calcularPuntos(contexto);


        assertEquals(20, resultado);
    }

    @Test
    void noDebeSumarPuntosSiHayDiezAfectados() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.INUNDACION,
                10,
                false,
                false,
                false
        );

        int resultado = regla.calcularPuntos(contexto);

        assertEquals(0, resultado);
    }

    @Test
    void noDebeSumarPuntosSiHayMenosDeDiezAfectados() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.INUNDACION,
                5,
                false,
                false,
                false
        );

        int resultado = regla.calcularPuntos(contexto);

        assertEquals(0, resultado);
    }

    @Test
    void debeRechazarContextoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> regla.calcularPuntos(null)
        );
    }
}