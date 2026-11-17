package com.alertacore.resourceservice.infrastructure.persistence.adapter;

import com.alertacore.resourceservice.domain.enums.EstadoReserva;
import com.alertacore.resourceservice.domain.model.ReservaRecurso;

import com.alertacore.resourceservice.infrastructure.persistence.entity.ReservaRecursoEntity;
import com.alertacore.resourceservice.infrastructure.persistence.mapper.ReservaPersistenceMapper;
import com.alertacore.resourceservice.infrastructure.persistence.repository.ReservaRecursoJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaPersistenceAdapterTest {

    @Mock
    private ReservaRecursoJpaRepository repository;

    private ReservaPersistenceMapper mapper;

    @InjectMocks
    private ReservaPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {

        mapper = new ReservaPersistenceMapper();

        adapter =
                new ReservaPersistenceAdapter(
                        repository,
                        mapper
                );
    }

    @Test
    void deberiaGuardarReserva() {

        ReservaRecurso reserva =
                ReservaRecurso.crear(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "KEY-001",
                        10
                );

        ReservaRecursoEntity entity =
                mapper.toEntity(reserva);

        when(repository.save(any()))
                .thenReturn(entity);

        ReservaRecurso resultado =
                adapter.guardar(reserva);

        assertNotNull(resultado);

        assertEquals(
                reserva.getId(),
                resultado.getId()
        );

        verify(repository)
                .save(any());
    }

    @Test
    void deberiaBuscarReservaActivaPorRecurso() {

        UUID recursoId =
                UUID.randomUUID();

        ReservaRecurso reserva =
                ReservaRecurso.crear(
                        recursoId,
                        UUID.randomUUID(),
                        "KEY-001",
                        10
                );

        ReservaRecursoEntity entity =
                mapper.toEntity(reserva);

        when(
                repository.findByRecursoIdAndEstado(
                        recursoId,
                        EstadoReserva.ACTIVA.name()
                )
        ).thenReturn(
                Optional.of(entity)
        );

        Optional<ReservaRecurso> resultado =
                adapter.buscarReservaActivaPorRecurso(
                        recursoId
                );

        assertTrue(
                resultado.isPresent()
        );

        assertEquals(
                recursoId,
                resultado.get().getRecursoId()
        );
    }

    @Test
    void deberiaBuscarPorClaveIdempotencia() {

        ReservaRecurso reserva =
                ReservaRecurso.crear(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "KEY-001",
                        10
                );

        ReservaRecursoEntity entity =
                mapper.toEntity(reserva);

        when(
                repository.findByClaveIdempotencia(
                        "KEY-001"
                )
        ).thenReturn(
                Optional.of(entity)
        );

        Optional<ReservaRecurso> resultado =
                adapter.buscarPorClaveIdempotencia(
                        "KEY-001"
                );

        assertTrue(
                resultado.isPresent()
        );

        assertEquals(
                "KEY-001",
                resultado.get()
                        .getClaveIdempotencia()
        );
    }
}