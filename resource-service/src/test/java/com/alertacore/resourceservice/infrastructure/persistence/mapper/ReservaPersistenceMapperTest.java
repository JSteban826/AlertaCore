package com.alertacore.resourceservice.infrastructure.persistence.mapper;

import com.alertacore.resourceservice.domain.enums.EstadoReserva;
import com.alertacore.resourceservice.domain.model.ReservaRecurso;
import com.alertacore.resourceservice.infrastructure.persistence.entity.ReservaRecursoEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReservaPersistenceMapperTest {

    private ReservaPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ReservaPersistenceMapper();
    }

    @Test
    void deberiaConvertirAEntity() {

        ReservaRecurso reserva =
                ReservaRecurso.crear(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "KEY-001",
                        10
                );

        ReservaRecursoEntity entity =
                mapper.toEntity(reserva);

        assertNotNull(entity);

        assertEquals(
                reserva.getId(),
                entity.getId()
        );

        assertEquals(
                reserva.getRecursoId(),
                entity.getRecursoId()
        );

        assertEquals(
                reserva.getIncidenteId(),
                entity.getIncidenteId()
        );

        assertEquals(
                reserva.getClaveIdempotencia(),
                entity.getClaveIdempotencia()
        );

        assertEquals(
                reserva.getEstadoReserva().name(),
                entity.getEstado()
        );
    }

    @Test
    void deberiaConvertirADomain() {

        ReservaRecursoEntity entity =
                new ReservaRecursoEntity();

        entity.setId(
                UUID.randomUUID()
        );

        entity.setRecursoId(
                UUID.randomUUID()
        );

        entity.setIncidenteId(
                UUID.randomUUID()
        );

        entity.setClaveIdempotencia(
                "KEY-001"
        );

        entity.setFechaReserva(
                java.time.LocalDateTime.now()
        );

        entity.setFechaVencimiento(
                java.time.LocalDateTime.now()
                        .plusMinutes(10)
        );

        entity.setEstado(
                EstadoReserva.ACTIVA.name()
        );

        ReservaRecurso reserva =
                mapper.toDomain(entity);

        assertNotNull(reserva);

        assertEquals(
                "KEY-001",
                reserva.getClaveIdempotencia()
        );

        assertEquals(
                EstadoReserva.ACTIVA,
                reserva.getEstadoReserva()
        );
    }
}