package com.alertacore.incidentservice.application.usecase;
import com.alertacore.incidentservice.application.command.RegistrarIncidenteCommand;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrarIncidenteUseCaseTest {

    private RepositorioIncidentePort repositorio;
    private RegistrarIncidenteUseCase casoUso;

    @BeforeEach
    void configurar() {
        repositorio = mock(
                RepositorioIncidentePort.class
        );

        casoUso = new RegistrarIncidenteUseCase(
                repositorio
        );
    }

    @Test
    void debeRegistrarIncidenteCorrectamente() {
        RegistrarIncidenteCommand comando =
                crearComandoValido();

        when(repositorio.guardar(any(Incidente.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        ResultadoIncidente resultado =
                casoUso.ejecutar(comando);

        assertNotNull(resultado);
        assertNotNull(resultado.id());

        assertEquals(
                TipoIncidente.INUNDACION,
                resultado.tipo()
        );

        assertEquals(
                EstadoIncidente.REPORTADO,
                resultado.estado()
        );

        assertEquals(
                "Inundacion con personas afectadas",
                resultado.descripcion()
        );

        assertEquals(
                15,
                resultado.cantidadPersonasAfectadas()
        );

        assertTrue(resultado.hayPersonasHeridas());
        assertNull(resultado.nivelRiesgo());

        verify(repositorio, times(1))
                .guardar(any(Incidente.class));
    }

    @Test
    void debeEnviarIncidenteCorrectoAlRepositorio() {
        RegistrarIncidenteCommand comando =
                crearComandoValido();

        when(repositorio.guardar(any(Incidente.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        casoUso.ejecutar(comando);

        ArgumentCaptor<Incidente> captor =
                ArgumentCaptor.forClass(
                        Incidente.class
                );

        verify(repositorio).guardar(
                captor.capture()
        );

        Incidente incidente = captor.getValue();

        assertEquals(
                TipoIncidente.INUNDACION,
                incidente.getTipo()
        );

        assertEquals(
                "Carrera 7 con Calle 72, Bogota",
                incidente.getUbicacion().getDireccion()
        );

        assertEquals(
                "operador@alertacore.com",
                incidente.getInformacionContacto()
                        .getCorreo()
        );
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
    void debeRechazarCorreoInvalido() {
        RegistrarIncidenteCommand comando =
                new RegistrarIncidenteCommand(
                        TipoIncidente.INUNDACION,
                        "Inundacion con personas afectadas",
                        4.6579,
                        -74.0596,
                        "Carrera 7 con Calle 72, Bogota",
                        "Operador de prueba",
                        "correo-invalido",
                        "3001234567",
                        15,
                        true
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> casoUso.ejecutar(comando)
        );

        verifyNoInteractions(repositorio);
    }

    @Test
    void debeRechazarRepositorioNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new RegistrarIncidenteUseCase(null)
        );
    }

    private RegistrarIncidenteCommand crearComandoValido() {
        return new RegistrarIncidenteCommand(
                TipoIncidente.INUNDACION,
                "Inundacion con personas afectadas",
                4.6579,
                -74.0596,
                "Carrera 7 con Calle 72, Bogota",
                "Operador de prueba",
                "OPERADOR@alertacore.com",
                "3001234567",
                15,
                true
        );
    }
}