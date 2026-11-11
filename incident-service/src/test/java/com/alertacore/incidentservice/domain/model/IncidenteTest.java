package com.alertacore.incidentservice.domain.model;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteInvalidoException;
import com.alertacore.incidentservice.domain.exception.TransicionEstadoInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IncidenteTest {

    private Ubicacion ubicacion;
    private InformacionContacto contacto;

    @BeforeEach
    void configurar() {
        ubicacion = new Ubicacion(
                4.6579,
                -74.0596,
                "Carrera 7 con Calle 72, Bogota"
        );

        contacto = new InformacionContacto(
                "Operador de prueba",
                "operador@alertacore.com",
                "3001234567"
        );
    }

    @Test
    void debeReportarIncidenteValido() {
        Incidente incidente = crearIncidente();

        assertNotNull(incidente.getId());
        assertEquals(
                TipoIncidente.INUNDACION,
                incidente.getTipo()
        );
        assertEquals(
                EstadoIncidente.REPORTADO,
                incidente.getEstado()
        );
        assertEquals(
                15,
                incidente.getCantidadPersonasAfectadas()
        );
        assertTrue(incidente.hayPersonasHeridas());
        assertFalse(incidente.estaClasificado());
        assertNull(incidente.getPuntajeRiesgo());
        assertNotNull(incidente.getFechaReporte());
    }

    @Test
    void debeValidarIncidenteReportado() {
        Incidente incidente = crearIncidente();

        incidente.validar();

        assertEquals(
                EstadoIncidente.VALIDADO,
                incidente.getEstado()
        );
    }

    @Test
    void debeClasificarIncidenteValidado() {
        Incidente incidente = crearIncidente();
        incidente.validar();

        incidente.clasificar(new PuntajeRiesgo(95));

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                incidente.getEstado()
        );
        assertTrue(incidente.estaClasificado());
        assertEquals(
                NivelRiesgo.CRITICO,
                incidente.obtenerNivelRiesgo()
        );
        assertEquals(
                10,
                incidente.obtenerTiempoMaximoRespuestaMinutos()
        );
    }

    @Test
    void debeRechazarClasificacionSinValidacionPrevia() {
        Incidente incidente = crearIncidente();

        assertThrows(
                TransicionEstadoInvalidaException.class,
                () -> incidente.clasificar(
                        new PuntajeRiesgo(50)
                )
        );
    }

    @Test
    void debeCompletarFlujoPrincipal() {
        Incidente incidente = crearIncidente();

        incidente.validar();
        incidente.clasificar(new PuntajeRiesgo(95));
        incidente.asignarRecurso();
        incidente.iniciarAtencion();
        incidente.resolver();
        incidente.cerrar();

        assertEquals(
                EstadoIncidente.CERRADO,
                incidente.getEstado()
        );
    }

    @Test
    void debeRechazarTransicionDeReportadoACerrado() {
        Incidente incidente = crearIncidente();

        TransicionEstadoInvalidaException excepcion =
                assertThrows(
                        TransicionEstadoInvalidaException.class,
                        incidente::cerrar
                );

        assertTrue(
                excepcion.getMessage().contains("REPORTADO")
        );

        assertTrue(
                excepcion.getMessage().contains("CERRADO")
        );
    }

    @Test
    void noDebePermitirCambiosDespuesDeCerrar() {
        Incidente incidente = crearIncidente();

        incidente.validar();
        incidente.clasificar(new PuntajeRiesgo(75));
        incidente.asignarRecurso();
        incidente.iniciarAtencion();
        incidente.resolver();
        incidente.cerrar();

        assertThrows(
                TransicionEstadoInvalidaException.class,
                incidente::iniciarAtencion
        );
    }

    @Test
    void debeRechazarDescripcionVacia() {
        assertThrows(
                IncidenteInvalidoException.class,
                () -> Incidente.reportar(
                        TipoIncidente.INUNDACION,
                        " ",
                        ubicacion,
                        contacto,
                        2,
                        false
                )
        );
    }

    @Test
    void debeRechazarDescripcionMuyCorta() {
        assertThrows(
                IncidenteInvalidoException.class,
                () -> Incidente.reportar(
                        TipoIncidente.INUNDACION,
                        "Lluvia",
                        ubicacion,
                        contacto,
                        2,
                        false
                )
        );
    }

    @Test
    void debeRechazarCantidadNegativaDePersonas() {
        assertThrows(
                IncidenteInvalidoException.class,
                () -> Incidente.reportar(
                        TipoIncidente.INUNDACION,
                        "Inundacion en zona residencial",
                        ubicacion,
                        contacto,
                        -1,
                        false
                )
        );
    }

    @Test
    void debeRechazarUbicacionNula() {
        assertThrows(
                IncidenteInvalidoException.class,
                () -> Incidente.reportar(
                        TipoIncidente.INUNDACION,
                        "Inundacion en zona residencial",
                        null,
                        contacto,
                        5,
                        false
                )
        );
    }

    @Test
    void debeRechazarContactoNulo() {
        assertThrows(
                IncidenteInvalidoException.class,
                () -> Incidente.reportar(
                        TipoIncidente.INUNDACION,
                        "Inundacion en zona residencial",
                        ubicacion,
                        null,
                        5,
                        false
                )
        );
    }

    @Test
    void debeRechazarConsultaDeTiempoSinClasificacion() {
        Incidente incidente = crearIncidente();

        assertThrows(
                IncidenteInvalidoException.class,
                incidente::
                        obtenerTiempoMaximoRespuestaMinutos
        );
    }

    @Test
    void debeCancelarIncidenteReportado() {
        Incidente incidente = crearIncidente();

        incidente.cancelar();

        assertEquals(
                EstadoIncidente.CANCELADO,
                incidente.getEstado()
        );
    }

    @Test
    void debeRechazarIncidenteReportado() {
        Incidente incidente = crearIncidente();

        incidente.rechazar();

        assertEquals(
                EstadoIncidente.RECHAZADO,
                incidente.getEstado()
        );
    }

    private Incidente crearIncidente() {
        return Incidente.reportar(
                TipoIncidente.INUNDACION,
                "Inundacion con varias personas afectadas",
                ubicacion,
                contacto,
                15,
                true
        );
    }
}