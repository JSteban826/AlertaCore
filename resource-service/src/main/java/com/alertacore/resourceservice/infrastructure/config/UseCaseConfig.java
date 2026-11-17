package com.alertacore.resourceservice.infrastructure.config;

import com.alertacore.resourceservice.application.usecase.ActualizarUbicacionRecursoUseCase;
import com.alertacore.resourceservice.application.usecase.BuscarRecursoMasCercanoUseCase;
import com.alertacore.resourceservice.application.usecase.DespacharRecursoUseCase;
import com.alertacore.resourceservice.application.usecase.GenerarReporteUtilizacionUseCase;
import com.alertacore.resourceservice.application.usecase.LiberarRecursoUseCase;
import com.alertacore.resourceservice.application.usecase.RegistrarRecursoUseCase;
import com.alertacore.resourceservice.application.usecase.ReservarRecursoUseCase;
import com.alertacore.resourceservice.application.usecase.ConsultarRecursoUseCase;
import com.alertacore.resourceservice.application.usecase.BuscarRecursosDisponiblesUseCase;
import com.alertacore.resourceservice.application.usecase.ListarRecursosUseCase;
import com.alertacore.resourceservice.domain.port.out.RepositorioDespachoRecursoPort;
import com.alertacore.resourceservice.domain.port.out.RepositorioRecursoPort;
import com.alertacore.resourceservice.domain.port.out.RepositorioReservaRecursoPort;
import com.alertacore.resourceservice.application.port.in.ConsultarRecursoPort;
import com.alertacore.resourceservice.application.port.in.BuscarRecursosDisponiblesPort;
import com.alertacore.resourceservice.application.port.in.ListarRecursosPort;
import com.alertacore.resourceservice.domain.service.CalculadorDistanciaHaversine;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CalculadorDistanciaHaversine calculadorDistanciaHaversine() {
        return new CalculadorDistanciaHaversine();
    }

    @Bean
    public RegistrarRecursoUseCase registrarRecursoUseCase(
            RepositorioRecursoPort repositorioRecursoPort
    ) {
        return new RegistrarRecursoUseCase(
                repositorioRecursoPort
        );
    }

    @Bean
    public BuscarRecursoMasCercanoUseCase buscarRecursoMasCercanoUseCase(
            RepositorioRecursoPort repositorioRecursoPort,
            CalculadorDistanciaHaversine calculador
    ) {
        return new BuscarRecursoMasCercanoUseCase(
                repositorioRecursoPort,
                calculador
        );
    }

    @Bean
    public ReservarRecursoUseCase reservarRecursoUseCase(
            RepositorioRecursoPort repositorioRecursoPort,
            RepositorioReservaRecursoPort repositorioReservaRecursoPort
    ) {
        return new ReservarRecursoUseCase(
                repositorioRecursoPort,
                repositorioReservaRecursoPort
        );
    }

    @Bean
    public DespacharRecursoUseCase despacharRecursoUseCase(
            RepositorioRecursoPort repositorioRecursoPort,
            RepositorioDespachoRecursoPort repositorioDespachoRecursoPort
    ) {
        return new DespacharRecursoUseCase(
                repositorioRecursoPort,
                repositorioDespachoRecursoPort
        );
    }

    @Bean
    public LiberarRecursoUseCase liberarRecursoUseCase(
            RepositorioRecursoPort repositorioRecursoPort
    ) {
        return new LiberarRecursoUseCase(
                repositorioRecursoPort
        );
    }

    @Bean
    public ActualizarUbicacionRecursoUseCase
    actualizarUbicacionRecursoUseCase(
            RepositorioRecursoPort repositorioRecursoPort
    ) {
        return new ActualizarUbicacionRecursoUseCase(
                repositorioRecursoPort
        );
    }

    @Bean
    public GenerarReporteUtilizacionUseCase
    generarReporteUtilizacionUseCase(
            RepositorioRecursoPort repositorioRecursoPort
    ) {
        return new GenerarReporteUtilizacionUseCase(
                repositorioRecursoPort
        );
    }

    @Bean
    public ConsultarRecursoUseCase consultarRecursoUseCase(
            RepositorioRecursoPort repositorio
    ) {
        return new ConsultarRecursoUseCase(
                repositorio
        );
    }

    @Bean
    public BuscarRecursosDisponiblesUseCase
    buscarRecursosDisponiblesUseCase(
            RepositorioRecursoPort repositorio
    ) {
        return new BuscarRecursosDisponiblesUseCase(
                repositorio
        );
    }

    @Bean
    public ListarRecursosUseCase listarRecursosUseCase(
            RepositorioRecursoPort repositorio
    ) {
        return new ListarRecursosUseCase(
                repositorio
        );
    }

}