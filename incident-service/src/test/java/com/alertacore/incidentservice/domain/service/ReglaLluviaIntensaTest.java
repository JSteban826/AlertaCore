package com.alertacore.incidentservice.domain.service;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReglaLluviaIntensaTest {

    private final ReglaLluviaIntensa regla =
            new ReglaLluviaIntensa();

    @Test
    void debeSumarQuincePuntosSiHayLluviaIntensa() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.INUNDACION,
                1,
                false,
                true,
                false
        );

        int resultado = regla.calcularPuntos(contexto);

        assertEquals(15, resultado);
    }

    @Test
    void noDebeSumarPuntosSiNoHayLluviaIntensa() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.INUNDACION,
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