package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.command.DespacharRecursoCommand;
import com.alertacore.resourceservice.application.dto.DespachoRecursoDto;
import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoReservadoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioDespachoRecursoPort;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;
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
class DespacharRecursoUseCaseTest {

    @Mock
    private RepositorioRecursoPort repositorioRecursoPort;

    @Mock
    private RepositorioDespachoRecursoPort
            repositorioDespachoRecursoPort;

    private DespacharRecursoUseCase useCase;

    @BeforeEach
    void setUp() {

        useCase = new DespacharRecursoUseCase(
                repositorioRecursoPort,
                repositorioDespachoRecursoPort
        );
    }

    @Test
    void deberiaDespacharUnRecursoReservado() {

        Recurso recurso = crearRecurso();

        recurso.reservar();

        when(repositorioRecursoPort
                .buscarPorId(recurso.getId()))
                .thenReturn(Optional.of(recurso));

        when(repositorioDespachoRecursoPort
                .guardar(any()))
                .thenAnswer(i -> i.getArgument(0));

        DespachoRecursoDto dto =
                useCase.despachar(
                        crearCommand(recurso.getId())
                );

        assertEquals(
                EstadoRecurso.DESPACHADO,
                dto.estado()
        );
    }

    @Test
    void noDeberiaDespacharUnDisponible() {

        Recurso recurso = crearRecurso();

        when(repositorioRecursoPort
                .buscarPorId(recurso.getId()))
                .thenReturn(Optional.of(recurso));

        assertThrows(
                RecursoNoReservadoException.class,
                () -> useCase.despachar(
                        crearCommand(recurso.getId())
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

    private DespacharRecursoCommand crearCommand(
            UUID recursoId
    ) {

        return new DespacharRecursoCommand(
                recursoId,
                UUID.randomUUID(),
                "OPERADOR-001"
        );
    }
}