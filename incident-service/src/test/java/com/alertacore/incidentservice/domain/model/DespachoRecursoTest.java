package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DespachoRecursoTest {

    @Test
    void debeCrearDespachoValido() {
        UUID recursoId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();

        DespachoRecurso despacho =
                new DespachoRecurso(
                        recursoId,
                        incidenteId,
                        EstadoRecurso.DESPACHADO,
                        fecha
                );

        assertEquals(recursoId, despacho.recursoId());
        assertEquals(incidenteId, despacho.incidenteId());
        assertEquals(
                EstadoRecurso.DESPACHADO,
                despacho.estado()
        );
        assertEquals(fecha, despacho.fechaDespacho());
    }

    @Test
    void debeRechazarRecursoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new DespachoRecurso(
                        null,
                        UUID.randomUUID(),
                        EstadoRecurso.DESPACHADO,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarIncidenteNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new DespachoRecurso(
                        UUID.randomUUID(),
                        null,
                        EstadoRecurso.DESPACHADO,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarEstadoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new DespachoRecurso(
                        UUID.randomUUID(),
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
                () -> new DespachoRecurso(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        EstadoRecurso.DESPACHADO,
                        null
                )
        );
    }
}