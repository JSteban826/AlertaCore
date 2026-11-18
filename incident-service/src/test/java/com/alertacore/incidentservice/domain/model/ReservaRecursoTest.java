package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoRecurso;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservaRecursoTest {

    @Test
    void debeCrearReservaValida() {
        UUID reservaId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();

        LocalDateTime fechaReserva =
                LocalDateTime.of(
                        2026,
                        11,
                        18,
                        10,
                        0
                );

        LocalDateTime fechaVencimiento =
                fechaReserva.plusMinutes(10);

        ReservaRecurso reserva =
                new ReservaRecurso(
                        reservaId,
                        recursoId,
                        incidenteId,
                        EstadoRecurso.RESERVADO,
                        fechaReserva,
                        fechaVencimiento
                );

        assertEquals(
                reservaId,
                reserva.reservaId()
        );

        assertEquals(
                recursoId,
                reserva.recursoId()
        );

        assertEquals(
                incidenteId,
                reserva.incidenteId()
        );

        assertEquals(
                EstadoRecurso.RESERVADO,
                reserva.estadoRecurso()
        );

        assertEquals(
                fechaReserva,
                reserva.fechaReserva()
        );

        assertEquals(
                fechaVencimiento,
                reserva.fechaVencimiento()
        );
    }

    @Test
    void debeRechazarReservaIdNulo() {
        assertThrows(
                NullPointerException.class,
                () -> crearReserva(
                        null,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        EstadoRecurso.RESERVADO,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                                .plusMinutes(10)
                )
        );
    }

    @Test
    void debeRechazarRecursoIdNulo() {
        assertThrows(
                NullPointerException.class,
                () -> crearReserva(
                        UUID.randomUUID(),
                        null,
                        UUID.randomUUID(),
                        EstadoRecurso.RESERVADO,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                                .plusMinutes(10)
                )
        );
    }

    @Test
    void debeRechazarIncidenteIdNulo() {
        assertThrows(
                NullPointerException.class,
                () -> crearReserva(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        EstadoRecurso.RESERVADO,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                                .plusMinutes(10)
                )
        );
    }

    @Test
    void debeRechazarEstadoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> crearReserva(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                                .plusMinutes(10)
                )
        );
    }

    @Test
    void debeRechazarFechaReservaNula() {
        assertThrows(
                NullPointerException.class,
                () -> crearReserva(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        EstadoRecurso.RESERVADO,
                        null,
                        LocalDateTime.now()
                                .plusMinutes(10)
                )
        );
    }

    @Test
    void debeRechazarFechaVencimientoNula() {
        assertThrows(
                NullPointerException.class,
                () -> crearReserva(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        EstadoRecurso.RESERVADO,
                        LocalDateTime.now(),
                        null
                )
        );
    }

    private ReservaRecurso crearReserva(
            UUID reservaId,
            UUID recursoId,
            UUID incidenteId,
            EstadoRecurso estado,
            LocalDateTime fechaReserva,
            LocalDateTime fechaVencimiento
    ) {
        return new ReservaRecurso(
                reservaId,
                recursoId,
                incidenteId,
                estado,
                fechaReserva,
                fechaVencimiento
        );
    }
}