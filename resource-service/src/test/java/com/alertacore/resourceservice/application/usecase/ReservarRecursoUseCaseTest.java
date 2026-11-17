package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.command.ReservarRecursoCommand;
import com.alertacore.resourceservice.application.dto.ReservaRecursoDto;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoDisponibleException;
import com.alertacore.resourceservice.domain.exception.RecursoYaReservadoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.ReservaRecurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;
import com.alertacore.resourceservice.domain.port.out.RepositorioReservaRecursoPort;
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
    private RepositorioRecursoPort repositorioRecursoPort;

    @Mock
    private RepositorioReservaRecursoPort repositorioReservaRecursoPort;

    private ReservarRecursoUseCase useCase;

    @BeforeEach
    void setUp() {

        useCase =
                new ReservarRecursoUseCase(
                        repositorioRecursoPort,
                        repositorioReservaRecursoPort
                );
    }

    @Test
    void deberiaReservarUnRecursoDisponible() {

        Recurso recurso = crearRecurso();

        when(repositorioReservaRecursoPort
                .buscarPorClaveIdempotencia(any()))
                .thenReturn(Optional.empty());

        when(repositorioRecursoPort
                .buscarPorId(recurso.getId()))
                .thenReturn(Optional.of(recurso));

        when(repositorioReservaRecursoPort
                .buscarReservaActivaPorRecurso(
                        recurso.getId()))
                .thenReturn(Optional.empty());

        when(repositorioReservaRecursoPort
                .guardar(any()))
                .thenAnswer(i -> i.getArgument(0));

        ReservaRecursoDto resultado =
                useCase.reservar(crearCommand(
                        recurso.getId()
                ));

        assertNotNull(resultado.reservaId());

        verify(repositorioRecursoPort)
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

        when(repositorioReservaRecursoPort
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
                repositorioRecursoPort
        );
    }

    @Test
    void deberiaRechazarRecursoNoDisponible() {

        Recurso recurso = crearRecurso();

        recurso.reservar();

        when(repositorioReservaRecursoPort
                .buscarPorClaveIdempotencia(any()))
                .thenReturn(Optional.empty());

        when(repositorioRecursoPort
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

        when(repositorioReservaRecursoPort
                .buscarPorClaveIdempotencia(any()))
                .thenReturn(Optional.empty());

        when(repositorioRecursoPort
                .buscarPorId(recurso.getId()))
                .thenReturn(Optional.of(recurso));

        when(repositorioReservaRecursoPort
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