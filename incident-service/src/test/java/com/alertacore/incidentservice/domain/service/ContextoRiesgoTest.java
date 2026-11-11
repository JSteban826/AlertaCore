package com.alertacore.incidentservice.domain.service;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContextoRiesgoTest {

    @Test
    void debeCrearContextoValido() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.INCENDIO,
                5,
                true,
                false,
                true
        );

        assertEquals(
                TipoIncidente.INCENDIO,
                contexto.tipoIncidente()
        );

        assertEquals(
                5,
                contexto.cantidadPersonasAfectadas()
        );

        assertTrue(contexto.hayPersonasHeridas());
        assertFalse(contexto.hayLluviaIntensa());
        assertTrue(contexto.accesoDificil());
    }

    @Test
    void debeRechazarCantidadNegativaDeAfectados() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ContextoRiesgo(
                        TipoIncidente.INCENDIO,
                        -1,
                        false,
                        false,
                        false
                )
        );
    }

    @Test
    void debeRechazarTipoDeIncidenteNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new ContextoRiesgo(
                        null,
                        1,
                        false,
                        false,
                        false
                )
        );
    }

    @Test
    void debePermitirCeroPersonasAfectadas() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.FALLA_ELECTRICA,
                0,
                false,
                false,
                false
        );

        assertEquals(
                0,
                contexto.cantidadPersonasAfectadas()
        );
    }
}