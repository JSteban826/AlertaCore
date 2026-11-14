package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.application.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.exception.TransicionEstadoInvalidaException;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.PuntajeRiesgo;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CambiarEstadoIncidenteUseCaseTest {

    private RepositorioIncidentePort repositorio;
    private CambiarEstadoIncidenteUseCase casoUso;

    @BeforeEach
    void configurar() {
        repositorio = mock(
                RepositorioIncidentePort.class
        );

        casoUso = new CambiarEstadoIncidenteUseCase(
                repositorio
        );
    }

    @Test
    void debeCambiarIncidenteDeReportadoAValidado() {
        Incidente incidente = crearIncidenteReportado();

        when(repositorio.buscarPorId(incidente.getId()))
                .thenReturn(Optional.of(incidente));

        when(repositorio.guardar(any(Incidente.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        incidente.getId(),
                        EstadoIncidente.VALIDADO,
                        "El operador verifico la informacion"
                );

        ResultadoIncidente resultado =
                casoUso.ejecutar(comando);

        assertEquals(
                EstadoIncidente.VALIDADO,
                resultado.estado()
        );

        verify(repositorio)
                .buscarPorId(incidente.getId());

        verify(repositorio)
                .guardar(incidente);
    }

    @Test
    void debeGuardarIncidenteConNuevoEstado() {
        Incidente incidente = crearIncidenteReportado();

        when(repositorio.buscarPorId(incidente.getId()))
                .thenReturn(Optional.of(incidente));

        when(repositorio.guardar(any(Incidente.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        incidente.getId(),
                        EstadoIncidente.VALIDADO,
                        "Informacion comprobada"
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

        assertEquals(
                EstadoIncidente.VALIDADO,
                incidenteGuardado.getEstado()
        );
    }

    @Test
    void debeCambiarIncidenteClasificadoARecursoAsignado() {
        Incidente incidente = crearIncidenteClasificado();

        when(repositorio.buscarPorId(incidente.getId()))
                .thenReturn(Optional.of(incidente));

        when(repositorio.guardar(any(Incidente.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        incidente.getId(),
                        EstadoIncidente.RECURSO_ASIGNADO,
                        "Se asigno una brigada de rescate"
                );

        ResultadoIncidente resultado =
                casoUso.ejecutar(comando);

        assertEquals(
                EstadoIncidente.RECURSO_ASIGNADO,
                resultado.estado()
        );
    }

    @Test
    void debeRechazarTransicionDeReportadoACerrado() {
        Incidente incidente = crearIncidenteReportado();

        when(repositorio.buscarPorId(incidente.getId()))
                .thenReturn(Optional.of(incidente));

        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        incidente.getId(),
                        EstadoIncidente.CERRADO,
                        "Intento de cierre invalido"
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
    }

    @Test
    void debeLanzarExcepcionSiElIncidenteNoExiste() {
        UUID incidenteId = UUID.randomUUID();

        when(repositorio.buscarPorId(incidenteId))
                .thenReturn(Optional.empty());

        CambiarEstadoIncidenteCommand comando =
                new CambiarEstadoIncidenteCommand(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        "Validacion de informacion"
                );

        assertThrows(
                IncidenteNoEncontradoException.class,
                () -> casoUso.ejecutar(comando)
        );

        verify(repositorio)
                .buscarPorId(incidenteId);

        verify(
                repositorio,
                never()
        ).guardar(any(Incidente.class));
    }

    @Test
    void debeRechazarComandoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> casoUso.ejecutar(null)
        );

        verifyNoInteractions(repositorio);
    }

    @Test
    void debeRechazarIdentificadorNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new CambiarEstadoIncidenteCommand(
                        null,
                        EstadoIncidente.VALIDADO,
                        "Validacion del incidente"
                )
        );
    }

    @Test
    void debeRechazarEstadoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new CambiarEstadoIncidenteCommand(
                        UUID.randomUUID(),
                        null,
                        "Validacion del incidente"
                )
        );
    }

    @Test
    void debeRechazarMotivoVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CambiarEstadoIncidenteCommand(
                        UUID.randomUUID(),
                        EstadoIncidente.VALIDADO,
                        " "
                )
        );
    }

    @Test
    void debeRechazarRepositorioNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new CambiarEstadoIncidenteUseCase(
                        null
                )
        );
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

    private Incidente crearIncidenteClasificado() {
        Incidente incidente = crearIncidenteReportado();

        incidente.validar();
        incidente.clasificar(
                new PuntajeRiesgo(95)
        );

        return incidente;
    }
}