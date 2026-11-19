package com.alertacore.incidentservice.domain.model.command;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DespacharRecursoCommandTest {

    @Test
    void debeCrearComandoValido() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        DespacharRecursoCommand comando =
                new DespacharRecursoCommand(
                        incidenteId,
                        recursoId,
                        "OPERADOR_CENTRAL"
                );

        assertEquals(
                incidenteId,
                comando.incidenteId()
        );

        assertEquals(
                recursoId,
                comando.recursoId()
        );

        assertEquals(
                "OPERADOR_CENTRAL",
                comando.responsable()
        );
    }

    @Test
    void debeRechazarIncidenteNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new DespacharRecursoCommand(
                                null,
                                UUID.randomUUID(),
                                "OPERADOR_CENTRAL"
                        )
                );

        assertEquals(
                "El identificador del incidente es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarRecursoNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new DespacharRecursoCommand(
                                UUID.randomUUID(),
                                null,
                                "OPERADOR_CENTRAL"
                        )
                );

        assertEquals(
                "El identificador del recurso es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarResponsableNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new DespacharRecursoCommand(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                null
                        )
                );

        assertEquals(
                "El responsable es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarResponsableVacio() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new DespacharRecursoCommand(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                ""
                        )
                );

        assertEquals(
                "El responsable no puede estar vacio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarResponsableConSoloEspacios() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new DespacharRecursoCommand(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "   "
                        )
                );

        assertEquals(
                "El responsable no puede estar vacio",
                excepcion.getMessage()
        );
    }
}