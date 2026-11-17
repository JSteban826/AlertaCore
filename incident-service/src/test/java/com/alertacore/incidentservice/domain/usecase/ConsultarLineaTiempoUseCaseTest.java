package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.exception.IncidenteNoEncontradoException;
import com.alertacore.incidentservice.domain.model.RegistroHistorialEstado;
import com.alertacore.incidentservice.domain.model.RegistroLineaTiempoResultado;
import com.alertacore.incidentservice.domain.model.gateway.HistorialEstadoGateway;
import com.alertacore.incidentservice.domain.model.gateway.IncidenteGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ConsultarLineaTiempoUseCaseTest {

    private IncidenteGateway incidenteGateway;
    private HistorialEstadoGateway historialGateway;
    private ConsultarLineaTiempoUseCase casoUso;

    @BeforeEach
    void configurar() {
        incidenteGateway =
                mock(IncidenteGateway.class);

        historialGateway =
                mock(HistorialEstadoGateway.class);

        casoUso =
                new ConsultarLineaTiempoUseCase(
                        incidenteGateway,
                        historialGateway
                );
    }

    @Test
    void debeConsultarLineaTiempoDeIncidenteExistente() {
        UUID incidenteId = UUID.randomUUID();

        LocalDateTime primeraFecha =
                LocalDateTime.of(
                        2026,
                        11,
                        17,
                        10,
                        0
                );

        LocalDateTime segundaFecha =
                LocalDateTime.of(
                        2026,
                        11,
                        17,
                        10,
                        5
                );

        RegistroHistorialEstado primerRegistro =
                crearRegistro(
                        incidenteId,
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        "Informacion verificada",
                        "OPERADOR_API",
                        primeraFecha
                );

        RegistroHistorialEstado segundoRegistro =
                crearRegistro(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        EstadoIncidente.CLASIFICADO,
                        "Clasificacion automatica",
                        "SISTEMA_CLASIFICACION",
                        segundaFecha
                );

        when(
                incidenteGateway.existePorId(
                        incidenteId
                )
        ).thenReturn(true);

        when(
                historialGateway
                        .buscarPorIncidenteIdOrdenado(
                                incidenteId
                        )
        ).thenReturn(
                List.of(
                        primerRegistro,
                        segundoRegistro
                )
        );

        List<RegistroLineaTiempoResultado> resultado =
                casoUso.ejecutar(incidenteId);

        assertEquals(
                2,
                resultado.size()
        );

        RegistroLineaTiempoResultado primero =
                resultado.get(0);

        assertEquals(
                primerRegistro.id(),
                primero.id()
        );

        assertEquals(
                EstadoIncidente.REPORTADO,
                primero.estadoAnterior()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                primero.nuevoEstado()
        );

        assertEquals(
                "Informacion verificada",
                primero.motivo()
        );

        assertEquals(
                "OPERADOR_API",
                primero.responsable()
        );

        assertEquals(
                primeraFecha,
                primero.fechaCambio()
        );

        RegistroLineaTiempoResultado segundo =
                resultado.get(1);

        assertEquals(
                segundoRegistro.id(),
                segundo.id()
        );

        assertEquals(
                EstadoIncidente.VALIDADO,
                segundo.estadoAnterior()
        );

        assertEquals(
                EstadoIncidente.CLASIFICADO,
                segundo.nuevoEstado()
        );

        assertEquals(
                "Clasificacion automatica",
                segundo.motivo()
        );

        assertEquals(
                "SISTEMA_CLASIFICACION",
                segundo.responsable()
        );

        assertEquals(
                segundaFecha,
                segundo.fechaCambio()
        );

        verify(incidenteGateway)
                .existePorId(incidenteId);

        verify(historialGateway)
                .buscarPorIncidenteIdOrdenado(
                        incidenteId
                );
    }

    @Test
    void debeConservarElOrdenCronologicoDelGateway() {
        UUID incidenteId = UUID.randomUUID();

        LocalDateTime primeraFecha =
                LocalDateTime.of(
                        2026,
                        11,
                        17,
                        8,
                        0
                );

        LocalDateTime segundaFecha =
                primeraFecha.plusMinutes(10);

        RegistroHistorialEstado primero =
                crearRegistro(
                        incidenteId,
                        EstadoIncidente.REPORTADO,
                        EstadoIncidente.VALIDADO,
                        "Primera transicion",
                        "OPERADOR_API",
                        primeraFecha
                );

        RegistroHistorialEstado segundo =
                crearRegistro(
                        incidenteId,
                        EstadoIncidente.VALIDADO,
                        EstadoIncidente.CLASIFICADO,
                        "Segunda transicion",
                        "SISTEMA_CLASIFICACION",
                        segundaFecha
                );

        when(
                incidenteGateway.existePorId(
                        incidenteId
                )
        ).thenReturn(true);

        when(
                historialGateway
                        .buscarPorIncidenteIdOrdenado(
                                incidenteId
                        )
        ).thenReturn(
                List.of(
                        primero,
                        segundo
                )
        );

        List<RegistroLineaTiempoResultado> resultado =
                casoUso.ejecutar(incidenteId);

        assertEquals(
                primeraFecha,
                resultado.get(0).fechaCambio()
        );

        assertEquals(
                segundaFecha,
                resultado.get(1).fechaCambio()
        );

        assertTrue(
                resultado.get(0)
                        .fechaCambio()
                        .isBefore(
                                resultado.get(1)
                                        .fechaCambio()
                        )
        );
    }

    @Test
    void debeRetornarListaVaciaCuandoIncidenteNoTieneHistorial() {
        UUID incidenteId = UUID.randomUUID();

        when(
                incidenteGateway.existePorId(
                        incidenteId
                )
        ).thenReturn(true);

        when(
                historialGateway
                        .buscarPorIncidenteIdOrdenado(
                                incidenteId
                        )
        ).thenReturn(List.of());

        List<RegistroLineaTiempoResultado> resultado =
                casoUso.ejecutar(incidenteId);

        assertTrue(
                resultado.isEmpty()
        );

        verify(incidenteGateway)
                .existePorId(incidenteId);

        verify(historialGateway)
                .buscarPorIncidenteIdOrdenado(
                        incidenteId
                );
    }

    @Test
    void debeLanzarExcepcionCuandoIncidenteNoExiste() {
        UUID incidenteId = UUID.randomUUID();

        when(
                incidenteGateway.existePorId(
                        incidenteId
                )
        ).thenReturn(false);

        IncidenteNoEncontradoException excepcion =
                assertThrows(
                        IncidenteNoEncontradoException.class,
                        () -> casoUso.ejecutar(
                                incidenteId
                        )
                );

        assertTrue(
                excepcion.getMessage()
                        .contains(
                                incidenteId.toString()
                        )
        );

        verify(incidenteGateway)
                .existePorId(incidenteId);

        verify(
                historialGateway,
                never()
        ).buscarPorIncidenteIdOrdenado(
                incidenteId
        );
    }

    @Test
    void debeRechazarIdentificadorNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> casoUso.ejecutar(null)
                );

        assertEquals(
                "El identificador del incidente es obligatorio",
                excepcion.getMessage()
        );

        verifyNoInteractions(
                incidenteGateway,
                historialGateway
        );
    }

    @Test
    void debeRechazarGatewayDeIncidentesNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new ConsultarLineaTiempoUseCase(
                                null,
                                historialGateway
                        )
                );

        assertEquals(
                "El repositorio de incidentes es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarGatewayDeHistorialNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new ConsultarLineaTiempoUseCase(
                                incidenteGateway,
                                null
                        )
                );

        assertEquals(
                "El repositorio de historial es obligatorio",
                excepcion.getMessage()
        );
    }

    private RegistroHistorialEstado crearRegistro(
            UUID incidenteId,
            EstadoIncidente estadoAnterior,
            EstadoIncidente nuevoEstado,
            String motivo,
            String responsable,
            LocalDateTime fechaCambio
    ) {
        return new RegistroHistorialEstado(
                UUID.randomUUID(),
                incidenteId,
                estadoAnterior,
                nuevoEstado,
                motivo,
                responsable,
                fechaCambio
        );
    }
}