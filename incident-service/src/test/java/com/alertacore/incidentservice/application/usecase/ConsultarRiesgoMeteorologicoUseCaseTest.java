package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.domain.model.RiesgoMeteorologicoResultado;
import com.alertacore.incidentservice.domain.enums.NivelRiesgoMeteorologico;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.model.gateway.IncidenteGateway;
import com.alertacore.incidentservice.domain.model.gateway.ServicioMeteorologicoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ConsultarRiesgoMeteorologicoUseCaseTest {

    private IncidenteGateway repositorioIncidente;
    private ServicioMeteorologicoGateway servicioMeteorologico;
    private ConsultarRiesgoMeteorologicoUseCase casoUso;

    @BeforeEach
    void configurar() {
        repositorioIncidente = mock(
                IncidenteGateway.class
        );

        servicioMeteorologico = mock(
                ServicioMeteorologicoGateway.class
        );

        casoUso = new ConsultarRiesgoMeteorologicoUseCase(
                repositorioIncidente,
                servicioMeteorologico
        );
    }

    @Test
    void debeConsultarRiesgoMeteorologicoAlto() {
        Incidente incidente = crearIncidente();

        CondicionMeteorologica condicion =
                new CondicionMeteorologica(
                        4.6579,
                        -74.0596,
                        15.4,
                        8.2,
                        8.2,
                        0,
                        12.5,
                        63,
                        LocalDateTime.now(),
                        "Open-Meteo"
                );

        when(
                repositorioIncidente.buscarPorId(
                        incidente.getId()
                )
        ).thenReturn(Optional.of(incidente));

        when(
                servicioMeteorologico
                        .consultarCondicionActual(
                                4.6579,
                                -74.0596
                        )
        ).thenReturn(condicion);

        RiesgoMeteorologicoResultado resultado =
                casoUso.ejecutar(incidente.getId());

        assertEquals(
                incidente.getId(),
                resultado.incidenteId()
        );

        assertEquals(
                8.2,
                resultado.precipitacionMilimetros()
        );

        assertEquals(
                NivelRiesgoMeteorologico.ALTO,
                resultado.nivelRiesgoMeteorologico()
        );

        assertTrue(
                resultado.lluviaIntensa()
        );

        assertEquals(
                "Open-Meteo",
                resultado.fuente()
        );

        verify(repositorioIncidente)
                .buscarPorId(incidente.getId());

        verify(servicioMeteorologico)
                .consultarCondicionActual(
                        4.6579,
                        -74.0596
                );
    }

    @Test
    void debeLanzarExcepcionSiNoExisteIncidente() {
        UUID id = UUID.randomUUID();

        when(repositorioIncidente.buscarPorId(id))
                .thenReturn(Optional.empty());

        assertThrows(
                IncidenteNoEncontradoException.class,
                () -> casoUso.ejecutar(id)
        );

        verifyNoInteractions(
                servicioMeteorologico
        );
    }

    @Test
    void debeRechazarIdentificadorNulo() {
        assertThrows(
                NullPointerException.class,
                () -> casoUso.ejecutar(null)
        );

        verifyNoInteractions(
                repositorioIncidente,
                servicioMeteorologico
        );
    }

    @Test
    void debeRechazarRepositorioNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new ConsultarRiesgoMeteorologicoUseCase(
                        null,
                        servicioMeteorologico
                )
        );
    }

    @Test
    void debeRechazarServicioMeteorologicoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new ConsultarRiesgoMeteorologicoUseCase(
                        repositorioIncidente,
                        null
                )
        );
    }

    private Incidente crearIncidente() {
        return Incidente.reportar(
                TipoIncidente.INUNDACION,
                "Inundacion con varias personas afectadas",
                new Ubicacion(
                        4.6579,
                        -74.0596,
                        "Carrera 7 con Calle 72, Bogota"
                ),
                new InformacionContacto(
                        "Operador de prueba",
                        "operador@alertacore.com",
                        "3001234567"
                ),
                15,
                true
        );
    }
}