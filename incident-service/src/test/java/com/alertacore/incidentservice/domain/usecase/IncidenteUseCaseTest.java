package com.alertacore.incidentservice.domain.usecase;

import com.alertacore.incidentservice.domain.model.AsignacionRecursoResultado;
import com.alertacore.incidentservice.domain.model.DespachoRecursoResultado;
import com.alertacore.incidentservice.domain.model.ResultadoClasificacion;
import com.alertacore.incidentservice.domain.model.ResultadoIncidente;
import com.alertacore.incidentservice.domain.model.command.AsignarRecursoCommand;
import com.alertacore.incidentservice.domain.model.command.CambiarEstadoIncidenteCommand;
import com.alertacore.incidentservice.domain.model.command.ClasificarIncidenteCommand;
import com.alertacore.incidentservice.domain.model.command.DespacharRecursoCommand;
import com.alertacore.incidentservice.domain.model.gateway.TransaccionGateway;
import org.junit.jupiter.api.BeforeEach;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.domain.model.EstadisticasIncidentesResultado;
import com.alertacore.incidentservice.domain.model.GeocodificacionResultado;
import com.alertacore.incidentservice.domain.model.PaginaIncidentesResultado;
import com.alertacore.incidentservice.domain.model.RegistroLineaTiempoResultado;
import com.alertacore.incidentservice.domain.model.RiesgoMeteorologicoResultado;
import com.alertacore.incidentservice.domain.model.command.GeocodificarDireccionCommand;
import com.alertacore.incidentservice.domain.model.command.RegistrarIncidenteCommand;
import com.alertacore.incidentservice.domain.model.LiberacionRecursoResultado;
import com.alertacore.incidentservice.domain.model.command.LiberarRecursoCommand;
import java.util.List;
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

    private ConsultarEstadisticasIncidentesUseCase estadisticas;
    private ConsultarRiesgoMeteorologicoUseCase meteorologia;
    private GeocodificarDireccionUseCase geocodificacion;
    private AsignarRecursoUseCase asignarRecurso;
    private DespacharRecursoUseCase despacharRecursoUseCase;
    private  LiberarRecursoUseCase liberarRecursoUseCase;
    private TransaccionGateway transaccionGateway;
    private IncidenteUseCase incidenteUseCase;

    @BeforeEach
    void configurar() {
        registrar =
                mock(
                        RegistrarIncidenteUseCase.class
                );

        consultar =
                mock(
                        ConsultarIncidenteUseCase.class
                );

        listar =
                mock(
                        ListarIncidentesUseCase.class
                );

        cambiarEstado =
                mock(
                        CambiarEstadoIncidenteUseCase.class
                );

        clasificar =
                mock(
                        ClasificarIncidenteUseCase.class
                );

        lineaTiempo =
                mock(
                        ConsultarLineaTiempoUseCase.class
                );

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

        asignarRecurso =
                mock(
                        AsignarRecursoUseCase.class
                );

        despacharRecursoUseCase =
                mock(
                        DespacharRecursoUseCase.class
                );
        liberarRecursoUseCase =
                mock(
                        LiberarRecursoUseCase.class
                );


        transaccionGateway =
                mock(
                        TransaccionGateway.class
                );

        /*
         * El gateway simulado ejecuta inmediatamente
         * el Supplier recibido.
         *
         * Sin esta configuración, Mockito devolvería null
         * porque TransaccionGateway es un mock.
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
                        asignarRecurso,
                        liberarRecursoUseCase,
                        despacharRecursoUseCase,
                        transaccionGateway
                );
    }

    @Test
    void debeDelegarLaConsultaDelIncidente() {
        UUID incidenteId =
                UUID.randomUUID();

        ResultadoIncidente esperado =
                mock(
                        ResultadoIncidente.class
                );

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
    void debeDelegarRegistroDeIncidente() {
        RegistrarIncidenteCommand comando =
                mock(RegistrarIncidenteCommand.class);

        ResultadoIncidente esperado =
                mock(ResultadoIncidente.class);

        when(
                registrar.ejecutar(comando)
        ).thenReturn(esperado);

        ResultadoIncidente resultado =
                incidenteUseCase.registrarIncidente(comando);

        assertSame(esperado, resultado);

        verify(registrar)
                .ejecutar(comando);
    }
    @Test
    void debeDelegarListadoDeIncidentes() {
        PaginaIncidentesResultado esperado =
                mock(PaginaIncidentesResultado.class);

        when(
                listar.ejecutar(
                        EstadoIncidente.CLASIFICADO,
                        TipoIncidente.INUNDACION,
                        0,
                        20
                )
        ).thenReturn(esperado);

        PaginaIncidentesResultado resultado =
                incidenteUseCase.listarIncidentes(
                        EstadoIncidente.CLASIFICADO,
                        TipoIncidente.INUNDACION,
                        0,
                        20
                );

        assertSame(esperado, resultado);

        verify(listar)
                .ejecutar(
                        EstadoIncidente.CLASIFICADO,
                        TipoIncidente.INUNDACION,
                        0,
                        20
                );
    }
    @Test
    void debeDelegarConsultaDeLineaDeTiempo() {
        UUID incidenteId = UUID.randomUUID();

        List<RegistroLineaTiempoResultado> esperado =
                List.of(
                        mock(
                                RegistroLineaTiempoResultado.class
                        )
                );

        when(
                lineaTiempo.ejecutar(incidenteId)
        ).thenReturn(esperado);

        List<RegistroLineaTiempoResultado> resultado =
                incidenteUseCase.consultarLineaTiempo(
                        incidenteId
                );

        assertSame(esperado, resultado);

        verify(lineaTiempo)
                .ejecutar(incidenteId);
    }
    @Test
    void debeDelegarConsultaDeEstadisticas() {
        EstadisticasIncidentesResultado esperado =
                mock(
                        EstadisticasIncidentesResultado.class
                );

        when(
                estadisticas.ejecutar()
        ).thenReturn(esperado);

        EstadisticasIncidentesResultado resultado =
                incidenteUseCase.consultarEstadisticas();

        assertSame(esperado, resultado);

        verify(estadisticas)
                .ejecutar();
    }
    @Test
    void debeDelegarConsultaDeRiesgoMeteorologico() {
        UUID incidenteId = UUID.randomUUID();

        RiesgoMeteorologicoResultado esperado =
                mock(
                        RiesgoMeteorologicoResultado.class
                );

        when(
                meteorologia.ejecutar(incidenteId)
        ).thenReturn(esperado);

        RiesgoMeteorologicoResultado resultado =
                incidenteUseCase
                        .consultarRiesgoMeteorologico(
                                incidenteId
                        );

        assertSame(esperado, resultado);

        verify(meteorologia)
                .ejecutar(incidenteId);
    }
    @Test
    void debeDelegarLiberacionDeRecurso() {
        LiberarRecursoCommand comando =
                mock(
                        LiberarRecursoCommand.class
                );

        LiberacionRecursoResultado esperado =
                mock(
                        LiberacionRecursoResultado.class
                );

        when(
                liberarRecursoUseCase.ejecutar(
                        comando
                )
        ).thenReturn(
                esperado
        );

        LiberacionRecursoResultado resultado =
                incidenteUseCase.liberarRecurso(
                        comando
                );

        assertSame(
                esperado,
                resultado
        );

        verify(liberarRecursoUseCase)
                .ejecutar(
                        comando
                );
    }
    @Test
    void debeDelegarGeocodificacionDeDireccion() {
        GeocodificarDireccionCommand comando =
                mock(
                        GeocodificarDireccionCommand.class
                );

        GeocodificacionResultado esperado =
                mock(
                        GeocodificacionResultado.class
                );

        when(
                geocodificacion.ejecutar(comando)
        ).thenReturn(esperado);

        GeocodificacionResultado resultado =
                incidenteUseCase.geocodificarDireccion(
                        comando
                );

        assertSame(esperado, resultado);

        verify(geocodificacion)
                .ejecutar(comando);
    }

    @Test
    void debeEjecutarCambioDeEstadoDentroDeTransaccion() {
        CambiarEstadoIncidenteCommand comando =
                mock(
                        CambiarEstadoIncidenteCommand.class
                );

        ResultadoIncidente esperado =
                mock(
                        ResultadoIncidente.class
                );

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
                mock(
                        ResultadoClasificacion.class
                );

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

    @Test
    void debeEjecutarAsignacionDentroDeTransaccion() {
        AsignarRecursoCommand comando =
                mock(
                        AsignarRecursoCommand.class
                );

        AsignacionRecursoResultado esperado =
                mock(
                        AsignacionRecursoResultado.class
                );

        when(
                asignarRecurso.ejecutar(
                        comando
                )
        ).thenReturn(
                esperado
        );

        AsignacionRecursoResultado resultado =
                incidenteUseCase.asignarRecurso(
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

        verify(asignarRecurso)
                .ejecutar(
                        comando
                );
    }

    @Test
    void debeEjecutarDespachoDentroDeTransaccion() {
        DespacharRecursoCommand comando =
                mock(
                        DespacharRecursoCommand.class
                );

        DespachoRecursoResultado esperado =
                mock(
                        DespachoRecursoResultado.class
                );

        when(
                despacharRecursoUseCase.ejecutar(
                        comando
                )
        ).thenReturn(
                esperado
        );

        DespachoRecursoResultado resultado =
                incidenteUseCase.despacharRecurso(
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

        verify(despacharRecursoUseCase)
                .ejecutar(
                        comando
                );
    }
}