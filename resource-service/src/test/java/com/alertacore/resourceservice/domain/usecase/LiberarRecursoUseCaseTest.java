package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.command.LiberarRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.LiberarRecursoDto;
import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoLiberableException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;

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
    private RecursoGateway recursoGateway;

    private LiberarRecursoUseCase useCase;

    @BeforeEach
    void setUp() {

        useCase = new LiberarRecursoUseCase(
                recursoGateway
        );
    }

    @Test
    void deberiaLiberarUnRecursoDespachado() {

        Recurso recurso = crearRecurso();

        recurso.reservar();
        recurso.despachar();

        when(
                recursoGateway.buscarPorId(
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

        verify(recursoGateway)
                .guardar(recurso);
    }

    @Test
    void deberiaLiberarUnRecursoEnAtencion() {

        Recurso recurso = crearRecurso();

        recurso.reservar();
        recurso.despachar();
        recurso.iniciarAtencion();

        when(
                recursoGateway.buscarPorId(
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

        verify(recursoGateway)
                .guardar(recurso);
    }

    @Test
    void noDeberiaLiberarUnDisponible() {

        Recurso recurso = crearRecurso();

        when(
                recursoGateway.buscarPorId(
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
                recursoGateway,
                never()
        ).guardar(any());
    }

    @Test
    void noDeberiaLiberarUnReservado() {

        Recurso recurso = crearRecurso();

        recurso.reservar();

        when(
                recursoGateway.buscarPorId(
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
                recursoGateway,
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