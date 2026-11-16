package com.alertacore.incidentservice.integration;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.Pagina;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("testcontainers")
@Testcontainers
@Transactional
class IncidentePersistenceIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer(
                    "postgres:17-alpine"
            )
                    .withDatabaseName(
                            "alertacore_test"
                    )
                    .withUsername(
                            "alertacore_test"
                    )
                    .withPassword(
                            "alertacore_test"
                    );

    @Autowired
    private RepositorioIncidentePort repositorioIncidente;

    @Test
    void debeGuardarYConsultarIncidentePorId() {
        Incidente incidente =
                crearIncidente(
                        TipoIncidente.INUNDACION,
                        "Inundacion para prueba de persistencia",
                        12,
                        true
                );

        Incidente guardado =
                repositorioIncidente.guardar(
                        incidente
                );

        Optional<Incidente> encontrado =
                repositorioIncidente.buscarPorId(
                        guardado.getId()
                );

        assertTrue(
                encontrado.isPresent()
        );

        Incidente recuperado =
                encontrado.orElseThrow();

        assertEquals(
                guardado.getId(),
                recuperado.getId()
        );

        assertEquals(
                TipoIncidente.INUNDACION,
                recuperado.getTipo()
        );

        assertEquals(
                "Inundacion para prueba de persistencia",
                recuperado.getDescripcion()
        );

        assertEquals(
                EstadoIncidente.REPORTADO,
                recuperado.getEstado()
        );

        assertEquals(
                12,
                recuperado.getCantidadPersonasAfectadas()
        );

        assertTrue(
                recuperado.hayPersonasHeridas()
        );

        assertEquals(
                4.6579,
                recuperado.getUbicacion()
                        .getLatitud()
        );

        assertEquals(
                -74.0596,
                recuperado.getUbicacion()
                        .getLongitud()
        );

        assertEquals(
                "Operador de integración",
                recuperado.getInformacionContacto()
                        .getNombreReportante()
        );

        assertNotNull(
                recuperado.getFechaReporte()
        );

        assertNotNull(
                recuperado.getFechaUltimaActualizacion()
        );
    }

    @Test
    void debeActualizarUnIncidenteExistente() {
        Incidente incidente =
                crearIncidente(
                        TipoIncidente.INCENDIO,
                        "Incendio para comprobar actualizacion",
                        5,
                        false
                );

        Incidente guardado =
                repositorioIncidente.guardar(
                        incidente
                );

        guardado.validar();

        Incidente actualizado =
                repositorioIncidente.guardar(
                        guardado
                );

        assertEquals(
                guardado.getId(),
                actualizado.getId()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                actualizado.getEstado()
        );

        Optional<Incidente> encontrado =
                repositorioIncidente.buscarPorId(
                        guardado.getId()
                );

        assertTrue(
                encontrado.isPresent()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                encontrado.orElseThrow()
                        .getEstado()
        );
    }

    @Test
    void debeComprobarExistenciaDelIncidente() {
        Incidente incidente =
                crearIncidente(
                        TipoIncidente.FALLA_ELECTRICA,
                        "Falla electrica para comprobar existencia",
                        0,
                        false
                );

        Incidente guardado =
                repositorioIncidente.guardar(
                        incidente
                );

        assertTrue(
                repositorioIncidente.existePorId(
                        guardado.getId()
                )
        );

        assertFalse(
                repositorioIncidente.existePorId(
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void debeRetornarVacioCuandoElIncidenteNoExiste() {
        Optional<Incidente> resultado =
                repositorioIncidente.buscarPorId(
                        UUID.randomUUID()
                );

        assertTrue(
                resultado.isEmpty()
        );
    }

    @Test
    void debeListarIncidentesPaginados() {
        repositorioIncidente.guardar(
                crearIncidente(
                        TipoIncidente.INUNDACION,
                        "Primera inundacion para listado paginado",
                        10,
                        true
                )
        );

        repositorioIncidente.guardar(
                crearIncidente(
                        TipoIncidente.INCENDIO,
                        "Segundo incidente para listado paginado",
                        3,
                        false
                )
        );

        Pagina<Incidente> pagina =
                repositorioIncidente.buscarPaginado(
                        null,
                        null,
                        0,
                        20
                );

        assertEquals(
                0,
                pagina.numeroPagina()
        );

        assertEquals(
                20,
                pagina.tamanioPagina()
        );

        assertTrue(
                pagina.totalElementos() >= 2
        );

        assertFalse(
                pagina.contenido().isEmpty()
        );

        assertTrue(
                pagina.esPrimeraPagina()
        );
    }

    @Test
    void debeFiltrarIncidentesPorEstado() {
        Incidente reportado =
                crearIncidente(
                        TipoIncidente.INUNDACION,
                        "Incidente reportado para filtro por estado",
                        4,
                        false
                );

        repositorioIncidente.guardar(reportado);

        Incidente validado =
                crearIncidente(
                        TipoIncidente.INCENDIO,
                        "Incidente validado para filtro por estado",
                        8,
                        true
                );

        validado.validar();

        repositorioIncidente.guardar(validado);

        Pagina<Incidente> pagina =
                repositorioIncidente.buscarPaginado(
                        EstadoIncidente.VALIDADO,
                        null,
                        0,
                        20
                );

        assertFalse(
                pagina.contenido().isEmpty()
        );

        assertTrue(
                pagina.contenido()
                        .stream()
                        .allMatch(
                                incidente ->
                                        incidente.getEstado()
                                                == EstadoIncidente.VALIDADO
                        )
        );
    }

    @Test
    void debeFiltrarIncidentesPorTipo() {
        repositorioIncidente.guardar(
                crearIncidente(
                        TipoIncidente.INUNDACION,
                        "Inundacion para filtro por tipo",
                        15,
                        true
                )
        );

        repositorioIncidente.guardar(
                crearIncidente(
                        TipoIncidente.INCENDIO,
                        "Incendio para filtro por tipo",
                        2,
                        false
                )
        );

        Pagina<Incidente> pagina =
                repositorioIncidente.buscarPaginado(
                        null,
                        TipoIncidente.INUNDACION,
                        0,
                        20
                );

        assertFalse(
                pagina.contenido().isEmpty()
        );

        assertTrue(
                pagina.contenido()
                        .stream()
                        .allMatch(
                                incidente ->
                                        incidente.getTipo()
                                                == TipoIncidente.INUNDACION
                        )
        );
    }

    @Test
    void debeFiltrarPorEstadoYTipo() {
        Incidente inundacionValidada =
                crearIncidente(
                        TipoIncidente.INUNDACION,
                        "Inundacion validada para filtro combinado",
                        20,
                        true
                );

        inundacionValidada.validar();

        repositorioIncidente.guardar(
                inundacionValidada
        );

        repositorioIncidente.guardar(
                crearIncidente(
                        TipoIncidente.INUNDACION,
                        "Inundacion reportada para filtro combinado",
                        3,
                        false
                )
        );

        Incidente incendioValidado =
                crearIncidente(
                        TipoIncidente.INCENDIO,
                        "Incendio validado para filtro combinado",
                        7,
                        true
                );

        incendioValidado.validar();

        repositorioIncidente.guardar(
                incendioValidado
        );

        Pagina<Incidente> pagina =
                repositorioIncidente.buscarPaginado(
                        EstadoIncidente.VALIDADO,
                        TipoIncidente.INUNDACION,
                        0,
                        20
                );

        assertFalse(
                pagina.contenido().isEmpty()
        );

        assertTrue(
                pagina.contenido()
                        .stream()
                        .allMatch(
                                incidente ->
                                        incidente.getEstado()
                                                == EstadoIncidente.VALIDADO
                                                && incidente.getTipo()
                                                == TipoIncidente.INUNDACION
                        )
        );
    }

    private Incidente crearIncidente(
            TipoIncidente tipo,
            String descripcion,
            int personasAfectadas,
            boolean hayPersonasHeridas
    ) {
        Ubicacion ubicacion =
                new Ubicacion(
                        4.6579,
                        -74.0596,
                        "Carrera 7 con Calle 72, Bogota"
                );

        InformacionContacto contacto =
                new InformacionContacto(
                        "Operador de integración",
                        "integracion@alertacore.com",
                        "3001234567"
                );

        return Incidente.reportar(
                tipo,
                descripcion,
                ubicacion,
                contacto,
                personasAfectadas,
                hayPersonasHeridas
        );
    }
}