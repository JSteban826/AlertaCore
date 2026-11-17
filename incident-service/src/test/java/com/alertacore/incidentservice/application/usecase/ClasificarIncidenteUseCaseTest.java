package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.domain.model.command.ClasificarIncidenteCommand;
import com.alertacore.incidentservice.domain.model.ResultadoClasificacion;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.NivelRiesgo;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.exception.TransicionEstadoInvalidaException;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.gateway.RepositorioHistorialEstadoPort;
import com.alertacore.incidentservice.domain.gateway.IncidenteGateway;
import com.alertacore.incidentservice.domain.usecase.risk.CalculadoraRiesgo;
import com.alertacore.incidentservice.domain.usecase.risk.ReglaAccesoDificil;
import com.alertacore.incidentservice.domain.usecase.risk.ReglaCantidadAfectados;
import com.alertacore.incidentservice.domain.usecase.risk.ReglaIncendio;
import com.alertacore.incidentservice.domain.usecase.risk.ReglaInundacion;
import com.alertacore.incidentservice.domain.usecase.risk.ReglaLluviaIntensa;
import com.alertacore.incidentservice.domain.usecase.risk.ReglaPersonasHeridas;
import com.alertacore.incidentservice.domain.usecase.risk.ReglaRiesgo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ClasificarIncidenteUseCaseTest {

    private IncidenteGateway repositorio;
    private RepositorioHistorialEstadoPort repositorioHistorial;
    private CalculadoraRiesgo calculadora;
    private ClasificarIncidenteUseCase casoUso;

    @BeforeEach
    void configurar() {
        repositorio = mock(
                IncidenteGateway.class
        );

        repositorioHistorial = mock(
                RepositorioHistorialEstadoPort.class
        );

        List<ReglaRiesgo> reglas = List.of(
                new ReglaPersonasHeridas(),
                new ReglaCantidadAfectados(),
                new ReglaLluviaIntensa(),
                new ReglaIncendio(),
                new ReglaInundacion(),
                new ReglaAccesoDificil()
        );

        calculadora = new CalculadoraRiesgo(
                reglas
        );

        casoUso = new ClasificarIncidenteUseCase(
                repositorio,
                repositorioHistorial,
                calculadora
        );
    }

    @Test
    void debeClasificarIncidenteCritico() {
        Incidente incidente =
                crearIncidenteValidado();

        when(repositorio.buscarPorId(incidente.getId()))
                .thenReturn(Optional.of(incidente));

        when(repositorio.guardar(any(Incidente.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        when(
                repositorioHistorial.guardar(
                        any(RegistroHistorialEstado.class)
                )
        ).thenAnswer(invocacion ->
                invocacion.getArgument(0)
        );

        ClasificarIncidenteCommand comando =
                new ClasificarIncidenteCommand(
                        incidente.getId(),
                        true,
                        true
                );

        ResultadoClasificacion resultado =
                casoUso.ejecutar(comando);

        assertEquals(
                incidente.getId(),
                resultado.incidenteId()
        );

        assertEquals(
                95,
                resultado.puntaje()
        );

        assertEquals(
                NivelRiesgo.CRITICO,
                resultado.nivelRiesgo()
        );

        assertEquals(
                10,
                resultado.tiempoMaximoRespuestaMinutos()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                resultado.estado()
        );

        verify(repositorio)
                .buscarPorId(incidente.getId());

        verify(repositorio)
                .guardar(incidente);

        verify(repositorioHistorial)
                .guardar(
                        any(RegistroHistorialEstado.class)
                );
    }

    @Test
    void debeGuardarIncidenteClasificado() {
        Incidente incidente =
                crearIncidenteValidado();

        when(repositorio.buscarPorId(incidente.getId()))
                .thenReturn(Optional.of(incidente));

        when(repositorio.guardar(any(Incidente.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        when(
                repositorioHistorial.guardar(
                        any(RegistroHistorialEstado.class)
                )
        ).thenAnswer(invocacion ->
                invocacion.getArgument(0)
        );

        ClasificarIncidenteCommand comando =
                new ClasificarIncidenteCommand(
                        incidente.getId(),
                        true,
                        true
                );

        casoUso.ejecutar(comando);

        ArgumentCaptor<Incidente> captor =
                ArgumentCaptor.forClass(
                        Incidente.class
                );

        verify(repositorio)
                .guardar(captor.capture());

        Incidente incidenteGuardado =
                captor.getValue();

        assertTrue(
                incidenteGuardado.estaClasificado()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                incidenteGuardado.getEstado()
        );

        assertEquals(
                NivelRiesgo.CRITICO,
                incidenteGuardado.obtenerNivelRiesgo()
        );

        assertEquals(
                95,
                incidenteGuardado
                        .getPuntajeRiesgo()
                        .getValor()
        );
    }

    @Test
    void debeGuardarHistorialAlClasificarIncidente() {
        Incidente incidente =
                crearIncidenteValidado();

        when(repositorio.buscarPorId(incidente.getId()))
                .thenReturn(Optional.of(incidente));

        when(repositorio.guardar(any(Incidente.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        when(
                repositorioHistorial.guardar(
                        any(RegistroHistorialEstado.class)
                )
        ).thenAnswer(invocacion ->
                invocacion.getArgument(0)
        );

        ClasificarIncidenteCommand comando =
                new ClasificarIncidenteCommand(
                        incidente.getId(),
                        true,
                        true
                );

        casoUso.ejecutar(comando);

        ArgumentCaptor<RegistroHistorialEstado> captor =
                ArgumentCaptor.forClass(
                        RegistroHistorialEstado.class
                );

        verify(repositorioHistorial)
                .guardar(captor.capture());

        RegistroHistorialEstado registro =
                captor.getValue();

        assertEquals(
                incidente.getId(),
                registro.incidenteId()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                registro.estadoAnterior()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                registro.nuevoEstado()
        );

        assertEquals(
                "Clasificacion automatica de riesgo",
                registro.motivo()
        );

        assertEquals(
                "SISTEMA_CLASIFICACION",
                registro.responsable()
        );
    }

    @Test
    void debeLanzarExcepcionSiElIncidenteNoExiste() {
        UUID id = UUID.randomUUID();

        when(repositorio.buscarPorId(id))
                .thenReturn(Optional.empty());

        ClasificarIncidenteCommand comando =
                new ClasificarIncidenteCommand(
                        id,
                        false,
                        false
                );

        assertThrows(
                IncidenteNoEncontradoException.class,
                () -> casoUso.ejecutar(comando)
        );

        verify(repositorio)
                .buscarPorId(id);

        verify(
                repositorio,
                never()
        ).guardar(any(Incidente.class));

        verifyNoInteractions(
                repositorioHistorial
        );
    }

    @Test
    void debeRechazarClasificacionSiEstaReportado() {
        Incidente incidente =
                crearIncidenteReportado();

        when(repositorio.buscarPorId(incidente.getId()))
                .thenReturn(Optional.of(incidente));

        ClasificarIncidenteCommand comando =
                new ClasificarIncidenteCommand(
                        incidente.getId(),
                        true,
                        true
                );

        assertThrows(
                TransicionEstadoInvalidaException.class,
                () -> casoUso.ejecutar(comando)
        );

        verify(repositorio)
                .buscarPorId(incidente.getId());

        verify(
                repositorio,
                never()
        ).guardar(any(Incidente.class));

        verifyNoInteractions(
                repositorioHistorial
        );
    }

    @Test
    void debeRechazarComandoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> casoUso.ejecutar(null)
        );

        verifyNoInteractions(
                repositorio,
                repositorioHistorial
        );
    }

    @Test
    void debeRechazarIdentificadorNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ClasificarIncidenteCommand(
                        null,
                        false,
                        false
                )
        );
    }

    @Test
    void debeRechazarRepositorioIncidenteNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new ClasificarIncidenteUseCase(
                        null,
                        repositorioHistorial,
                        calculadora
                )
        );
    }

    @Test
    void debeRechazarRepositorioHistorialNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new ClasificarIncidenteUseCase(
                        repositorio,
                        null,
                        calculadora
                )
        );
    }

    @Test
    void debeRechazarCalculadoraNula() {
        assertThrows(
                NullPointerException.class,
                () -> new ClasificarIncidenteUseCase(
                        repositorio,
                        repositorioHistorial,
                        null
                )
        );
    }

    private Incidente crearIncidenteValidado() {
        Incidente incidente =
                crearIncidenteReportado();

        incidente.validar();

        return incidente;
    }

    private Incidente crearIncidenteReportado() {
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