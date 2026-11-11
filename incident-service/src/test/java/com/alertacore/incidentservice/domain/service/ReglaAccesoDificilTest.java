package com.alertacore.incidentservice.domain.service;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReglaAccesoDificilTest {

    private final ReglaAccesoDificil regla =
            new ReglaAccesoDificil();

    @Test
    void debeSumarDiezPuntosSiElAccesoEsDificil() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.DESLIZAMIENTO,
                1,
                false,
                false,
                true
        );

        int resultado = regla.calcularPuntos(contexto);

        assertEquals(10, resultado);
    }

    @Test
    void noDebeSumarPuntosSiElAccesoEsNormal() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.DESLIZAMIENTO,
                1,
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