package com.alertacore.resourceservice.application.command;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.UbicacionInvalidaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BuscarRecursoMasCercanoCommandTest {

    @Test
    void deberiaCrearUnComandoValido() {
        BuscarRecursoMasCercanoCommand command =
                new BuscarRecursoMasCercanoCommand(
                        UUID.randomUUID(),
                        TipoRecurso.BRIGADA_RESCATE,
                        4.6579,
                        -74.0596,
                        30
                );

        assertEquals(
                TipoRecurso.BRIGADA_RESCATE,
                command.tipoRecurso()
        );
        assertEquals(30, command.distanciaMaximaKm());
    }

    @Test
    void deberiaRechazarIncidenteNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new BuscarRecursoMasCercanoCommand(
                        null,
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596,
                        30
                )
        );
    }

    @Test
    void deberiaRechazarTipoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new BuscarRecursoMasCercanoCommand(
                        UUID.randomUUID(),
                        null,
                        4.6579,
                        -74.0596,
                        30
                )
        );
    }

    @Test
    void deberiaRechazarUbicacionInvalida() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> new BuscarRecursoMasCercanoCommand(
                        UUID.randomUUID(),
                        TipoRecurso.AMBULANCIA,
                        91,
                        -74.0596,
                        30
                )
        );
    }

    @Test
    void deberiaRechazarDistanciaCero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BuscarRecursoMasCercanoCommand(
                        UUID.randomUUID(),
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596,
                        0
                )
        );
    }

    @Test
    void deberiaRechazarDistanciaNegativa() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BuscarRecursoMasCercanoCommand(
                        UUID.randomUUID(),
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596,
                        -1
                )
        );
    }


    @Test
    void deberiaRechazarDistanciaNoFinita() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BuscarRecursoMasCercanoCommand(
                        UUID.randomUUID(),
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596,
                        Double.NaN
                )
        );
    }
}