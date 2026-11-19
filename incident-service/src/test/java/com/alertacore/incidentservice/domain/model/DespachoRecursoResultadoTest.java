package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DespachoRecursoResultadoTest {

    @Test
    void debeConvertirDespachoEnResultado() {
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

        DespachoRecursoResultado resultado =
                DespachoRecursoResultado.desde(
                        despacho,
                        EstadoIncidente.RECURSO_ASIGNADO
                );

        assertEquals(
                incidenteId,
                resultado.incidenteId()
        );

        assertEquals(
                recursoId,
                resultado.recursoId()
        );

        assertEquals(
                EstadoRecurso.DESPACHADO,
                resultado.estadoRecurso()
        );

        assertEquals(
                EstadoIncidente.RECURSO_ASIGNADO,
                resultado.estadoIncidente()
        );

        assertEquals(
                fecha,
                resultado.fechaDespacho()
        );
    }

    @Test
    void debeRechazarDespachoNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () ->
                                DespachoRecursoResultado.desde(
                                        null,
                                        EstadoIncidente.RECURSO_ASIGNADO
                                )
                );

        assertEquals(
                "El despacho es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarIncidenteIdNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new DespachoRecursoResultado(
                        null,
                        UUID.randomUUID(),
                        EstadoRecurso.DESPACHADO,
                        EstadoIncidente.RECURSO_ASIGNADO,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarRecursoIdNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new DespachoRecursoResultado(
                        UUID.randomUUID(),
                        null,
                        EstadoRecurso.DESPACHADO,
                        EstadoIncidente.RECURSO_ASIGNADO,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarEstadoRecursoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new DespachoRecursoResultado(
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
                () -> new DespachoRecursoResultado(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        EstadoRecurso.DESPACHADO,
                        null,
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void debeRechazarFechaNula() {
        assertThrows(
                NullPointerException.class,
                () -> new DespachoRecursoResultado(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        EstadoRecurso.DESPACHADO,
                        EstadoIncidente.RECURSO_ASIGNADO,
                        null
                )
        );
    }
}