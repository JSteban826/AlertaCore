package com.alertacore.incidentservice.domain.service;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReglaInundacionTest {

    private final ReglaInundacion regla =
            new ReglaInundacion();

    @Test
    void debeSumarDiezPuntosSiEsInundacion() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.INUNDACION,
                1,
                false,
                false,
                false
        );

        int resultado = regla.calcularPuntos(contexto);

        assertEquals(10, resultado);
    }

    @Test
    void noDebeSumarPuntosSiNoEsInundacion() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.INCENDIO,
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