package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import com.alertacore.incidentservice.domain.enums.TipoRecurso;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecursoDisponibleTest {

    @Test
    void debeCrearRecursoDisponibleValido() {
        UUID recursoId = UUID.randomUUID();

        RecursoDisponible recurso =
                new RecursoDisponible(
                        recursoId,
                        "AMB-001",
                        TipoRecurso.AMBULANCIA,
                        4.8,
                        12,
                        EstadoRecurso.DISPONIBLE
                );

        assertEquals(
                recursoId,
                recurso.recursoId()
        );

        assertEquals(
                "AMB-001",
                recurso.codigo()
        );

        assertEquals(
                TipoRecurso.AMBULANCIA,
                recurso.tipo()
        );

        assertEquals(
                4.8,
                recurso.distanciaKm()
        );

        assertEquals(
                12,
                recurso.tiempoEstimadoLlegadaMinutos()
        );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                recurso.estado()
        );
    }

    @Test
    void debePermitirDistanciaYTiempoCero() {
        RecursoDisponible recurso =
                new RecursoDisponible(
                        UUID.randomUUID(),
                        "AMB-002",
                        TipoRecurso.AMBULANCIA,
                        0.0,
                        0,
                        EstadoRecurso.DISPONIBLE
                );

        assertEquals(
                0.0,
                recurso.distanciaKm()
        );

        assertEquals(
                0,
                recurso.tiempoEstimadoLlegadaMinutos()
        );
    }

    @Test
    void debeRechazarIdentificadorNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new RecursoDisponible(
                        null,
                        "AMB-001",
                        TipoRecurso.AMBULANCIA,
                        4.8,
                        12,
                        EstadoRecurso.DISPONIBLE
                )
        );
    }

    @Test
    void debeRechazarCodigoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new RecursoDisponible(
                        UUID.randomUUID(),
                        null,
                        TipoRecurso.AMBULANCIA,
                        4.8,
                        12,
                        EstadoRecurso.DISPONIBLE
                )
        );
    }

    @Test
    void debeRechazarTipoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new RecursoDisponible(
                        UUID.randomUUID(),
                        "AMB-001",
                        null,
                        4.8,
                        12,
                        EstadoRecurso.DISPONIBLE
                )
        );
    }

    @Test
    void debeRechazarEstadoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new RecursoDisponible(
                        UUID.randomUUID(),
                        "AMB-001",
                        TipoRecurso.AMBULANCIA,
                        4.8,
                        12,
                        null
                )
        );
    }

    @Test
    void debeRechazarDistanciaNegativa() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new RecursoDisponible(
                                UUID.randomUUID(),
                                "AMB-001",
                                TipoRecurso.AMBULANCIA,
                                -0.1,
                                12,
                                EstadoRecurso.DISPONIBLE
                        )
                );

        assertEquals(
                "La distancia no puede ser negativa",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarTiempoEstimadoNegativo() {
        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new RecursoDisponible(
                                UUID.randomUUID(),
                                "AMB-001",
                                TipoRecurso.AMBULANCIA,
                                4.8,
                                -1,
                                EstadoRecurso.DISPONIBLE
                        )
                );

        assertEquals(
                "El tiempo estimado no puede ser negativo",
                excepcion.getMessage()
        );
    }
}