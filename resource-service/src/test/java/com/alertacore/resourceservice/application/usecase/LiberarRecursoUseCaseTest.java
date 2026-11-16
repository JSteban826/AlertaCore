package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.command.LiberarRecursoCommand;
import com.alertacore.resourceservice.application.dto.LiberarRecursoDto;
import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoLiberableException;
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
class LiberarRecursoUseCaseTest {

    @Mock
    private RepositorioRecursoPort repositorioRecursoPort;

    private LiberarRecursoUseCase useCase;

    @BeforeEach
    void setUp() {

        useCase = new LiberarRecursoUseCase(
                repositorioRecursoPort
        );
    }

    @Test
    void deberiaLiberarUnRecursoDespachado() {

        Recurso recurso = crearRecurso();

        recurso.reservar();
        recurso.despachar();

        when(
                repositorioRecursoPort.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(Optional.of(recurso));

        LiberarRecursoDto resultado =
                useCase.liberar(
                        crearCommand(
                                recurso.getId()
                        )
                );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                resultado.estado()
        );

        verify(repositorioRecursoPort)
                .guardar(recurso);
    }

    @Test
    void deberiaLiberarUnRecursoEnAtencion() {

        Recurso recurso = crearRecurso();

        recurso.reservar();
        recurso.despachar();
        recurso.iniciarAtencion();

        when(
                repositorioRecursoPort.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(Optional.of(recurso));

        LiberarRecursoDto resultado =
                useCase.liberar(
                        crearCommand(
                                recurso.getId()
                        )
                );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                resultado.estado()
        );

        verify(repositorioRecursoPort)
                .guardar(recurso);
    }

    @Test
    void noDeberiaLiberarUnDisponible() {

        Recurso recurso = crearRecurso();

        when(
                repositorioRecursoPort.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(Optional.of(recurso));

        assertThrows(
                RecursoNoLiberableException.class,
                () -> useCase.liberar(
                        crearCommand(
                                recurso.getId()
                        )
                )
        );

        verify(
                repositorioRecursoPort,
                never()
        ).guardar(any());
    }

    @Test
    void noDeberiaLiberarUnReservado() {

        Recurso recurso = crearRecurso();

        recurso.reservar();

        when(
                repositorioRecursoPort.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(Optional.of(recurso));

        assertThrows(
                RecursoNoLiberableException.class,
                () -> useCase.liberar(
                        crearCommand(
                                recurso.getId()
                        )
                )
        );

        verify(
                repositorioRecursoPort,
                never()
        ).guardar(any());
    }

    private Recurso crearRecurso() {

        return Recurso.crear(
                "AMB-001",
                "Ambulancia Principal",
                TipoRecurso.AMBULANCIA,
                UbicacionRecurso.crear(
                        4.6579,
                        -74.0596
                )
        );
    }

    private LiberarRecursoCommand crearCommand(
            UUID recursoId
    ) {

        return new LiberarRecursoCommand(
                recursoId,
                UUID.randomUUID(),
                "Atencion finalizada"
        );
    }
}