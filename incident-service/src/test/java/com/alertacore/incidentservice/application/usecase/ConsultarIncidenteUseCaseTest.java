package com.alertacore.incidentservice.application.usecase;

import com.alertacore.incidentservice.application.dto.ResultadoIncidente;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.Incidente;
import com.alertacore.incidentservice.domain.model.InformacionContacto;
import com.alertacore.incidentservice.domain.model.Ubicacion;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ConsultarIncidenteUseCaseTest {

    private RepositorioIncidentePort repositorio;
    private ConsultarIncidenteUseCase casoUso;

    @BeforeEach
    void configurar() {
        repositorio = mock(
                RepositorioIncidentePort.class
        );

        casoUso = new ConsultarIncidenteUseCase(
                repositorio
        );
    }

    @Test
    void debeConsultarIncidenteExistente() {
        Incidente incidente = crearIncidente();

        when(repositorio.buscarPorId(incidente.getId()))
                .thenReturn(Optional.of(incidente));

        ResultadoIncidente resultado =
                casoUso.ejecutar(incidente.getId());

        assertEquals(
                incidente.getId(),
                resultado.id()
        );

        assertEquals(
                TipoIncidente.INUNDACION,
                resultado.tipo()
        );

        assertEquals(
                EstadoIncidente.REPORTADO,
                resultado.estado()
        );

        assertEquals(
                15,
                resultado.cantidadPersonasAfectadas()
        );

        verify(repositorio)
                .buscarPorId(incidente.getId());
    }

    @Test
    void debeLanzarExcepcionSiNoExiste() {
        UUID id = UUID.randomUUID();

        when(repositorio.buscarPorId(id))
                .thenReturn(Optional.empty());

        assertThrows(
                IncidenteNoEncontradoException.class,
                () -> casoUso.ejecutar(id)
        );

        verify(repositorio).buscarPorId(id);
    }

    @Test
    void debeRechazarIdentificadorNulo() {
        assertThrows(
                NullPointerException.class,
                () -> casoUso.ejecutar(null)
        );

        verifyNoInteractions(repositorio);
    }

    @Test
    void debeRechazarRepositorioNulo() {
        assertThrows(
                NullPointerException.class,
                () -> new ConsultarIncidenteUseCase(null)
        );
    }

    private Incidente crearIncidente() {
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