package com.alertacore.incidentservice.domain.model.command;

import com.alertacore.incidentservice.domain.enums.TipoRecurso;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AsignarRecursoCommandTest {

    @Test
    void debeCrearComandoValido() {
        UUID incidenteId = UUID.randomUUID();

        AsignarRecursoCommand comando =
                new AsignarRecursoCommand(
                        incidenteId,
                        TipoRecurso.AMBULANCIA,
                        30.0,
                        10
                );

        assertEquals(
                incidenteId,
                comando.incidenteId()
        );

        assertEquals(
                TipoRecurso.AMBULANCIA,
                comando.tipoRecurso()
        );

        assertEquals(
                30.0,
                comando.distanciaMaximaKm()
        );

        assertEquals(
                10,
                comando.duracionReservaMinutos()
        );
    }

    @Test
    void debeRechazarIncidenteNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new AsignarRecursoCommand(
                                null,
                                TipoRecurso.AMBULANCIA,
                                30.0,
                                10
                        )
                );

        assertEquals(
                "El identificador del incidente es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarTipoRecursoNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new AsignarRecursoCommand(
                                UUID.randomUUID(),
                                null,
                                30.0,
                                10
                        )
                );

        assertEquals(
                "El tipo de recurso es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarDistanciaCero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AsignarRecursoCommand(
                        UUID.randomUUID(),
                        TipoRecurso.AMBULANCIA,
                        0.0,
                        10
                )
        );
    }

    @Test
    void debeRechazarDistanciaNegativa() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new AsignarRecursoCommand(
                                UUID.randomUUID(),
                                TipoRecurso.AMBULANCIA,
                                -1.0,
                                10
                        )
                );

        assertEquals(
                "La distancia maxima debe ser mayor que cero",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarDuracionCero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AsignarRecursoCommand(
                        UUID.randomUUID(),
                        TipoRecurso.AMBULANCIA,
                        30.0,
                        0
                )
        );
    }

    @Test
    void debeRechazarDuracionNegativa() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new AsignarRecursoCommand(
                                UUID.randomUUID(),
                                TipoRecurso.AMBULANCIA,
                                30.0,
                                -1
                        )
                );

        assertEquals(
                "La duracion de la reserva debe ser mayor que cero",
                excepcion.getMessage()
        );
    }
}