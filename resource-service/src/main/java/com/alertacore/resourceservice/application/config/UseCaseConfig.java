package com.alertacore.resourceservice.application.config;

import com.alertacore.resourceservice.domain.usecase.ActualizarUbicacionRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.BuscarRecursoMasCercanoUseCase;
import com.alertacore.resourceservice.domain.usecase.DespacharRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.GenerarReporteUtilizacionUseCase;
import com.alertacore.resourceservice.domain.usecase.LiberarRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.RegistrarRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.ReservarRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.ConsultarRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.BuscarRecursosDisponiblesUseCase;
import com.alertacore.resourceservice.domain.usecase.ListarRecursosUseCase;
import com.alertacore.resourceservice.domain.model.command.DespacharRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.DespachoRecursoDto;
import com.alertacore.resourceservice.domain.model.gateway.NotificacionDespachoGateway;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClientException;
import com.alertacore.resourceservice.domain.service.CalculadorDistanciaHaversine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import com.alertacore.resourceservice.domain.model.gateway.ReservaRecursoGateway;
import com.alertacore.resourceservice.domain.model.gateway.DespachoRecursoGateway;

@Configuration
public class UseCaseConfig {

    @Bean
    public CalculadorDistanciaHaversine calculadorDistanciaHaversine() {
        return new CalculadorDistanciaHaversine();
    }

    @Bean
    public RegistrarRecursoUseCase registrarRecursoUseCase(
            RecursoGateway recursoGateway
    ) {

        return new RegistrarRecursoUseCase(
                recursoGateway
        );
    }

    @Bean
    public BuscarRecursoMasCercanoUseCase buscarRecursoMasCercanoUseCase(

            RecursoGateway recursoGateway,

            CalculadorDistanciaHaversine calculador

    ) {

        return new BuscarRecursoMasCercanoUseCase(
                recursoGateway,
                calculador
        );
    }

    @Bean
    public ReservarRecursoUseCase reservarRecursoUseCase(
            RecursoGateway recursoGateway,
            ReservaRecursoGateway reservaRecursoGateway
    )
    {
        return new ReservarRecursoUseCase(
                recursoGateway,
                reservaRecursoGateway
        );
    }

    @Bean
    public DespacharRecursoUseCase despacharRecursoUseCase(
            RecursoGateway recursoGateway,
            DespachoRecursoGateway despachoRecursoGateway,
            NotificacionDespachoGateway notificacionDespachoGateway,
            PlatformTransactionManager transactionManager
    ) {
        var transaccion = new TransactionTemplate(transactionManager);
        var log = LoggerFactory.getLogger(UseCaseConfig.class);

        return new DespacharRecursoUseCase(recursoGateway, despachoRecursoGateway) {
            @Override
            public DespachoRecursoDto despachar(DespacharRecursoCommand command) {
                return transaccion.execute(estado -> {
                    var despacho = super.despachar(command);

                    TransactionSynchronizationManager.registerSynchronization(
                            new TransactionSynchronization() {
                                @Override
                                public void afterCommit() {
                                    try {
                                        notificacionDespachoGateway.notificarDespacho(
                                                command.incidenteId(),
                                                command.recursoId()
                                        );
                                    } catch (RestClientException e) {
                                        log.warn(
                                                "Despacho confirmado, pero no se pudo notificar. "
                                                        + "incidenteId={}, recursoId={}",
                                                command.incidenteId(),
                                                command.recursoId(),
                                                e
                                        );
                                    }
                                }
                            }
                    );

                    return despacho;
                });
            }
        };
    }

    @Bean
    public LiberarRecursoUseCase liberarRecursoUseCase(
            RecursoGateway recursoGateway,
            ReservaRecursoGateway reservaRecursoGateway
    ) {
        return new LiberarRecursoUseCase(
                recursoGateway,
                reservaRecursoGateway
        );
    }
    @Bean
    public ActualizarUbicacionRecursoUseCase
    actualizarUbicacionRecursoUseCase(
            RecursoGateway recursoGateway
    ) {

        return new ActualizarUbicacionRecursoUseCase(
                recursoGateway
        );
    }

    @Bean
    public GenerarReporteUtilizacionUseCase
    generarReporteUtilizacionUseCase(
            RecursoGateway recursoGateway
    ) {

        return new GenerarReporteUtilizacionUseCase(
                recursoGateway
        );
    }


    @Bean
    public ConsultarRecursoUseCase consultarRecursoUseCase(
            RecursoGateway recursoGateway
    ) {

        return new ConsultarRecursoUseCase(
                recursoGateway
        );
    }

    @Bean
    public BuscarRecursosDisponiblesUseCase
    buscarRecursosDisponiblesUseCase(

            RecursoGateway recursoGateway

    ) {

        return new BuscarRecursosDisponiblesUseCase(
                recursoGateway
        );
    }


    @Bean
    public ListarRecursosUseCase listarRecursosUseCase(
            RecursoGateway recursoGateway
    ) {

        return new ListarRecursosUseCase(
                recursoGateway
        );
    }

}