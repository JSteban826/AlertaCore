package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.RecursoNoEncontradoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.model.dto.RecursoDto;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarRecursoUseCaseTest {

    @Mock
    private RecursoGateway recursoGateway;

    private ConsultarRecursoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarRecursoUseCase(
                recursoGateway
        );
    }

    @Test
    void deberiaConsultarYTransformarElRecurso() {
        Recurso recurso = Recurso.crear(
                "AMB-001",
                "Ambulancia Principal",
                TipoRecurso.AMBULANCIA,
                UbicacionRecurso.crear(
                        4.6579,
                        -74.0596
                )
        );

        UUID recursoId = recurso.getId();

        when(
                recursoGateway.buscarPorId(recursoId)
        ).thenReturn(
                Optional.of(recurso)
        );

        RecursoDto resultado =
                useCase.consultar(recursoId);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(
                        recurso.getId(),
                        resultado.id()
                ),
                () -> assertEquals(
                        recurso.getCodigo(),
                        resultado.codigo()
                ),
                () -> assertEquals(
                        recurso.getNombre(),
                        resultado.nombre()
                ),
                () -> assertEquals(
                        TipoRecurso.AMBULANCIA,
                        resultado.tipo()
                ),
                () -> assertEquals(
                        EstadoRecurso.DISPONIBLE,
                        resultado.estado()
                ),
                () -> assertEquals(
                        recurso.getUbicacion()
                                .getLatitud(),
                        resultado.latitud()
                ),
                () -> assertEquals(
                        recurso.getUbicacion()
                                .getLongitud(),
                        resultado.longitud()
                ),
                () -> assertEquals(
                        recurso.getFechaDisponibleDesde(),
                        resultado.fechaDisponibleDesde()
                ),
                () -> assertEquals(
                        recurso.getFechaUltimaActualizacion(),
                        resultado.fechaUltimaActualizacion()
                )
        );

        verify(
                recursoGateway,
                times(1)
        ).buscarPorId(recursoId);

        verifyNoMoreInteractions(recursoGateway);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElRecursoNoExiste() {
        UUID recursoId = UUID.randomUUID();

        when(
                recursoGateway.buscarPorId(recursoId)
        ).thenReturn(Optional.empty());

        RecursoNoEncontradoException excepcion =
                assertThrows(
                        RecursoNoEncontradoException.class,
                        () -> useCase.consultar(
                                recursoId
                        )
                );

        assertAll(
                () -> assertNotNull(excepcion),
                () -> assertEquals(
                        "RECURSO_NO_ENCONTRADO",
                        excepcion.getCodigo()
                ),
                () -> assertNotNull(
                        excepcion.getMessage()
                )
        );

        verify(
                recursoGateway,
                times(1)
        ).buscarPorId(recursoId);

        verifyNoMoreInteractions(recursoGateway);
    }

    @Test
    void noDeberiaRealizarOtrasConsultasCuandoEncuentraElRecurso() {
        Recurso recurso = Recurso.crear(
                "AMB-002",
                "Ambulancia Secundaria",
                TipoRecurso.AMBULANCIA,
                UbicacionRecurso.crear(
                        4.6600,
                        -74.0620
                )
        );

        when(
                recursoGateway.buscarPorId(
                        recurso.getId()
                )
        ).thenReturn(
                Optional.of(recurso)
        );

        useCase.consultar(recurso.getId());

        verify(recursoGateway)
                .buscarPorId(recurso.getId());

        verify(
                recursoGateway,
                never()
        ).buscarTodos();

        verify(
                recursoGateway,
                never()
        ).buscarPorCodigo(recurso.getCodigo());

        verifyNoMoreInteractions(recursoGateway);
    }

    @Test
    void noDeberiaRealizarOtrasConsultasCuandoElRecursoNoExiste() {
        UUID recursoId = UUID.randomUUID();

        when(
                recursoGateway.buscarPorId(recursoId)
        ).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> useCase.consultar(recursoId)
        );

        verify(recursoGateway)
                .buscarPorId(recursoId);

        verify(
                recursoGateway,
                never()
        ).buscarTodos();

        verifyNoMoreInteractions(recursoGateway);
    }
}