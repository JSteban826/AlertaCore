package com.alertacore.resourceservice.infrastructure.driver_adapter.mapper;

import com.alertacore.resourceservice.domain.model.DespachoRecurso;
import com.alertacore.resourceservice.infrastructure.driver_adapter.entity.DespachoRecursoEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DespachoPersistenceMapperTest {

    private DespachoPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DespachoPersistenceMapper();
    }

    @Test
    void deberiaConvertirAEntity() {

        DespachoRecurso despacho =
                DespachoRecurso.crear(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "OPERADOR-001"
                );

        DespachoRecursoEntity entity =
                mapper.toEntity(despacho);

        assertNotNull(entity);

        assertEquals(
                despacho.getId(),
                entity.getId()
        );

        assertEquals(
                despacho.getRecursoId(),
                entity.getRecursoId()
        );

        assertEquals(
                despacho.getIncidenteId(),
                entity.getIncidenteId()
        );

        assertEquals(
                despacho.getResponsable(),
                entity.getResponsable()
        );

        assertEquals(
                despacho.getFechaDespacho(),
                entity.getFechaDespacho()
        );
    }

    @Test
    void deberiaConvertirADomain() {

        DespachoRecursoEntity entity =
                new DespachoRecursoEntity();

        entity.setId(
                UUID.randomUUID()
        );

        entity.setRecursoId(
                UUID.randomUUID()
        );

        entity.setIncidenteId(
                UUID.randomUUID()
        );

        entity.setResponsable(
                "OPERADOR-001"
        );

        entity.setFechaDespacho(
                LocalDateTime.now()
        );

        entity.setFechaLlegada(
                LocalDateTime.now()
                        .plusMinutes(5)
        );

        entity.setFechaFinalizacion(
                LocalDateTime.now()
                        .plusMinutes(30)
        );

        DespachoRecurso despacho =
                mapper.toDomain(entity);

        assertNotNull(despacho);

        assertEquals(
                entity.getId(),
                despacho.getId()
        );

        assertEquals(
                entity.getRecursoId(),
                despacho.getRecursoId()
        );

        assertEquals(
                entity.getIncidenteId(),
                despacho.getIncidenteId()
        );

        assertEquals(
                "OPERADOR-001",
                despacho.getResponsable()
        );
    }

    @Test
    void deberiaMantenerFechasDelDespacho() {

        LocalDateTime despachoTime =
                LocalDateTime.now();

        LocalDateTime llegadaTime =
                despachoTime.plusMinutes(10);

        LocalDateTime finTime =
                despachoTime.plusMinutes(45);

        DespachoRecursoEntity entity =
                new DespachoRecursoEntity();

        entity.setId(UUID.randomUUID());
        entity.setRecursoId(UUID.randomUUID());
        entity.setIncidenteId(UUID.randomUUID());

        entity.setResponsable(
                "OPERADOR-001"
        );

        entity.setFechaDespacho(
                despachoTime
        );

        entity.setFechaLlegada(
                llegadaTime
        );

        entity.setFechaFinalizacion(
                finTime
        );

        DespachoRecurso despacho =
                mapper.toDomain(entity);

        assertEquals(
                despachoTime,
                despacho.getFechaDespacho()
        );

        assertEquals(
                llegadaTime,
                despacho.getFechaLlegada()
        );

        assertEquals(
                finTime,
                despacho.getFechaFinalizacion()
        );
    }
}