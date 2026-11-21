package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.domain.model.dto.RecursoDto;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarRecursosDisponiblesUseCaseTest {

    @Mock
    private RecursoGateway recursoGateway;

    private BuscarRecursosDisponiblesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BuscarRecursosDisponiblesUseCase(
                recursoGateway
        );
    }

    @Test
    void deberiaBuscarYTransformarRecursosDisponibles() {
        Recurso primerRecurso = crearRecurso(
                "AMB-001",
                "Ambulancia Principal",
                4.6579,
                -74.0596
        );

        Recurso segundoRecurso = crearRecurso(
                "AMB-002",
                "Ambulancia Secundaria",
                4.6600,
                -74.0620
        );

        when(
                recursoGateway.buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                )
        ).thenReturn(
                List.of(
                        primerRecurso,
                        segundoRecurso
                )
        );

        List<RecursoDto> resultado =
                useCase.buscar(
                        TipoRecurso.AMBULANCIA
                );

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(
                        2,
                        resultado.size()
                ),
                () -> assertEquals(
                        primerRecurso.getId(),
                        resultado.get(0).id()
                ),
                () -> assertEquals(
                        "AMB-001",
                        resultado.get(0).codigo()
                ),
                () -> assertEquals(
                        "Ambulancia Principal",
                        resultado.get(0).nombre()
                ),
                () -> assertEquals(
                        TipoRecurso.AMBULANCIA,
                        resultado.get(0).tipo()
                ),
                () -> assertEquals(
                        EstadoRecurso.DISPONIBLE,
                        resultado.get(0).estado()
                ),
                () -> assertEquals(
                        4.6579,
                        resultado.get(0).latitud()
                ),
                () -> assertEquals(
                        -74.0596,
                        resultado.get(0).longitud()
                ),
                () -> assertEquals(
                        primerRecurso
                                .getFechaDisponibleDesde(),
                        resultado.get(0)
                                .fechaDisponibleDesde()
                ),
                () -> assertEquals(
                        primerRecurso
                                .getFechaUltimaActualizacion(),
                        resultado.get(0)
                                .fechaUltimaActualizacion()
                ),
                () -> assertEquals(
                        segundoRecurso.getId(),
                        resultado.get(1).id()
                ),
                () -> assertEquals(
                        "AMB-002",
                        resultado.get(1).codigo()
                )
        );

        verify(
                recursoGateway,
                times(1)
        ).buscarDisponiblesPorTipo(
                TipoRecurso.AMBULANCIA
        );

        verifyNoMoreInteractions(recursoGateway);
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayRecursosDisponibles() {
        when(
                recursoGateway.buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                )
        ).thenReturn(List.of());

        List<RecursoDto> resultado =
                useCase.buscar(
                        TipoRecurso.AMBULANCIA
                );

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertTrue(resultado.isEmpty())
        );

        verify(recursoGateway)
                .buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                );

        verifyNoMoreInteractions(recursoGateway);
    }

    @Test
    void deberiaConsultarUnicamenteElTipoSolicitado() {
        when(
                recursoGateway.buscarDisponiblesPorTipo(
                        TipoRecurso.UNIDAD_BOMBEROS
                )
        ).thenReturn(List.of());

        List<RecursoDto> resultado =
                useCase.buscar(
                        TipoRecurso.UNIDAD_BOMBEROS
                );

        assertTrue(resultado.isEmpty());

        verify(
                recursoGateway,
                times(1)
        ).buscarDisponiblesPorTipo(
                TipoRecurso.UNIDAD_BOMBEROS
        );

        verify(
                recursoGateway,
                never()
        ).buscarDisponiblesPorTipo(
                TipoRecurso.AMBULANCIA
        );

        verifyNoMoreInteractions(recursoGateway);
    }

    @Test
    void deberiaConservarElOrdenEntregadoPorElGateway() {
        Recurso primerRecurso = crearRecurso(
                "AMB-PRIMERO",
                "Primera ambulancia",
                4.6500,
                -74.0500
        );

        Recurso segundoRecurso = crearRecurso(
                "AMB-SEGUNDO",
                "Segunda ambulancia",
                4.6700,
                -74.0700
        );

        when(
                recursoGateway.buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                )
        ).thenReturn(
                List.of(
                        segundoRecurso,
                        primerRecurso
                )
        );

        List<RecursoDto> resultado =
                useCase.buscar(
                        TipoRecurso.AMBULANCIA
                );

        assertAll(
                () -> assertEquals(
                        2,
                        resultado.size()
                ),
                () -> assertEquals(
                        "AMB-SEGUNDO",
                        resultado.get(0).codigo()
                ),
                () -> assertEquals(
                        "AMB-PRIMERO",
                        resultado.get(1).codigo()
                )
        );
    }

    private Recurso crearRecurso(
            String codigo,
            String nombre,
            double latitud,
            double longitud
    ) {
        return Recurso.crear(
                codigo,
                nombre,
                TipoRecurso.AMBULANCIA,
                UbicacionRecurso.crear(
                        latitud,
                        longitud
                )
        );
    }
}