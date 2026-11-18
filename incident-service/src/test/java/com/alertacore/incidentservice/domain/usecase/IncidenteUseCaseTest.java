package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.application.port.in
        .CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.application.port.in
        .ClasificarIncidentePort;
import com.alertacore.incidentservice.domain.model.ResultadoIncidente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IncidenteUseCaseTest {

    private RegistrarIncidenteUseCase registrar;
    private ConsultarIncidenteUseCase consultar;
    private ListarIncidentesUseCase listar;
    private CambiarEstadoIncidentePort cambiarEstado;
    private ClasificarIncidentePort clasificar;
    private ConsultarLineaTiempoUseCase lineaTiempo;

    private ConsultarEstadisticasIncidentesUseCase
            estadisticas;

    private ConsultarRiesgoMeteorologicoUseCase
            meteorologia;

    private GeocodificarDireccionUseCase
            geocodificacion;

    private IncidenteUseCase incidenteUseCase;

    @BeforeEach
    void configurar() {
        registrar =
                mock(RegistrarIncidenteUseCase.class);

        consultar =
                mock(ConsultarIncidenteUseCase.class);

        listar =
                mock(ListarIncidentesUseCase.class);

        cambiarEstado =
                mock(CambiarEstadoIncidentePort.class);

        clasificar =
                mock(ClasificarIncidentePort.class);

        lineaTiempo =
                mock(ConsultarLineaTiempoUseCase.class);

        estadisticas =
                mock(
                        ConsultarEstadisticasIncidentesUseCase.class
                );

        meteorologia =
                mock(
                        ConsultarRiesgoMeteorologicoUseCase.class
                );

        geocodificacion =
                mock(GeocodificarDireccionUseCase.class);

        incidenteUseCase =
                new IncidenteUseCase(
                        registrar,
                        consultar,
                        listar,
                        cambiarEstado,
                        clasificar,
                        lineaTiempo,
                        estadisticas,
                        meteorologia,
                        geocodificacion
                );
    }

    @Test
    void debeDelegarLaConsultaDelIncidente() {
        UUID incidenteId = UUID.randomUUID();

        ResultadoIncidente esperado =
                mock(ResultadoIncidente.class);

        when(
                consultar.ejecutar(incidenteId)
        ).thenReturn(esperado);

        ResultadoIncidente resultado =
                incidenteUseCase.consultarIncidente(
                        incidenteId
                );

        assertSame(
                esperado,
                resultado
        );

        verify(consultar)
                .ejecutar(incidenteId);
    }
}