package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.model.ResultadoClasificacion;
import com.alertacore.incidentservice.domain.model.ResultadoIncidente;
import com.alertacore.incidentservice.domain.model.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.domain.model.command.ClasificarIncidenteCommand;
import com.alertacore.incidentservice.domain.model.gateway.TransaccionGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IncidenteUseCaseTest {

    private RegistrarIncidenteUseCase registrar;
    private ConsultarIncidenteUseCase consultar;
    private ListarIncidentesUseCase listar;

    private CambiarEstadoIncidenteUseCase cambiarEstado;
    private ClasificarIncidenteUseCase clasificar;

    private ConsultarLineaTiempoUseCase lineaTiempo;

    private ConsultarEstadisticasIncidentesUseCase
            estadisticas;

    private ConsultarRiesgoMeteorologicoUseCase
            meteorologia;

    private GeocodificarDireccionUseCase
            geocodificacion;

    private TransaccionGateway transaccionGateway;

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
                mock(CambiarEstadoIncidenteUseCase.class);

        clasificar =
                mock(ClasificarIncidenteUseCase.class);

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
                mock(
                        GeocodificarDireccionUseCase.class
                );

        transaccionGateway =
                mock(TransaccionGateway.class);

        /*
         * El gateway simulado ejecuta inmediatamente
         * la operación recibida.
         *
         * Esto permite probar que IncidenteUseCase
         * envuelve correctamente las operaciones de
         * cambio de estado y clasificación.
         */
        doAnswer(
                invocacion -> {
                    Supplier<?> operacion =
                            invocacion.getArgument(0);

                    return operacion.get();
                }
        ).when(
                transaccionGateway
        ).ejecutar(
                any()
        );

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
                        geocodificacion,
                        transaccionGateway
                );
    }

    @Test
    void debeDelegarLaConsultaDelIncidente() {
        UUID incidenteId = UUID.randomUUID();

        ResultadoIncidente esperado =
                mock(ResultadoIncidente.class);

        when(
                consultar.ejecutar(
                        incidenteId
                )
        ).thenReturn(
                esperado
        );

        ResultadoIncidente resultado =
                incidenteUseCase.consultarIncidente(
                        incidenteId
                );

        assertSame(
                esperado,
                resultado
        );

        verify(consultar)
                .ejecutar(
                        incidenteId
                );
    }

    @Test
    void debeEjecutarCambioDeEstadoDentroDeTransaccion() {
        CambiarEstadoIncidenteCommand comando =
                mock(
                        CambiarEstadoIncidenteCommand.class
                );

        ResultadoIncidente esperado =
                mock(ResultadoIncidente.class);

        when(
                cambiarEstado.ejecutar(
                        comando
                )
        ).thenReturn(
                esperado
        );

        ResultadoIncidente resultado =
                incidenteUseCase
                        .cambiarEstadoIncidente(
                                comando
                        );

        assertSame(
                esperado,
                resultado
        );

        verify(transaccionGateway)
                .ejecutar(
                        any()
                );

        verify(cambiarEstado)
                .ejecutar(
                        comando
                );
    }

    @Test
    void debeEjecutarClasificacionDentroDeTransaccion() {
        ClasificarIncidenteCommand comando =
                mock(
                        ClasificarIncidenteCommand.class
                );

        ResultadoClasificacion esperado =
                mock(ResultadoClasificacion.class);

        when(
                clasificar.ejecutar(
                        comando
                )
        ).thenReturn(
                esperado
        );

        ResultadoClasificacion resultado =
                incidenteUseCase.clasificarIncidente(
                        comando
                );

        assertSame(
                esperado,
                resultado
        );

        verify(transaccionGateway)
                .ejecutar(
                        any()
                );

        verify(clasificar)
                .ejecutar(
                        comando
                );
    }
}