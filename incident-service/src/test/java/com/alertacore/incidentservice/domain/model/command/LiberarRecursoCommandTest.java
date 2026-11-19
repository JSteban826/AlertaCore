package com.alertacore.incidentservice.domain.model.command;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LiberarRecursoCommandTest {

    @Test
    void debeCrearComandoValido() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        LiberarRecursoCommand comando =
                new LiberarRecursoCommand(
                        incidenteId,
                        recursoId,
                        "Atencion finalizada"
                );

        assertEquals(incidenteId, comando.incidenteId());
        assertEquals(recursoId, comando.recursoId());
        assertEquals(
                "Atencion finalizada",
                comando.motivo()
        );
    }

    @Test
    void debeRechazarIncidenteNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberarRecursoCommand(
                        null,
                        UUID.randomUUID(),
                        "Atencion finalizada"
                )
        );
    }

    @Test
    void debeRechazarRecursoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberarRecursoCommand(
                        UUID.randomUUID(),
                        null,
                        "Atencion finalizada"
                )
        );
    }

    @Test
    void debeRechazarMotivoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberarRecursoCommand(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null
                )
        );
    }

    @Test
    void debeRechazarMotivoVacio() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new LiberarRecursoCommand(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "   "
                        )
                );

        assertEquals(
                "El motivo no puede estar vacio",
                excepcion.getMessage()
        );
    }
}