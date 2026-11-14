package com.alertacore.incidentservice.infrastructure.persistence.mapper;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.PuntajeRiesgo;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.infrastructure.persistence.entity.IncidenteEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IncidentePersistenceMapperTest {

    private IncidentePersistenceMapper mapper;

    @BeforeEach
    void configurar() {
        mapper = new IncidentePersistenceMapper();
    }

    @Test
    void debeConvertirDominioAEntidad() {
        Incidente incidente = crearIncidenteClasificado();

        IncidenteEntity entidad =
                mapper.aEntidad(incidente);

        assertEquals(
                incidente.getId(),
                entidad.getId()
        );

        assertEquals(
                TipoIncidente.INUNDACION,
                entidad.getTipo()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                entidad.getEstado()
        );

        assertEquals(
                95,
                entidad.getPuntajeRiesgo()
        );

        assertEquals(
                "operador@alertacore.com",
                entidad.getCorreoReportante()
        );
    }

    @Test
    void debeConvertirEntidadADominio() {
        Incidente incidenteOriginal =
                crearIncidenteClasificado();

        IncidenteEntity entidad =
                mapper.aEntidad(incidenteOriginal);

        Incidente incidenteReconstruido =
                mapper.aDominio(entidad);

        assertNotNull(incidenteReconstruido);

        assertEquals(
                incidenteOriginal.getId(),
                incidenteReconstruido.getId()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                incidenteReconstruido.getEstado()
        );

        assertEquals(
                NivelRiesgo.CRITICO,
                incidenteReconstruido.obtenerNivelRiesgo()
        );

        assertEquals(
                "Carrera 7 con Calle 72, Bogota",
                incidenteReconstruido
                        .getUbicacion()
                        .getDireccion()
        );
    }

    private Incidente crearIncidenteClasificado() {
        Ubicacion ubicacion = new Ubicacion(
                4.6579,
                -74.0596,
                "Carrera 7 con Calle 72, Bogota"
        );

        InformacionContacto contacto =
                new InformacionContacto(
                        "Operador de prueba",
                        "operador@alertacore.com",
                        "3001234567"
                );

        Incidente incidente = Incidente.reportar(
                TipoIncidente.INUNDACION,
                "Inundacion con varias personas afectadas",
                ubicacion,
                contacto,
                15,
                true
        );

        incidente.validar();
        incidente.clasificar(
                new PuntajeRiesgo(95)
        );

        return incidente;
    }
}