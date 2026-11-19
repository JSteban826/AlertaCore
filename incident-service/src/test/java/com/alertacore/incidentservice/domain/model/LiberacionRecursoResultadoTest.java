package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LiberacionRecursoResultadoTest {

    @Test
    void debeCrearResultadoValido() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();

        LiberacionRecursoResultado resultado =
                new LiberacionRecursoResultado(
                        incidenteId,
                        recursoId,
                        EstadoRecurso.DISPONIBLE,
                        EstadoIncidente.RECURSO_ASIGNADO,
                        fecha
                );

        assertEquals(incidenteId, resultado.incidenteId());
        assertEquals(recursoId, resultado.recursoId());
        assertEquals(
                EstadoRecurso.DISPONIBLE,
                resultado.estadoRecurso()
        );
        assertEquals(
                EstadoIncidente.RECURSO_ASIGNADO,
                resultado.estadoIncidente()
        );
        assertEquals(fecha, resultado.fechaLiberacion());
    }

    @Test
    void debeRechazarIncidenteNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberacionRecursoResultado(
                        null,
                        UUID.randomUUID(),
                        EstadoRecurso.DISPONIBLE,
                        EstadoIncidente.RECURSO_ASIGNADO,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarRecursoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberacionRecursoResultado(
                        UUID.randomUUID(),
                        null,
                        EstadoRecurso.DISPONIBLE,
                        EstadoIncidente.RECURSO_ASIGNADO,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarEstadoRecursoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberacionRecursoResultado(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        EstadoIncidente.RECURSO_ASIGNADO,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarEstadoIncidenteNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberacionRecursoResultado(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        EstadoRecurso.DISPONIBLE,
                        null,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarFechaNula() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberacionRecursoResultado(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        EstadoRecurso.DISPONIBLE,
                        EstadoIncidente.RECURSO_ASIGNADO,
                        null
                )
        );
    }
}