package com.alertacore.incidentservice.domain.model.command;

import com.alertacore.incidentservice.domain.model.command.ClasificarIncidenteCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClasificarIncidenteCommandTest {

    @Test
    void debeCrearComandoValido() {
        UUID incidenteId = UUID.randomUUID();

        ClasificarIncidenteCommand comando =
                new ClasificarIncidenteCommand(
                        incidenteId,
                        true,
                        true
                );

        assertEquals(
                incidenteId,
                comando.incidenteId()
        );

        assertTrue(
                comando.hayLluviaIntensa()
        );

        assertTrue(
                comando.accesoDificil()
        );
    }

    @Test
    void debeCrearComandoSinLluviaNiAccesoDificil() {
        UUID incidenteId = UUID.randomUUID();

        ClasificarIncidenteCommand comando =
                new ClasificarIncidenteCommand(
                        incidenteId,
                        false,
                        false
                );

        assertEquals(
                incidenteId,
                comando.incidenteId()
        );

        assertFalse(
                comando.hayLluviaIntensa()
        );

        assertFalse(
                comando.accesoDificil()
        );
    }

    @Test
    void debePermitirLluviaSinAccesoDificil() {
        ClasificarIncidenteCommand comando =
                new ClasificarIncidenteCommand(
                        UUID.randomUUID(),
                        true,
                        false
                );

        assertTrue(
                comando.hayLluviaIntensa()
        );

        assertFalse(
                comando.accesoDificil()
        );
    }

    @Test
    void debePermitirAccesoDificilSinLluvia() {
        ClasificarIncidenteCommand comando =
                new ClasificarIncidenteCommand(
                        UUID.randomUUID(),
                        false,
                        true
                );

        assertFalse(
                comando.hayLluviaIntensa()
        );

        assertTrue(
                comando.accesoDificil()
        );
    }

    @Test
    void debeRechazarIdentificadorNulo() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new ClasificarIncidenteCommand(
                                null,
                                false,
                                false
                        )
                );

        assertEquals(
                "El identificador del incidente es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeCompararComandosConLosMismosValores() {
        UUID incidenteId = UUID.randomUUID();

        ClasificarIncidenteCommand primerComando =
                new ClasificarIncidenteCommand(
                        incidenteId,
                        true,
                        false
                );

        ClasificarIncidenteCommand segundoComando =
                new ClasificarIncidenteCommand(
                        incidenteId,
                        true,
                        false
                );

        assertEquals(
                primerComando,
                segundoComando
        );

        assertEquals(
                primerComando.hashCode(),
                segundoComando.hashCode()
        );
    }

    @Test
    void debeDiferenciarComandosConDatosDistintos() {
        UUID incidenteId = UUID.randomUUID();

        ClasificarIncidenteCommand primerComando =
                new ClasificarIncidenteCommand(
                        incidenteId,
                        true,
                        false
                );

        ClasificarIncidenteCommand segundoComando =
                new ClasificarIncidenteCommand(
                        incidenteId,
                        false,
                        true
                );

        assertNotEquals(
                primerComando,
                segundoComando
        );
    }
}