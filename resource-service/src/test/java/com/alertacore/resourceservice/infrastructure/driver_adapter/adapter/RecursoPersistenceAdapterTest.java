package com.alertacore.resourceservice.infrastructure.driver_adapter.adapter;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.infrastructure.driver_adapter.entity.RecursoEntity;
import com.alertacore.resourceservice.infrastructure.driver_adapter.mapper.RecursoPersistenceMapper;
import com.alertacore.resourceservice.infrastructure.driver_adapter.repository.RecursoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecursoPersistenceAdapterTest {

    @Mock
    private RecursoJpaRepository repository;

    private RecursoPersistenceMapper mapper;
    private RecursoPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = new RecursoPersistenceMapper();

        adapter = new RecursoPersistenceAdapter(
                repository,
                mapper
        );
    }

    @Test
    void deberiaGuardarUnRecursoNuevo() {
        Recurso recurso = crearRecurso(
                "AMB-001",
                4.6579,
                -74.0596
        );

        RecursoEntity entityGuardada =
                mapper.toEntity(recurso);

        when(repository.findById(recurso.getId()))
                .thenReturn(Optional.empty());

        when(repository.save(any(RecursoEntity.class)))
                .thenReturn(entityGuardada);

        Recurso resultado =
                adapter.guardar(recurso);

        ArgumentCaptor<RecursoEntity> captor =
                ArgumentCaptor.forClass(
                        RecursoEntity.class
                );

        verify(repository)
                .findById(recurso.getId());

        verify(repository)
                .save(captor.capture());

        RecursoEntity entityEnviada =
                captor.getValue();

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(
                        recurso.getId(),
                        resultado.getId()
                ),
                () -> assertEquals(
                        recurso.getCodigo(),
                        resultado.getCodigo()
                ),
                () -> assertEquals(
                        recurso.getNombre(),
                        resultado.getNombre()
                ),
                () -> assertEquals(
                        recurso.getTipo(),
                        resultado.getTipo()
                ),
                () -> assertEquals(
                        recurso.getEstado(),
                        resultado.getEstado()
                ),
                () -> assertNotNull(entityEnviada)
        );
    }

    @Test
    void deberiaActualizarUnaEntidadExistenteAlGuardar() {
        Recurso recurso = crearRecurso(
                "AMB-002",
                4.6600,
                -74.0620
        );

        RecursoEntity entityExistente =
                mapper.toEntity(recurso);

        recurso.actualizarUbicacion(
                UbicacionRecurso.crear(
                        4.6700,
                        -74.0700
                )
        );

        when(repository.findById(recurso.getId()))
                .thenReturn(
                        Optional.of(entityExistente)
                );

        when(repository.save(any(RecursoEntity.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        Recurso resultado =
                adapter.guardar(recurso);

        ArgumentCaptor<RecursoEntity> captor =
                ArgumentCaptor.forClass(
                        RecursoEntity.class
                );

        verify(repository)
                .findById(recurso.getId());

        verify(repository)
                .save(captor.capture());

        RecursoEntity entityActualizada =
                captor.getValue();

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertSame(
                        entityExistente,
                        entityActualizada
                ),
                () -> assertEquals(
                        recurso.getId(),
                        resultado.getId()
                ),
                () -> assertEquals(
                        recurso.getCodigo(),
                        resultado.getCodigo()
                ),
                () -> assertEquals(
                        recurso.getUbicacion().getLatitud(),
                        resultado.getUbicacion().getLatitud()
                ),
                () -> assertEquals(
                        recurso.getUbicacion().getLongitud(),
                        resultado.getUbicacion().getLongitud()
                )
        );
    }

    @Test
    void deberiaBuscarPorIdCuandoExiste() {
        Recurso recurso = crearRecurso(
                "AMB-003",
                4.6579,
                -74.0596
        );

        RecursoEntity entity =
                mapper.toEntity(recurso);

        when(repository.findById(recurso.getId()))
                .thenReturn(
                        Optional.of(entity)
                );

        Optional<Recurso> resultado =
                adapter.buscarPorId(
                        recurso.getId()
                );

        assertAll(
                () -> assertTrue(resultado.isPresent()),
                () -> assertEquals(
                        recurso.getId(),
                        resultado.orElseThrow().getId()
                ),
                () -> assertEquals(
                        recurso.getCodigo(),
                        resultado.orElseThrow().getCodigo()
                )
        );

        verify(repository)
                .findById(recurso.getId());
    }

    @Test
    void deberiaRetornarVacioAlBuscarPorIdInexistente() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Recurso> resultado =
                adapter.buscarPorId(id);

        assertTrue(resultado.isEmpty());

        verify(repository)
                .findById(id);
    }

    @Test
    void deberiaBuscarPorCodigoCuandoExiste() {
        Recurso recurso = crearRecurso(
                "AMB-004",
                4.6579,
                -74.0596
        );

        RecursoEntity entity =
                mapper.toEntity(recurso);

        when(repository.findByCodigo(
                recurso.getCodigo()
        )).thenReturn(
                Optional.of(entity)
        );

        Optional<Recurso> resultado =
                adapter.buscarPorCodigo(
                        recurso.getCodigo()
                );

        assertAll(
                () -> assertTrue(resultado.isPresent()),
                () -> assertEquals(
                        recurso.getCodigo(),
                        resultado.orElseThrow().getCodigo()
                )
        );

        verify(repository)
                .findByCodigo(recurso.getCodigo());
    }

    @Test
    void deberiaRetornarVacioAlBuscarPorCodigoInexistente() {
        String codigo = "NO-EXISTE";

        when(repository.findByCodigo(codigo))
                .thenReturn(Optional.empty());

        Optional<Recurso> resultado =
                adapter.buscarPorCodigo(codigo);

        assertTrue(resultado.isEmpty());

        verify(repository)
                .findByCodigo(codigo);
    }

    @Test
    void deberiaBuscarRecursosDisponiblesPorTipo() {
        Recurso primerRecurso = crearRecurso(
                "AMB-005",
                4.6579,
                -74.0596
        );

        Recurso segundoRecurso = crearRecurso(
                "AMB-006",
                4.6600,
                -74.0620
        );

        RecursoEntity primeraEntity =
                mapper.toEntity(primerRecurso);

        RecursoEntity segundaEntity =
                mapper.toEntity(segundoRecurso);

        when(
                repository.findByTipoAndEstado(
                        TipoRecurso.AMBULANCIA.name(),
                        EstadoRecurso.DISPONIBLE.name()
                )
        ).thenReturn(
                List.of(
                        primeraEntity,
                        segundaEntity
                )
        );

        List<Recurso> resultado =
                adapter.buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                );

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(2, resultado.size()),
                () -> assertEquals(
                        "AMB-005",
                        resultado.get(0).getCodigo()
                ),
                () -> assertEquals(
                        "AMB-006",
                        resultado.get(1).getCodigo()
                ),
                () -> assertTrue(
                        resultado.stream()
                                .allMatch(recurso ->
                                        recurso.getEstado()
                                                == EstadoRecurso.DISPONIBLE
                                )
                ),
                () -> assertTrue(
                        resultado.stream()
                                .allMatch(recurso ->
                                        recurso.getTipo()
                                                == TipoRecurso.AMBULANCIA
                                )
                )
        );

        verify(repository)
                .findByTipoAndEstado(
                        TipoRecurso.AMBULANCIA.name(),
                        EstadoRecurso.DISPONIBLE.name()
                );
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayDisponibles() {
        when(
                repository.findByTipoAndEstado(
                        TipoRecurso.AMBULANCIA.name(),
                        EstadoRecurso.DISPONIBLE.name()
                )
        ).thenReturn(List.of());

        List<Recurso> resultado =
                adapter.buscarDisponiblesPorTipo(
                        TipoRecurso.AMBULANCIA
                );

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertTrue(resultado.isEmpty())
        );

        verify(repository)
                .findByTipoAndEstado(
                        TipoRecurso.AMBULANCIA.name(),
                        EstadoRecurso.DISPONIBLE.name()
                );
    }

    @Test
    void deberiaRetornarTrueCuandoExisteElCodigo() {
        String codigo = "AMB-007";

        when(repository.existsByCodigo(codigo))
                .thenReturn(true);

        boolean resultado =
                adapter.existePorCodigo(codigo);

        assertTrue(resultado);

        verify(repository)
                .existsByCodigo(codigo);
    }

    @Test
    void deberiaRetornarFalseCuandoNoExisteElCodigo() {
        String codigo = "AMB-INEXISTENTE";

        when(repository.existsByCodigo(codigo))
                .thenReturn(false);

        boolean resultado =
                adapter.existePorCodigo(codigo);

        assertFalse(resultado);

        verify(repository)
                .existsByCodigo(codigo);
    }

    @Test
    void deberiaBuscarTodosLosRecursos() {
        Recurso primerRecurso = crearRecurso(
                "AMB-008",
                4.6579,
                -74.0596
        );

        Recurso segundoRecurso = crearRecurso(
                "AMB-009",
                4.6600,
                -74.0620
        );

        when(repository.findAll())
                .thenReturn(
                        List.of(
                                mapper.toEntity(primerRecurso),
                                mapper.toEntity(segundoRecurso)
                        )
                );

        List<Recurso> resultado =
                adapter.buscarTodos();

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(2, resultado.size()),
                () -> assertEquals(
                        "AMB-008",
                        resultado.get(0).getCodigo()
                ),
                () -> assertEquals(
                        "AMB-009",
                        resultado.get(1).getCodigo()
                )
        );

        verify(repository).findAll();
    }

    @Test
    void deberiaRetornarListaVaciaCuandoNoHayRecursos() {
        when(repository.findAll())
                .thenReturn(List.of());

        List<Recurso> resultado =
                adapter.buscarTodos();

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertTrue(resultado.isEmpty())
        );

        verify(repository).findAll();
    }

    @Test
    void noDeberiaGuardarCuandoSoloSeConsulta() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Recurso> resultado =
                adapter.buscarPorId(id);

        assertTrue(resultado.isEmpty());

        verify(repository).findById(id);
        verify(repository, never())
                .save(any(RecursoEntity.class));

        verifyNoMoreInteractions(repository);
    }

    private Recurso crearRecurso(
            String codigo,
            double latitud,
            double longitud
    ) {
        return Recurso.crear(
                codigo,
                "Recurso " + codigo,
                TipoRecurso.AMBULANCIA,
                UbicacionRecurso.crear(
                        latitud,
                        longitud
                )
        );
    }
}