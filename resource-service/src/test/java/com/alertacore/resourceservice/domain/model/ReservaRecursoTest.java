package com.alertacore.resourceservice.domain.model;

import com.alertacore.resourceservice.domain.enums.EstadoReserva;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReservaRecursoTest {

    @Test
    void deberiaCrearUnaReservaActiva() {

        ReservaRecurso reserva =
                ReservaRecurso.crear(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "KEY-001",
                        10
                );

        assertNotNull(reserva.getId());

        assertEquals(
                EstadoReserva.ACTIVA,
                reserva.getEstadoReserva()
        );

        assertFalse(reserva.estaVencida());
    }

    @Test
    void deberiaMarcarUnaReservaComoVencida() {

        ReservaRecurso reserva =
                ReservaRecurso.crear(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "KEY-001",
                        10
                );

        reserva.vencer();

        assertEquals(
                EstadoReserva.VENCIDA,
                reserva.getEstadoReserva()
        );

        assertTrue(reserva.estaVencida());
    }
}