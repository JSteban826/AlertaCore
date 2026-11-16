package com.alertacore.resourceservice.application.usecase;

import com.alertacore.resourceservice.application.dto.ReporteUtilizacionDto;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerarReporteUtilizacionUseCaseTest {

    @Mock
    private RepositorioRecursoPort repositorio;

    private GenerarReporteUtilizacionUseCase useCase;

    @BeforeEach
    void setUp() {

        useCase =
                new GenerarReporteUtilizacionUseCase(
                        repositorio
                );
    }

    @Test
    void deberiaGenerarReporte() {

        Recurso disponible = crear();

        Recurso reservado = crear();
        reservado.reservar();

        Recurso despachado = crear();
        despachado.reservar();
        despachado.despachar();

        Recurso enAtencion = crear();
        enAtencion.reservar();
        enAtencion.despachar();
        enAtencion.iniciarAtencion();

        Recurso fuera = crear();
        fuera.ponerFueraDeServicio();

        when(repositorio.buscarTodos())
                .thenReturn(
                        List.of(
                                disponible,
                                reservado,
                                despachado,
                                enAtencion,
                                fuera
                        )
                );

        ReporteUtilizacionDto dto =
                useCase.generar();

        assertEquals(5,
                dto.totalRecursos());

        assertEquals(1,
                dto.disponibles());

        assertEquals(1,
                dto.reservados());

        assertEquals(1,
                dto.despachados());

        assertEquals(1,
                dto.enAtencion());

        assertEquals(1,
                dto.fueraDeServicio());

        assertEquals(
                75.0,
                dto.porcentajeUtilizacion(),
                0.01
        );
    }

    private Recurso crear() {

        return Recurso.crear(
                java.util.UUID.randomUUID()
                        .toString(),
                "Resource",
                TipoRecurso.AMBULANCIA,
                UbicacionRecurso.crear(
                        4.6579,
                        -74.0596
                )
        );
    }
}