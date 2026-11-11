package com.alertacore.incidentservice.domain.service;

import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReglaPersonasHeridasTest {

    private final ReglaPersonasHeridas regla =
            new ReglaPersonasHeridas();

    @Test
    void debeSumarCuarentaPuntosSiHayPersonasHeridas() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.ACCIDENTE_VIAL,
                2,
                true,
                false,
                false
        );

        int resultado = regla.calcularPuntos(contexto);

        assertEquals(40, resultado);
    }

    @Test
    void noDebeSumarPuntosSiNoHayPersonasHeridas() {
        ContextoRiesgo contexto = new ContextoRiesgo(
                TipoIncidente.ACCIDENTE_VIAL,
                2,
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