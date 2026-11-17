package com.alertacore.resourceservice.infrastructure.persistence.adapter;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;

import com.alertacore.resourceservice.infrastructure.persistence.entity.RecursoEntity;
import com.alertacore.resourceservice.infrastructure.persistence.mapper.RecursoPersistenceMapper;
import com.alertacore.resourceservice.infrastructure.persistence.repository.RecursoJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecursoPersistenceAdapterTest {

    @Mock
    private RecursoJpaRepository repository;

    private RecursoPersistenceMapper mapper;

    @InjectMocks
    private RecursoPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {

        mapper = new RecursoPersistenceMapper();

        adapter =
                new RecursoPersistenceAdapter(
                        repository,
                        mapper
                );
    }

    @Test
    void deberiaGuardarRecurso() {

        Recurso recurso =
                crearRecurso();

        RecursoEntity entity =
                mapper.toEntity(recurso);

        when(repository.findById(
                recurso.getId()))
                .thenReturn(Optional.empty());

        when(repository.save(any()))
                .thenReturn(entity);

        Recurso resultado =
                adapter.guardar(recurso);

        assertNotNull(resultado);

        assertEquals(
                recurso.getCodigo(),
                resultado.getCodigo()
        );

        verify(repository)
                .save(any());
    }

    @Test
    void deberiaBuscarPorId() {

        Recurso recurso =
                crearRecurso();

        RecursoEntity entity =
                mapper.toEntity(recurso);

        when(repository.findById(
                recurso.getId()))
                .thenReturn(
                        Optional.of(entity)
                );

        Optional<Recurso> resultado =
                adapter.buscarPorId(
                        recurso.getId()
                );

        assertTrue(
                resultado.isPresent()
        );

        assertEquals(
                recurso.getCodigo(),
                resultado.get().getCodigo()
        );
    }

    @Test
    void deberiaBuscarPorCodigo() {

        Recurso recurso =
                crearRecurso();

        RecursoEntity entity =
                mapper.toEntity(recurso);

        when(repository.findByCodigo(
                recurso.getCodigo()))
                .thenReturn(
                        Optional.of(entity)
                );

        Optional<Recurso> resultado =
                adapter.buscarPorCodigo(
                        recurso.getCodigo()
                );

        assertTrue(
                resultado.isPresent()
        );
    }

    @Test
    void deberiaValidarExistenciaCodigo() {

        when(repository.existsByCodigo(
                "AMB-001"))
                .thenReturn(true);

        assertTrue(
                adapter.existePorCodigo(
                        "AMB-001"
                )
        );
    }

    @Test
    void deberiaBuscarTodos() {

        Recurso recurso =
                crearRecurso();

        RecursoEntity entity =
                mapper.toEntity(recurso);

        when(repository.findAll())
                .thenReturn(
                        List.of(entity)
                );

        List<Recurso> recursos =
                adapter.buscarTodos();

        assertEquals(
                1,
                recursos.size()
        );
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
}