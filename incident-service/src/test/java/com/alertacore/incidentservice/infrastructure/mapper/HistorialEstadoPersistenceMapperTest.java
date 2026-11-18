package com.alertacore.incidentservice.infrastructure.mapper;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.infrastructure.mapper.HistorialEstadoPersistenceMapper;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.entity.HistorialEstadoIncidenteEntity;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.entity.IncidenteEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HistorialEstadoPersistenceMapperTest {

    private HistorialEstadoPersistenceMapper mapper;

    @BeforeEach
    void configurar() {
        mapper = new HistorialEstadoPersistenceMapper();
    }

    @Test
    void debeConvertirRegistroDeDominioAEntidad() {
        UUID registroId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();

        LocalDateTime fechaCambio =
                LocalDateTime.of(
                        2026,
                        11,
                        16,
                        13,
                        30
                );

        RegistroHistorialEstado registro =
                new RegistroHistorialEstado(
                        registroId,
                        incidenteId,
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        "Informacion verificada",
                        "OPERADOR_API",
                        fechaCambio
                );

        IncidenteEntity incidenteEntity =
                mock(IncidenteEntity.class);

        HistorialEstadoIncidenteEntity entidad =
                mapper.aEntidad(
                        registro,
                        incidenteEntity
                );

        assertEquals(
                registroId,
                entidad.getId()
        );

        assertSame(
                incidenteEntity,
                entidad.getIncidente()
        );

        assertEquals(
                EstadoIncidente.REPORTADO,
                entidad.getEstadoAnterior()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                entidad.getNuevoEstado()
        );

        assertEquals(
                "Informacion verificada",
                entidad.getMotivo()
        );

        assertEquals(
                "OPERADOR_API",
                entidad.getResponsable()
        );

        assertEquals(
                fechaCambio,
                entidad.getFechaCambio()
        );
    }

    @Test
    void debeConvertirEntidadADominio() {
        UUID registroId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();

        LocalDateTime fechaCambio =
                LocalDateTime.of(
                        2026,
                        11,
                        16,
                        13,
                        45
                );

        IncidenteEntity incidenteEntity =
                mock(IncidenteEntity.class);

        when(incidenteEntity.getId())
                .thenReturn(incidenteId);

        HistorialEstadoIncidenteEntity entidad =
                mock(
                        HistorialEstadoIncidenteEntity.class
                );

        when(entidad.getId())
                .thenReturn(registroId);

        when(entidad.getIncidente())
                .thenReturn(incidenteEntity);

        when(entidad.getEstadoAnterior())
                .thenReturn(
                        EstadoIncidente.VALIDADO
                );

        when(entidad.getNuevoEstado())
                .thenReturn(
                        EstadoIncidente.CLASIFICADO
                );

        when(entidad.getMotivo())
                .thenReturn(
                        "Clasificacion automatica de riesgo"
                );

        when(entidad.getResponsable())
                .thenReturn(
                        "SISTEMA_CLASIFICACION"
                );

        when(entidad.getFechaCambio())
                .thenReturn(fechaCambio);

        RegistroHistorialEstado resultado =
                mapper.aDominio(entidad);

        assertEquals(
                registroId,
                resultado.id()
        );

        assertEquals(
                incidenteId,
                resultado.incidenteId()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                resultado.estadoAnterior()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                resultado.nuevoEstado()
        );

        assertEquals(
                "Clasificacion automatica de riesgo",
                resultado.motivo()
        );

        assertEquals(
                "SISTEMA_CLASIFICACION",
                resultado.responsable()
        );

        assertEquals(
                fechaCambio,
                resultado.fechaCambio()
        );
    }

    @Test
    void debePermitirEstadoAnteriorNulo() {
        UUID registroId = UUID.randomUUID();
        UUID incidenteId = UUID.randomUUID();

        RegistroHistorialEstado registro =
                new RegistroHistorialEstado(
                        registroId,
                        incidenteId,
                        null,
                        EstadoIncidente.REPORTADO,
                        "Incidente registrado",
                        "SISTEMA",
                        LocalDateTime.now()
                );

        IncidenteEntity incidenteEntity =
                mock(IncidenteEntity.class);

        HistorialEstadoIncidenteEntity entidad =
                mapper.aEntidad(
                        registro,
                        incidenteEntity
                );

        assertEquals(
                null,
                entidad.getEstadoAnterior()
        );

        assertEquals(
                EstadoIncidente.REPORTADO,
                entidad.getNuevoEstado()
        );
    }

    @Test
    void debeRechazarRegistroNulo() {
        IncidenteEntity incidenteEntity =
                mock(IncidenteEntity.class);

        assertThrows(
                NullPointerException.class,
                () -> mapper.aEntidad(
                        null,
                        incidenteEntity
                )
        );
    }

    @Test
    void debeRechazarIncidenteEntityNulo() {
        RegistroHistorialEstado registro =
                crearRegistro();

        assertThrows(
                NullPointerException.class,
                () -> mapper.aEntidad(
                        registro,
                        null
                )
        );
    }

    @Test
    void debeRechazarEntidadNulaAlConvertirADominio() {
        assertThrows(
                NullPointerException.class,
                () -> mapper.aDominio(null)
        );
    }

    private RegistroHistorialEstado crearRegistro() {
        return new RegistroHistorialEstado(
                UUID.randomUUID(),
                UUID.randomUUID(),
                EstadoIncidente.REPORTADO,
                EstadoIncidente.VALIDADO,
                "Informacion verificada",
                "OPERADOR_API",
                LocalDateTime.now()
        );
    }
}