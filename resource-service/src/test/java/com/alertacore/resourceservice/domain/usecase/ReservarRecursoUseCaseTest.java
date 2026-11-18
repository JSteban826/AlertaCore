package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.command.ReservarRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.ReservaRecursoDto;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoDisponibleException;
import com.alertacore.resourceservice.domain.exception.RecursoYaReservadoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.ReservaRecurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import com.alertacore.resourceservice.domain.model.gateway.ReservaRecursoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservarRecursoUseCaseTest {

    @Mock
    private RecursoGateway recursoGateway;

    @Mock
    private ReservaRecursoGateway reservaRecursoGateway;

    private ReservarRecursoUseCase useCase;

    @BeforeEach
    void setUp() {

        useCase =
                new ReservarRecursoUseCase(
                        recursoGateway,
                        reservaRecursoGateway
                );
    }

    @Test
    void deberiaReservarUnRecursoDisponible() {

        Recurso recurso = crearRecurso();

        when(reservaRecursoGateway
                .buscarPorClaveIdempotencia(any()))
                .thenReturn(Optional.empty());

        when(recursoGateway
                .buscarPorId(recurso.getId()))
                .thenReturn(Optional.of(recurso));

        when(reservaRecursoGateway
                .buscarReservaActivaPorRecurso(
                        recurso.getId()))
                .thenReturn(Optional.empty());

        when(reservaRecursoGateway
                .guardar(any()))
                .thenAnswer(i -> i.getArgument(0));

        ReservaRecursoDto resultado =
                useCase.reservar(crearCommand(
                        recurso.getId()
                ));

        assertNotNull(resultado.reservaId());

        verify(recursoGateway)
                .guardar(any());
    }

    @Test
    void deberiaSerIdempotente() {

        ReservaRecurso reserva =
                ReservaRecurso.crear(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "KEY-001",
                        10
                );

        when(reservaRecursoGateway
                .buscarPorClaveIdempotencia(
                        "KEY-001"))
                .thenReturn(Optional.of(reserva));

        ReservaRecursoDto dto =
                useCase.reservar(
                        crearCommand(UUID.randomUUID())
                );

        assertEquals(
                reserva.getId(),
                dto.reservaId()
        );

        verifyNoInteractions(
                recursoGateway
        );
    }

    @Test
    void deberiaRechazarRecursoNoDisponible() {

        Recurso recurso = crearRecurso();

        recurso.reservar();

        when(reservaRecursoGateway
                .buscarPorClaveIdempotencia(any()))
                .thenReturn(Optional.empty());

        when(recursoGateway
                .buscarPorId(recurso.getId()))
                .thenReturn(Optional.of(recurso));

        assertThrows(
                RecursoNoDisponibleException.class,
                () ->
                        useCase.reservar(
                                crearCommand(
                                        recurso.getId()
                                )
                        )
        );
    }

    @Test
    void deberiaRechazarReservaDuplicada() {

        Recurso recurso = crearRecurso();

        ReservaRecurso reserva =
                ReservaRecurso.crear(
                        recurso.getId(),
                        UUID.randomUUID(),
                        "KEY-X",
                        10
                );

        when(reservaRecursoGateway
                .buscarPorClaveIdempotencia(any()))
                .thenReturn(Optional.empty());

        when(recursoGateway
                .buscarPorId(recurso.getId()))
                .thenReturn(Optional.of(recurso));

        when(reservaRecursoGateway
                .buscarReservaActivaPorRecurso(
                        recurso.getId()))
                .thenReturn(Optional.of(reserva));

        assertThrows(
                RecursoYaReservadoException.class,
                () ->
                        useCase.reservar(
                                crearCommand(
                                        recurso.getId()
                                )
                        )
        );
    }

    private Recurso crearRecurso() {

        return Recurso.crear(
                "AMB-001",
                "Ambulancia",
                TipoRecurso.AMBULANCIA,
                UbicacionRecurso.crear(
                        4.6579,
                        -74.0596
                )
        );
    }

    private ReservarRecursoCommand crearCommand(
            UUID recursoId
    ) {

        return new ReservarRecursoCommand(
                recursoId,
                UUID.randomUUID(),
                "KEY-001",
                10
        );
    }
}