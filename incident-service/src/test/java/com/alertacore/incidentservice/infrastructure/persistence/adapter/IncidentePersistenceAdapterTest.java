package com.alertacore.incidentservice.infrastructure.persistence.adapter;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.Pagina;
import com.alertacore.incidentservice.infrastructure.persistence.entity.IncidenteEntity;
import com.alertacore.incidentservice.infrastructure.persistence.mapper.IncidentePersistenceMapper;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaIncidenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IncidentePersistenceAdapterTest {

    private JpaIncidenteRepository repositorioJpa;
    private IncidentePersistenceMapper mapper;
    private IncidentePersistenceAdapter adapter;

    @BeforeEach
    void configurar() {
        repositorioJpa = mock(
                JpaIncidenteRepository.class
        );

        mapper = mock(
                IncidentePersistenceMapper.class
        );

        adapter = new IncidentePersistenceAdapter(
                repositorioJpa,
                mapper
        );
    }

    @Test
    void debeGuardarIncidenteNuevo() {
        Incidente incidente = mock(Incidente.class);
        UUID incidenteId = UUID.randomUUID();

        IncidenteEntity entidadNueva =
                mock(IncidenteEntity.class);

        IncidenteEntity entidadGuardada =
                mock(IncidenteEntity.class);

        Incidente incidenteGuardado =
                mock(Incidente.class);

        when(incidente.getId())
                .thenReturn(incidenteId);

        when(repositorioJpa.findById(incidenteId))
                .thenReturn(Optional.empty());

        when(mapper.aEntidad(incidente))
                .thenReturn(entidadNueva);

        when(repositorioJpa.save(entidadNueva))
                .thenReturn(entidadGuardada);

        when(mapper.aDominio(entidadGuardada))
                .thenReturn(incidenteGuardado);

        Incidente resultado =
                adapter.guardar(incidente);

        assertSame(
                incidenteGuardado,
                resultado
        );

        verify(repositorioJpa)
                .findById(incidenteId);

        verify(mapper)
                .aEntidad(incidente);

        verify(
                mapper,
                never()
        ).actualizarEntidad(
                any(IncidenteEntity.class),
                any(Incidente.class)
        );

        verify(repositorioJpa)
                .save(entidadNueva);

        verify(mapper)
                .aDominio(entidadGuardada);
    }

    @Test
    void debeActualizarIncidenteExistente() {
        Incidente incidente = mock(Incidente.class);
        UUID incidenteId = UUID.randomUUID();

        IncidenteEntity entidadExistente =
                mock(IncidenteEntity.class);

        IncidenteEntity entidadGuardada =
                mock(IncidenteEntity.class);

        Incidente incidenteGuardado =
                mock(Incidente.class);

        when(incidente.getId())
                .thenReturn(incidenteId);

        when(repositorioJpa.findById(incidenteId))
                .thenReturn(
                        Optional.of(entidadExistente)
                );

        when(repositorioJpa.save(entidadExistente))
                .thenReturn(entidadGuardada);

        when(mapper.aDominio(entidadGuardada))
                .thenReturn(incidenteGuardado);

        Incidente resultado =
                adapter.guardar(incidente);

        assertSame(
                incidenteGuardado,
                resultado
        );

        verify(repositorioJpa)
                .findById(incidenteId);

        verify(mapper)
                .actualizarEntidad(
                        entidadExistente,
                        incidente
                );

        verify(
                mapper,
                never()
        ).aEntidad(incidente);

        verify(repositorioJpa)
                .save(entidadExistente);

        verify(mapper)
                .aDominio(entidadGuardada);
    }

    @Test
    void debeBuscarIncidenteExistentePorId() {
        UUID incidenteId = UUID.randomUUID();

        IncidenteEntity entidad =
                mock(IncidenteEntity.class);

        Incidente incidente =
                mock(Incidente.class);

        when(repositorioJpa.findById(incidenteId))
                .thenReturn(Optional.of(entidad));

        when(mapper.aDominio(entidad))
                .thenReturn(incidente);

        Optional<Incidente> resultado =
                adapter.buscarPorId(incidenteId);

        assertTrue(resultado.isPresent());

        assertSame(
                incidente,
                resultado.orElseThrow()
        );

        verify(repositorioJpa)
                .findById(incidenteId);

        verify(mapper)
                .aDominio(entidad);
    }

    @Test
    void debeRetornarVacioCuandoIncidenteNoExiste() {
        UUID incidenteId = UUID.randomUUID();

        when(repositorioJpa.findById(incidenteId))
                .thenReturn(Optional.empty());

        Optional<Incidente> resultado =
                adapter.buscarPorId(incidenteId);

        assertTrue(resultado.isEmpty());

        verify(repositorioJpa)
                .findById(incidenteId);

        verifyNoInteractions(mapper);
    }

    @Test
    void debeRetornarVerdaderoCuandoIncidenteExiste() {
        UUID incidenteId = UUID.randomUUID();

        when(repositorioJpa.existsById(incidenteId))
                .thenReturn(true);

        boolean resultado =
                adapter.existePorId(incidenteId);

        assertTrue(resultado);

        verify(repositorioJpa)
                .existsById(incidenteId);
    }

    @Test
    void debeRetornarFalsoCuandoIncidenteNoExiste() {
        UUID incidenteId = UUID.randomUUID();

        when(repositorioJpa.existsById(incidenteId))
                .thenReturn(false);

        boolean resultado =
                adapter.existePorId(incidenteId);

        assertFalse(resultado);

        verify(repositorioJpa)
                .existsById(incidenteId);
    }

    @Test
    void debeListarIncidentesSinFiltros() {
        IncidenteEntity entidad =
                mock(IncidenteEntity.class);

        Incidente incidente =
                mock(Incidente.class);

        Page<IncidenteEntity> paginaEntidades =
                new PageImpl<>(
                        List.of(entidad)
                );

        when(
                repositorioJpa.findAll(
                        any(Pageable.class)
                )
        ).thenReturn(paginaEntidades);

        when(mapper.aDominio(entidad))
                .thenReturn(incidente);

        Pagina<Incidente> resultado =
                adapter.buscarPaginado(
                        null,
                        null,
                        0,
                        20
                );

        assertEquals(
                1,
                resultado.contenido().size()
        );

        assertSame(
                incidente,
                resultado.contenido().get(0)
        );

        verificarDatosPaginacion(
                resultado,
                0,
                1,
                1L,
                1
        );

        verify(repositorioJpa)
                .findAll(any(Pageable.class));

        verify(
                repositorioJpa,
                never()
        ).findByEstado(
                any(EstadoIncidente.class),
                any(Pageable.class)
        );

        verify(
                repositorioJpa,
                never()
        ).findByTipo(
                any(TipoIncidente.class),
                any(Pageable.class)
        );
    }

    @Test
    void debeListarIncidentesFiltradosPorEstado() {
        IncidenteEntity entidad =
                mock(IncidenteEntity.class);

        Incidente incidente =
                mock(Incidente.class);

        Page<IncidenteEntity> paginaEntidades =
                new PageImpl<>(
                        List.of(entidad)
                );

        when(
                repositorioJpa.findByEstado(
                        any(EstadoIncidente.class),
                        any(Pageable.class)
                )
        ).thenReturn(paginaEntidades);

        when(mapper.aDominio(entidad))
                .thenReturn(incidente);

        Pagina<Incidente> resultado =
                adapter.buscarPaginado(
                        EstadoIncidente.CLASIFICADO,
                        null,
                        0,
                        10
                );

        assertEquals(
                1,
                resultado.contenido().size()
        );

        assertSame(
                incidente,
                resultado.contenido().get(0)
        );

        verify(repositorioJpa)
                .findByEstado(
                        any(EstadoIncidente.class),
                        any(Pageable.class)
                );

        verify(
                repositorioJpa,
                never()
        ).findByTipo(
                any(TipoIncidente.class),
                any(Pageable.class)
        );

        verify(
                repositorioJpa,
                never()
        ).findByEstadoAndTipo(
                any(EstadoIncidente.class),
                any(TipoIncidente.class),
                any(Pageable.class)
        );
    }

    @Test
    void debeListarIncidentesFiltradosPorTipo() {
        IncidenteEntity entidad =
                mock(IncidenteEntity.class);

        Incidente incidente =
                mock(Incidente.class);

        Page<IncidenteEntity> paginaEntidades =
                new PageImpl<>(
                        List.of(entidad)
                );

        when(
                repositorioJpa.findByTipo(
                        any(TipoIncidente.class),
                        any(Pageable.class)
                )
        ).thenReturn(paginaEntidades);

        when(mapper.aDominio(entidad))
                .thenReturn(incidente);

        Pagina<Incidente> resultado =
                adapter.buscarPaginado(
                        null,
                        TipoIncidente.INUNDACION,
                        0,
                        10
                );

        assertEquals(
                1,
                resultado.contenido().size()
        );

        assertSame(
                incidente,
                resultado.contenido().get(0)
        );

        verify(repositorioJpa)
                .findByTipo(
                        any(TipoIncidente.class),
                        any(Pageable.class)
                );

        verify(
                repositorioJpa,
                never()
        ).findByEstado(
                any(EstadoIncidente.class),
                any(Pageable.class)
        );

        verify(
                repositorioJpa,
                never()
        ).findByEstadoAndTipo(
                any(EstadoIncidente.class),
                any(TipoIncidente.class),
                any(Pageable.class)
        );
    }

    @Test
    void debeListarIncidentesFiltradosPorEstadoYTipo() {
        IncidenteEntity entidad =
                mock(IncidenteEntity.class);

        Incidente incidente =
                mock(Incidente.class);

        Page<IncidenteEntity> paginaEntidades =
                new PageImpl<>(
                        List.of(entidad)
                );

        when(
                repositorioJpa.findByEstadoAndTipo(
                        any(EstadoIncidente.class),
                        any(TipoIncidente.class),
                        any(Pageable.class)
                )
        ).thenReturn(paginaEntidades);

        when(mapper.aDominio(entidad))
                .thenReturn(incidente);

        Pagina<Incidente> resultado =
                adapter.buscarPaginado(
                        EstadoIncidente.CLASIFICADO,
                        TipoIncidente.INUNDACION,
                        0,
                        10
                );

        assertEquals(
                1,
                resultado.contenido().size()
        );

        assertSame(
                incidente,
                resultado.contenido().get(0)
        );

        verify(repositorioJpa)
                .findByEstadoAndTipo(
                        any(EstadoIncidente.class),
                        any(TipoIncidente.class),
                        any(Pageable.class)
                );

        verify(
                repositorioJpa,
                never()
        ).findByEstado(
                any(EstadoIncidente.class),
                any(Pageable.class)
        );

        verify(
                repositorioJpa,
                never()
        ).findByTipo(
                any(TipoIncidente.class),
                any(Pageable.class)
        );
    }

    @Test
    void debeConstruirPaginacionConOrdenDescendente() {
        Page<IncidenteEntity> paginaEntidades =
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(2, 15),
                        0
                );

        when(
                repositorioJpa.findAll(
                        any(Pageable.class)
                )
        ).thenReturn(paginaEntidades);

        Pagina<Incidente> resultado =
                adapter.buscarPaginado(
                        null,
                        null,
                        2,
                        15
                );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        verify(repositorioJpa)
                .findAll(captor.capture());

        Pageable pageable = captor.getValue();

        assertEquals(
                2,
                pageable.getPageNumber()
        );

        assertEquals(
                15,
                pageable.getPageSize()
        );

        Sort.Order ordenFecha =
                pageable.getSort()
                        .getOrderFor("fechaReporte");

        assertNotNull(ordenFecha);

        assertEquals(
                Sort.Direction.DESC,
                ordenFecha.getDirection()
        );

        assertEquals(
                2,
                resultado.numeroPagina()
        );

        assertEquals(
                15,
                resultado.tamanioPagina()
        );

        assertTrue(
                resultado.contenido().isEmpty()
        );
    }

    @Test
    void debeRetornarPaginaVacia() {
        Page<IncidenteEntity> paginaEntidades =
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 20),
                        0
                );

        when(
                repositorioJpa.findAll(
                        any(Pageable.class)
                )
        ).thenReturn(paginaEntidades);

        Pagina<Incidente> resultado =
                adapter.buscarPaginado(
                        null,
                        null,
                        0,
                        20
                );

        assertTrue(
                resultado.contenido().isEmpty()
        );

        assertEquals(
                0,
                resultado.numeroPagina()
        );

        assertEquals(
                20,
                resultado.tamanioPagina()
        );

        assertEquals(
                0L,
                resultado.totalElementos()
        );

        assertEquals(
                0,
                resultado.totalPaginas()
        );

        assertTrue(
                resultado.esPrimeraPagina()
        );

        assertTrue(
                resultado.esUltimaPagina()
        );

        assertTrue(
                resultado.estaVacia()
        );

        verify(repositorioJpa)
                .findAll(any(Pageable.class));

        verifyNoInteractions(mapper);
    }

    @Test
    void debeRechazarIdentificadorNuloAlBuscar() {
        assertThrows(
                NullPointerException.class,
                () -> adapter.buscarPorId(null)
        );

        verifyNoInteractions(
                repositorioJpa,
                mapper
        );
    }

    @Test
    void debeRechazarIdentificadorNuloAlComprobarExistencia() {
        assertThrows(
                NullPointerException.class,
                () -> adapter.existePorId(null)
        );

        verifyNoInteractions(
                repositorioJpa,
                mapper
        );
    }

    @Test
    void debeRechazarRepositorioJpaNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new IncidentePersistenceAdapter(
                        null,
                        mapper
                )
        );
    }

    @Test
    void debeRechazarMapperNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new IncidentePersistenceAdapter(
                        repositorioJpa,
                        null
                )
        );
    }

    private void verificarDatosPaginacion(
            Pagina<Incidente> pagina,
            int numeroPagina,
            int tamanioPagina,
            long totalElementos,
            int totalPaginas
    ) {
        assertEquals(
                numeroPagina,
                pagina.numeroPagina()
        );

        assertEquals(
                tamanioPagina,
                pagina.tamanioPagina()
        );

        assertEquals(
                totalElementos,
                pagina.totalElementos()
        );

        assertEquals(
                totalPaginas,
                pagina.totalPaginas()
        );
    }
}