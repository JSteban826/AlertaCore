package com.alertacore.resourceservice.infrastructure.driver_adapter.adapter;

import com.alertacore.resourceservice.domain.model.DespachoRecurso;

import com.alertacore.resourceservice.infrastructure.driver_adapter.entity.DespachoRecursoEntity;
import com.alertacore.resourceservice.infrastructure.driver_adapter.mapper.DespachoPersistenceMapper;
import com.alertacore.resourceservice.infrastructure.driver_adapter.repository.DespachoRecursoJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DespachoPersistenceAdapterTest {

    @Mock
    private DespachoRecursoJpaRepository repository;

    private DespachoPersistenceMapper mapper;

    @InjectMocks
    private DespachoPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {

        mapper = new DespachoPersistenceMapper();

        adapter =
                new DespachoPersistenceAdapter(
                        repository,
                        mapper
                );
    }

    @Test
    void deberiaGuardarDespacho() {

        DespachoRecurso despacho =
                DespachoRecurso.crear(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "OPERADOR-001"
                );

        DespachoRecursoEntity entity =
                mapper.toEntity(despacho);

        when(repository.save(any()))
                .thenReturn(entity);

        DespachoRecurso resultado =
                adapter.guardar(despacho);

        assertNotNull(resultado);

        assertEquals(
                despacho.getId(),
                resultado.getId()
        );

        assertEquals(
                despacho.getRecursoId(),
                resultado.getRecursoId()
        );

        assertEquals(
                despacho.getIncidenteId(),
                resultado.getIncidenteId()
        );

        assertEquals(
                despacho.getResponsable(),
                resultado.getResponsable()
        );

        verify(repository)
                .save(any());
    }
}