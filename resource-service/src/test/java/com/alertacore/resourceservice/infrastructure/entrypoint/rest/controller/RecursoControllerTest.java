package com.alertacore.resourceservice.infrastructure.entrypoint.rest.controller;

import com.alertacore.resourceservice.domain.model.dto.*;
import com.alertacore.resourceservice.domain.usecase.*;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;

import com.alertacore.resourceservice.infrastructure.entrypoint.rest.request.*;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecursoControllerTest {

    @Mock
    private RegistrarRecursoUseCase registrarRecursoUseCase;

    @Mock
    private BuscarRecursoMasCercanoUseCase buscarRecursoMasCercanoUseCase;

    @Mock
    private ReservarRecursoUseCase reservarRecursoUseCase;

    @Mock
    private DespacharRecursoUseCase despacharRecursoUseCase;

    @Mock
    private LiberarRecursoUseCase liberarRecursoUseCase;

    @Mock
    private ActualizarUbicacionRecursoUseCase actualizarUbicacionRecursoUseCase;

    @Mock
    private GenerarReporteUtilizacionUseCase generarReporteUtilizacionUseCase;

    @Mock
    private ConsultarRecursoUseCase consultarRecursoUseCase;

    @Mock
    private BuscarRecursosDisponiblesUseCase buscarRecursosDisponiblesUseCase;

    @Mock
    private ListarRecursosUseCase listarRecursosUseCase;

    private RecursoController controller;

    @BeforeEach
    void setUp() {

        controller =
                new RecursoController(
                        registrarRecursoUseCase,
                        consultarRecursoUseCase,
                        listarRecursosUseCase,
                        buscarRecursosDisponiblesUseCase,
                        buscarRecursoMasCercanoUseCase,
                        reservarRecursoUseCase,
                        despacharRecursoUseCase,
                        liberarRecursoUseCase,
                        actualizarUbicacionRecursoUseCase,
                        generarReporteUtilizacionUseCase
                );
    }

    @Test
    void deberiaRegistrar() {

        UUID id = UUID.randomUUID();

        when(registrarRecursoUseCase.registrar(any()))
                .thenReturn(
                        new RecursoDto(
                                id,
                                "AMB-001",
                                "Ambulancia",
                                TipoRecurso.AMBULANCIA,
                                EstadoRecurso.DISPONIBLE,
                                4.0,
                                -74.0,
                                LocalDateTime.now(),
                                LocalDateTime.now()
                        )
                );

        RecursoResponse response =
                controller.registrar(
                        new RegistrarRecursoRequest(
                                "AMB-001",
                                "Ambulancia",
                                TipoRecurso.AMBULANCIA,
                                4.0,
                                -74.0
                        )
                );

        assertEquals(
                "AMB-001",
                response.codigo()
        );
    }

    @Test
    void deberiaConsultar() {

        UUID id = UUID.randomUUID();

        when(consultarRecursoUseCase.consultar(id))
                .thenReturn(
                        new RecursoDto(
                                id,
                                "AMB-001",
                                "Ambulancia",
                                TipoRecurso.AMBULANCIA,
                                EstadoRecurso.DISPONIBLE,
                                4.0,
                                -74.0,
                                LocalDateTime.now(),
                                LocalDateTime.now()
                        )
                );

        RecursoResponse response =
                controller.consultar(id);

        assertEquals(
                id,
                response.id()
        );
    }

    @Test
    void deberiaListar() {

        when(listarRecursosUseCase.listar())
                .thenReturn(List.of());

        assertNotNull(
                controller.listar()
        );
    }

    @Test
    void deberiaListarDisponibles() {

        when(
                buscarRecursosDisponiblesUseCase.buscar(
                        TipoRecurso.AMBULANCIA
                )
        ).thenReturn(List.of());

        assertNotNull(
                controller.disponibles(
                        TipoRecurso.AMBULANCIA
                )
        );
    }

    @Test
    void deberiaGenerarReporte() {

        when(
                generarReporteUtilizacionUseCase.generar()
        ).thenReturn(
                new ReporteUtilizacionDto(
                        10,
                        5,
                        1,
                        2,
                        1,
                        1,
                        40
                )
        );

        ReporteUtilizacionDto dto =
                controller.reporte();

        assertEquals(
                10,
                dto.totalRecursos()
        );
    }

    @Test
    void deberiaBuscarMasCercano() {

        UUID recursoId = UUID.randomUUID();

        when(buscarRecursoMasCercanoUseCase.buscar(any()))
                .thenReturn(
                        new RecursoCercanoDto(
                                recursoId,
                                "AMB-001",
                                TipoRecurso.AMBULANCIA,
                                2.5,
                                5,
                                EstadoRecurso.DISPONIBLE
                        )
                );

        BuscarRecursoMasCercanoRequest request =
                new BuscarRecursoMasCercanoRequest(
                        UUID.randomUUID(),
                        TipoRecurso.AMBULANCIA,
                        4.6579,
                        -74.0596,
                        30
                );

        RecursoCercanoResponse response =
                controller.buscarMasCercano(
                        request
                );

        assertEquals(
                "AMB-001",
                response.codigo()
        );
    }

    @Test
    void deberiaReservar() {

        UUID recursoId = UUID.randomUUID();

        when(reservarRecursoUseCase.reservar(any()))
                .thenReturn(
                        new ReservaRecursoDto(
                                UUID.randomUUID(),
                                recursoId,
                                UUID.randomUUID(),
                                EstadoRecurso.RESERVADO,
                                LocalDateTime.now(),
                                LocalDateTime.now()
                                        .plusMinutes(10)
                        )
                );

        ReservaRecursoResponse response =
                controller.reservar(
                        recursoId,
                        "KEY-001",
                        new ReservarRecursoRequest(
                                UUID.randomUUID(),
                                10
                        )
                );

        assertEquals(
                EstadoRecurso.RESERVADO,
                response.estadoRecurso()
        );
    }

    @Test
    void deberiaDespachar() {

        UUID recursoId = UUID.randomUUID();

        when(despacharRecursoUseCase.despachar(any()))
                .thenReturn(
                        new DespachoRecursoDto(
                                recursoId,
                                UUID.randomUUID(),
                                EstadoRecurso.DESPACHADO,
                                LocalDateTime.now()
                        )
                );

        DespachoRecursoResponse response =
                controller.despachar(
                        recursoId,
                        new DespacharRecursoRequest(
                                UUID.randomUUID(),
                                "OPERADOR-001"
                        )
                );

        assertEquals(
                EstadoRecurso.DESPACHADO,
                response.estado()
        );
    }

    @Test
    void deberiaLiberar() {

        UUID recursoId = UUID.randomUUID();

        when(liberarRecursoUseCase.liberar(any()))
                .thenReturn(
                        new LiberarRecursoDto(
                                recursoId,
                                EstadoRecurso.DISPONIBLE,
                                LocalDateTime.now()
                        )
                );

        LiberarRecursoResponse response =
                controller.liberar(
                        recursoId,
                        new LiberarRecursoRequest(
                                UUID.randomUUID(),
                                "FINALIZADO"
                        )
                );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                response.estado()
        );
    }

    @Test
    void deberiaActualizarUbicacion() {

        UUID recursoId = UUID.randomUUID();

        when(
                actualizarUbicacionRecursoUseCase.actualizar(
                        any()
                )
        ).thenReturn(
                new ActualizarUbicacionRecursoDto(
                        recursoId,
                        4.6,
                        -74.0,
                        LocalDateTime.now()
                )
        );

        ActualizarUbicacionResponse response =
                controller.actualizarUbicacion(
                        recursoId,
                        new ActualizarUbicacionRequest(
                                4.6,
                                -74.0
                        )
                );

        assertEquals(
                recursoId,
                response.recursoId()
        );
    }


}