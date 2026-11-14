package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.application.dto.PaginaIncidentesResultado;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.Pagina;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListarIncidentesUseCaseTest {

    private RepositorioIncidentePort repositorio;
    private ListarIncidentesUseCase casoUso;

    @BeforeEach
    void configurar() {
        repositorio =
                mock(RepositorioIncidentePort.class);

        casoUso = new ListarIncidentesUseCase(
                repositorio
        );
    }

    @Test
    void debeListarIncidentesPaginados() {
        Incidente incidente = crearIncidente();

        Pagina<Incidente> pagina =
                new Pagina<>(
                        List.of(incidente),
                        0,
                        20,
                        1,
                        1
                );

        when(
                repositorio.buscarPaginado(
                        null,
                        null,
                        0,
                        20
                )
        ).thenReturn(pagina);

        PaginaIncidentesResultado resultado =
                casoUso.ejecutar(
                        null,
                        null,
                        0,
                        20
                );

        assertEquals(1, resultado.contenido().size());
        assertEquals(1, resultado.totalElementos());
        assertEquals(1, resultado.totalPaginas());
        assertTrue(resultado.primeraPagina());
        assertTrue(resultado.ultimaPagina());

        verify(repositorio).buscarPaginado(
                null,
                null,
                0,
                20
        );
    }

    @Test
    void debeAplicarFiltros() {
        Pagina<Incidente> pagina =
                new Pagina<>(
                        List.of(),
                        0,
                        10,
                        0,
                        0
                );

        when(
                repositorio.buscarPaginado(
                        EstadoIncidente.CLASIFICADO,
                        TipoIncidente.INUNDACION,
                        0,
                        10
                )
        ).thenReturn(pagina);

        casoUso.ejecutar(
                EstadoIncidente.CLASIFICADO,
                TipoIncidente.INUNDACION,
                0,
                10
        );

        verify(repositorio).buscarPaginado(
                EstadoIncidente.CLASIFICADO,
                TipoIncidente.INUNDACION,
                0,
                10
        );
    }

    @Test
    void debeRechazarPaginaNegativa() {
        assertThrows(
                IllegalArgumentException.class,
                () -> casoUso.ejecutar(
                        null,
                        null,
                        -1,
                        20
                )
        );
    }

    @Test
    void debeRechazarTamanioMayorACien() {
        assertThrows(
                IllegalArgumentException.class,
                () -> casoUso.ejecutar(
                        null,
                        null,
                        0,
                        101
                )
        );
    }

    private Incidente crearIncidente() {
        return Incidente.reportar(
                TipoIncidente.INUNDACION,
                "Inundacion con varias personas afectadas",
                new Ubicacion(
                        4.6579,
                        -74.0596,
                        "Carrera 7 con Calle 72, Bogota"
                ),
                new InformacionContacto(
                        "Operador de prueba",
                        "operador@alertacore.com",
                        "3001234567"
                ),
                15,
                true
        );
    }
}