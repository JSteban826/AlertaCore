package com.alertacore.incidentservice.infrastructure.mapper;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.PuntajeRiesgo;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.infrastructure.mapper.IncidentePersistenceMapper;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.entity.IncidenteEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncidentePersistenceMapperTest {

    private static final double LATITUD = 4.6579;
    private static final double LONGITUD = -74.0596;

    private static final String DIRECCION =
            "Carrera 7 con Calle 72, Bogota";

    private static final String DESCRIPCION =
            "Inundacion con varias personas afectadas";

    private static final String NOMBRE_REPORTANTE =
            "Operador de prueba";

    private static final String CORREO_REPORTANTE =
            "operador@alertacore.com";

    private static final String TELEFONO_REPORTANTE =
            "3001234567";

    private IncidentePersistenceMapper mapper;

    @BeforeEach
    void configurar() {
        mapper = new IncidentePersistenceMapper();
    }

    @Test
    void debeConvertirIncidenteClasificadoDeDominioAEntidad() {
        Incidente incidente =
                crearIncidenteClasificado();

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
                DESCRIPCION,
                entidad.getDescripcion()
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
                15,
                entidad.getCantidadPersonasAfectadas()
        );

        assertTrue(
                entidad.isHayPersonasHeridas()
        );

        assertEquals(
                LATITUD,
                entidad.getLatitud()
        );

        assertEquals(
                LONGITUD,
                entidad.getLongitud()
        );

        assertEquals(
                DIRECCION,
                entidad.getDireccion()
        );

        assertEquals(
                NOMBRE_REPORTANTE,
                entidad.getNombreReportante()
        );

        assertEquals(
                CORREO_REPORTANTE,
                entidad.getCorreoReportante()
        );

        assertEquals(
                TELEFONO_REPORTANTE,
                entidad.getTelefonoReportante()
        );

        assertEquals(
                incidente.getFechaReporte(),
                entidad.getFechaReporte()
        );

        assertEquals(
                incidente.getFechaUltimaActualizacion(),
                entidad.getFechaUltimaActualizacion()
        );
    }

    @Test
    void debeConvertirIncidenteSinClasificarDeDominioAEntidad() {
        Incidente incidente =
                crearIncidenteReportado();

        IncidenteEntity entidad =
                mapper.aEntidad(incidente);

        assertEquals(
                incidente.getId(),
                entidad.getId()
        );

        assertEquals(
                EstadoIncidente.REPORTADO,
                entidad.getEstado()
        );

        assertNull(
                entidad.getPuntajeRiesgo()
        );

        assertEquals(
                TipoIncidente.INUNDACION,
                entidad.getTipo()
        );

        assertEquals(
                DESCRIPCION,
                entidad.getDescripcion()
        );

        assertEquals(
                15,
                entidad.getCantidadPersonasAfectadas()
        );

        assertTrue(
                entidad.isHayPersonasHeridas()
        );
    }

    @Test
    void debeConvertirEntidadClasificadaADominio() {
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
                TipoIncidente.INUNDACION,
                incidenteReconstruido.getTipo()
        );

        assertEquals(
                DESCRIPCION,
                incidenteReconstruido.getDescripcion()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                incidenteReconstruido.getEstado()
        );

        assertNotNull(
                incidenteReconstruido.getPuntajeRiesgo()
        );

        assertEquals(
                95,
                incidenteReconstruido
                        .getPuntajeRiesgo()
                        .getValor()
        );

        assertEquals(
                NivelRiesgo.CRITICO,
                incidenteReconstruido.obtenerNivelRiesgo()
        );

        assertEquals(
                15,
                incidenteReconstruido
                        .getCantidadPersonasAfectadas()
        );

        assertTrue(
                incidenteReconstruido
                        .hayPersonasHeridas()
        );

        assertEquals(
                LATITUD,
                incidenteReconstruido
                        .getUbicacion()
                        .getLatitud()
        );

        assertEquals(
                LONGITUD,
                incidenteReconstruido
                        .getUbicacion()
                        .getLongitud()
        );

        assertEquals(
                DIRECCION,
                incidenteReconstruido
                        .getUbicacion()
                        .getDireccion()
        );

        assertEquals(
                NOMBRE_REPORTANTE,
                incidenteReconstruido
                        .getInformacionContacto()
                        .getNombreReportante()
        );

        assertEquals(
                CORREO_REPORTANTE,
                incidenteReconstruido
                        .getInformacionContacto()
                        .getCorreo()
        );

        assertEquals(
                TELEFONO_REPORTANTE,
                incidenteReconstruido
                        .getInformacionContacto()
                        .getTelefono()
        );

        assertEquals(
                incidenteOriginal.getFechaReporte(),
                incidenteReconstruido.getFechaReporte()
        );

        assertEquals(
                incidenteOriginal.getFechaUltimaActualizacion(),
                incidenteReconstruido
                        .getFechaUltimaActualizacion()
        );
    }

    @Test
    void debeConvertirEntidadSinPuntajeADominio() {
        Incidente incidenteOriginal =
                crearIncidenteReportado();

        IncidenteEntity entidad =
                mapper.aEntidad(incidenteOriginal);

        Incidente incidenteReconstruido =
                mapper.aDominio(entidad);

        assertNotNull(incidenteReconstruido);

        assertEquals(
                EstadoIncidente.REPORTADO,
                incidenteReconstruido.getEstado()
        );

        assertNull(
                incidenteReconstruido.getPuntajeRiesgo()
        );

        assertFalse(
                incidenteReconstruido.estaClasificado()
        );

        assertEquals(
                incidenteOriginal.getId(),
                incidenteReconstruido.getId()
        );

        assertEquals(
                TipoIncidente.INUNDACION,
                incidenteReconstruido.getTipo()
        );

        assertEquals(
                DESCRIPCION,
                incidenteReconstruido.getDescripcion()
        );

        assertEquals(
                DIRECCION,
                incidenteReconstruido
                        .getUbicacion()
                        .getDireccion()
        );
    }

    @Test
    void debeActualizarEntidadConIncidenteClasificado() {
        Incidente incidente =
                crearIncidenteReportado();

        IncidenteEntity entidad =
                mapper.aEntidad(incidente);

        assertEquals(
                EstadoIncidente.REPORTADO,
                entidad.getEstado()
        );

        assertNull(
                entidad.getPuntajeRiesgo()
        );

        incidente.validar();

        incidente.clasificar(
                new PuntajeRiesgo(95)
        );

        mapper.actualizarEntidad(
                entidad,
                incidente
        );

        assertEquals(
                TipoIncidente.INUNDACION,
                entidad.getTipo()
        );

        assertEquals(
                DESCRIPCION,
                entidad.getDescripcion()
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
                15,
                entidad.getCantidadPersonasAfectadas()
        );

        assertTrue(
                entidad.isHayPersonasHeridas()
        );

        assertEquals(
                LATITUD,
                entidad.getLatitud()
        );

        assertEquals(
                LONGITUD,
                entidad.getLongitud()
        );

        assertEquals(
                DIRECCION,
                entidad.getDireccion()
        );

        assertEquals(
                NOMBRE_REPORTANTE,
                entidad.getNombreReportante()
        );

        assertEquals(
                CORREO_REPORTANTE,
                entidad.getCorreoReportante()
        );

        assertEquals(
                TELEFONO_REPORTANTE,
                entidad.getTelefonoReportante()
        );

        assertEquals(
                incidente.getFechaUltimaActualizacion(),
                entidad.getFechaUltimaActualizacion()
        );
    }

    @Test
    void debeActualizarEntidadConIncidenteSinPuntaje() {
        Incidente incidenteInicial =
                crearIncidenteReportado();

        IncidenteEntity entidad =
                mapper.aEntidad(incidenteInicial);

        Incidente otroIncidenteReportado =
                crearIncidenteReportado();

        mapper.actualizarEntidad(
                entidad,
                otroIncidenteReportado
        );

        assertEquals(
                EstadoIncidente.REPORTADO,
                entidad.getEstado()
        );

        assertNull(
                entidad.getPuntajeRiesgo()
        );

        assertEquals(
                TipoIncidente.INUNDACION,
                entidad.getTipo()
        );

        assertEquals(
                DESCRIPCION,
                entidad.getDescripcion()
        );
    }

    @Test
    void debeConservarIdentificadorYFechaReporteAlActualizar() {
        Incidente incidente =
                crearIncidenteReportado();

        IncidenteEntity entidad =
                mapper.aEntidad(incidente);

        var identificadorOriginal =
                entidad.getId();

        var fechaReporteOriginal =
                entidad.getFechaReporte();

        incidente.validar();

        incidente.clasificar(
                new PuntajeRiesgo(80)
        );

        mapper.actualizarEntidad(
                entidad,
                incidente
        );

        assertEquals(
                identificadorOriginal,
                entidad.getId()
        );

        assertEquals(
                fechaReporteOriginal,
                entidad.getFechaReporte()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                entidad.getEstado()
        );

        assertEquals(
                80,
                entidad.getPuntajeRiesgo()
        );
    }

    @Test
    void debeRechazarIncidenteNuloAlConvertirAEntidad() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> mapper.aEntidad(null)
                );

        assertEquals(
                "El incidente es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarEntidadNulaAlConvertirADominio() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> mapper.aDominio(null)
                );

        assertEquals(
                "La entidad de incidente es obligatoria",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarEntidadNulaAlActualizar() {
        Incidente incidente =
                crearIncidenteReportado();

        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> mapper.actualizarEntidad(
                                null,
                                incidente
                        )
                );

        assertEquals(
                "La entidad de incidente es obligatoria",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarIncidenteNuloAlActualizar() {
        IncidenteEntity entidad =
                mapper.aEntidad(
                        crearIncidenteReportado()
                );

        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> mapper.actualizarEntidad(
                                entidad,
                                null
                        )
                );

        assertEquals(
                "El incidente es obligatorio",
                excepcion.getMessage()
        );
    }

    private Incidente crearIncidenteReportado() {
        Ubicacion ubicacion = new Ubicacion(
                LATITUD,
                LONGITUD,
                DIRECCION
        );

        InformacionContacto contacto =
                new InformacionContacto(
                        NOMBRE_REPORTANTE,
                        CORREO_REPORTANTE,
                        TELEFONO_REPORTANTE
                );

        return Incidente.reportar(
                TipoIncidente.INUNDACION,
                DESCRIPCION,
                ubicacion,
                contacto,
                15,
                true
        );
    }

    private Incidente crearIncidenteClasificado() {
        Incidente incidente =
                crearIncidenteReportado();

        incidente.validar();

        incidente.clasificar(
                new PuntajeRiesgo(95)
        );

        return incidente;
    }
}