package com.alertacore.resourceservice.infrastructure.driver_adapter.mapper;

import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.UbicacionRecurso;
import com.alertacore.resourceservice.infrastructure.driver_adapter.entity.RecursoEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecursoPersistenceMapperTest {

    private RecursoPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RecursoPersistenceMapper();
    }

    @Test
    void deberiaConvertirAEntity() {

        Recurso recurso =
                Recurso.crear(
                        "AMB-001",
                        "Ambulancia Principal",
                        TipoRecurso.AMBULANCIA,
                        UbicacionRecurso.crear(
                                4.6579,
                                -74.0596
                        )
                );

        RecursoEntity entity =
                mapper.toEntity(recurso);

        assertNotNull(entity);

        assertEquals(
                recurso.getId(),
                entity.getId()
        );

        assertEquals(
                recurso.getCodigo(),
                entity.getCodigo()
        );

        assertEquals(
                recurso.getNombre(),
                entity.getNombre()
        );

        assertEquals(
                recurso.getTipo().name(),
                entity.getTipo()
        );

        assertEquals(
                recurso.getEstado().name(),
                entity.getEstado()
        );
    }

    @Test
    void deberiaConvertirADomain() {

        RecursoEntity entity =
                new RecursoEntity();

        entity.setId(
                java.util.UUID.randomUUID()
        );

        entity.setCodigo("AMB-001");

        entity.setNombre(
                "Ambulancia Principal"
        );

        entity.setTipo(
                TipoRecurso.AMBULANCIA.name()
        );

        entity.setEstado(
                EstadoRecurso.DISPONIBLE.name()
        );

        entity.setLatitud(4.6579);

        entity.setLongitud(-74.0596);

        entity.setFechaUltimaActualizacion(
                java.time.LocalDateTime.now()
        );

        Recurso recurso =
                mapper.toDomain(entity);

        assertNotNull(recurso);

        assertEquals(
                "AMB-001",
                recurso.getCodigo()
        );

        assertEquals(
                TipoRecurso.AMBULANCIA,
                recurso.getTipo()
        );

        assertEquals(
                EstadoRecurso.DISPONIBLE,
                recurso.getEstado()
        );
    }

    @Test
    void deberiaActualizarEntidad() {

        Recurso recurso =
                Recurso.crear(
                        "AMB-002",
                        "Nueva Ambulancia",
                        TipoRecurso.AMBULANCIA,
                        UbicacionRecurso.crear(
                                4.60,
                                -74.00
                        )
                );

        RecursoEntity entity =
                new RecursoEntity();

        mapper.actualizarEntidad(
                recurso,
                entity
        );

        assertEquals(
                "AMB-002",
                entity.getCodigo()
        );

        assertEquals(
                "Nueva Ambulancia",
                entity.getNombre()
        );

        assertEquals(
                TipoRecurso.AMBULANCIA.name(),
                entity.getTipo()
        );

        assertEquals(
                EstadoRecurso.DISPONIBLE.name(),
                entity.getEstado()
        );
    }
}