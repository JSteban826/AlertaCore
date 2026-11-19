package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LiberacionRecursoTest {

    @Test
    void debeCrearLiberacionValida() {
        UUID recursoId = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();

        LiberacionRecurso liberacion =
                new LiberacionRecurso(
                        recursoId,
                        EstadoRecurso.DISPONIBLE,
                        fecha
                );

        assertEquals(recursoId, liberacion.recursoId());
        assertEquals(
                EstadoRecurso.DISPONIBLE,
                liberacion.estado()
        );
        assertEquals(fecha, liberacion.fechaLiberacion());
    }

    @Test
    void debeRechazarRecursoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberacionRecurso(
                        null,
                        EstadoRecurso.DISPONIBLE,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarEstadoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberacionRecurso(
                        UUID.randomUUID(),
                        null,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarFechaNula() {
        assertThrows(
                NullPointerException.class,
                () -> new LiberacionRecurso(
                        UUID.randomUUID(),
                        EstadoRecurso.DISPONIBLE,
                        null
                )
        );
    }
}