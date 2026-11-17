package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.command.ActualizarUbicacionRecursoCommand;
import com.alertacore.resourceservice.application.dto.ActualizarUbicacionRecursoDto;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoEncontradoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
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
class ActualizarUbicacionRecursoUseCaseTest {

    @Mock
    private RepositorioRecursoPort repositorioRecursoPort;

    private ActualizarUbicacionRecursoUseCase useCase;

    @BeforeEach
    void setUp() {

        useCase =
                new ActualizarUbicacionRecursoUseCase(
                        repositorioRecursoPort
                );
    }

    @Test
    void deberiaActualizarUbicacion() {

        Recurso recurso = crearRecurso();

        when(
                repositorioRecursoPort.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(Optional.of(recurso));

        ActualizarUbicacionRecursoDto dto =
                useCase.actualizar(
                        new ActualizarUbicacionRecursoCommand(
                                recurso.getId(),
                                4.6612,
                                -74.0634
                        )
                );

        assertEquals(
                4.6612,
                dto.latitud()
        );

        assertEquals(
                -74.0634,
                dto.longitud()
        );

        verify(repositorioRecursoPort)
                .guardar(recurso);
    }

    @Test
    void deberiaFallarSiNoExiste() {

        UUID id = UUID.randomUUID();

        when(
                repositorioRecursoPort.buscarPorId(id)
        ).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () ->
                        useCase.actualizar(
                                new ActualizarUbicacionRecursoCommand(
                                        id,
                                        4.6612,
                                        -74.0634
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
}