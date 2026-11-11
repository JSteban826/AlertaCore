package com.alertacore.incidentservice.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoIncidenteTest {

    @Test
    void debePermitirCambiarDeReportadoAValidado() {
        boolean resultado = EstadoIncidente.REPORTADO
                .puedeCambiarA(EstadoIncidente.VALIDADO);

        assertTrue(resultado);
    }

    @Test
    void debeRechazarCambioDeReportadoACerrado() {
        boolean resultado = EstadoIncidente.REPORTADO
                .puedeCambiarA(EstadoIncidente.CERRADO);

        assertFalse(resultado);
    }

    @Test
    void debePermitirCambiarDeResueltoACerrado() {
        boolean resultado = EstadoIncidente.RESUELTO
                .puedeCambiarA(EstadoIncidente.CERRADO);

        assertTrue(resultado);
    }

    @Test
    void cerradoDebeSerEstadoFinal() {
        for (EstadoIncidente estado : EstadoIncidente.values()) {
            assertFalse(
                    EstadoIncidente.CERRADO.puedeCambiarA(estado)
            );
        }
    }

    @Test
    void canceladoDebeSerEstadoFinal() {
        for (EstadoIncidente estado : EstadoIncidente.values()) {
            assertFalse(
                    EstadoIncidente.CANCELADO.puedeCambiarA(estado)
            );
        }
    }

    @Test
    void debeRechazarTransicionNula() {
        assertFalse(
                EstadoIncidente.REPORTADO.puedeCambiarA(null)
        );
    }
}