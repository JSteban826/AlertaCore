package com.alertacore.incidentservice.application.config;

import com.alertacore.incidentservice.domain.model.gateway.*;
import com.alertacore.incidentservice.domain.usecase.*;
import com.alertacore.incidentservice.domain.usecase.risk.*;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.adapter.EstadisticasIncidentePersistenceAdapter;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.adapter.HistorialEstadoPersistenceAdapter;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.adapter.IncidentePersistenceAdapter;
import com.alertacore.incidentservice.infrastructure.mapper.HistorialEstadoPersistenceMapper;
import com.alertacore.incidentservice.infrastructure.mapper.IncidentePersistenceMapper;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.repository.JpaEstadisticasIncidenteRepository;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.repository.JpaHistorialEstadoRepository;
import com.alertacore.incidentservice.infrastructure.driver_adapter.jpa_repository.repository.JpaIncidenteRepository;
import com.alertacore.incidentservice.infrastructure.driver_adapter.transaction.SpringTransaccionAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.alertacore.incidentservice.domain.model.gateway
        .HistorialEstadoGateway;

import com.alertacore.incidentservice.domain.model.gateway
        .IncidenteGateway;

import com.alertacore.incidentservice.domain.model.gateway
        .ServicioRecursosGateway;

import com.alertacore.incidentservice.domain.usecase
        .AsignarRecursoUseCase;

import java.util.List;

@Configuration
public class UseCaseConfig {

    @Bean
    public IncidentePersistenceMapper
    incidentePersistenceMapper() {

        return new IncidentePersistenceMapper();
    }

    @Bean
    public IncidenteGateway incidenteGateway(
            JpaIncidenteRepository repositorioJpa,
            IncidentePersistenceMapper mapper
    ) {
        return new IncidentePersistenceAdapter(
                repositorioJpa,
                mapper
        );
    }

    @Bean
    public HistorialEstadoPersistenceMapper
    historialEstadoPersistenceMapper() {

        return new HistorialEstadoPersistenceMapper();
    }

    @Bean
    public HistorialEstadoGateway historialEstadoGateway(
            JpaHistorialEstadoRepository repositorioHistorialJpa,
            JpaIncidenteRepository repositorioIncidenteJpa,
            HistorialEstadoPersistenceMapper mapper
    ) {
        return new HistorialEstadoPersistenceAdapter(
                repositorioHistorialJpa,
                repositorioIncidenteJpa,
                mapper
        );
    }
    @Bean
    public TransaccionGateway transaccionGateway(
            PlatformTransactionManager transactionManager
    ) {
        TransactionTemplate transactionTemplate =
                new TransactionTemplate(
                        transactionManager
                );

        return new SpringTransaccionAdapter(
                transactionTemplate
        );
    }

    @Bean
    public EstadisticasIncidenteGateway
    estadisticasIncidenteGateway(
            JpaEstadisticasIncidenteRepository
                    repositorioJpa
    ) {

        return new EstadisticasIncidentePersistenceAdapter(
                repositorioJpa
        );
    }

    @Bean
    public CalculadoraRiesgo calculadoraRiesgo() {
        List<ReglaRiesgo> reglas = List.of(
                new ReglaPersonasHeridas(),
                new ReglaCantidadAfectados(),
                new ReglaLluviaIntensa(),
                new ReglaIncendio(),
                new ReglaInundacion(),
                new ReglaAccesoDificil()
        );

        return new CalculadoraRiesgo(reglas);
    }

    @Bean
    public RegistrarIncidenteUseCase
    registrarIncidenteUseCase(
            IncidenteGateway incidenteGateway
    ) {

        return new RegistrarIncidenteUseCase(
                incidenteGateway
        );
    }

    @Bean
    public ConsultarIncidenteUseCase
    consultarIncidenteUseCase(
            IncidenteGateway incidenteGateway
    ) {

        return new ConsultarIncidenteUseCase(
                incidenteGateway
        );
    }

    @Bean
    public ListarIncidentesUseCase
    listarIncidentesUseCase(
            IncidenteGateway incidenteGateway
    ) {

        return new ListarIncidentesUseCase(
                incidenteGateway
        );
    }

    @Bean
    public CambiarEstadoIncidenteUseCase
    cambiarEstadoIncidenteUseCase(
            IncidenteGateway incidenteGateway,
            HistorialEstadoGateway historialGateway
    ) {

        return new CambiarEstadoIncidenteUseCase(
                incidenteGateway,
                historialGateway
        );
    }

    @Bean
    public ClasificarIncidenteUseCase
    clasificarIncidenteUseCase(
            IncidenteGateway incidenteGateway,
            HistorialEstadoGateway historialGateway,
            CalculadoraRiesgo calculadoraRiesgo
    ) {

        return new ClasificarIncidenteUseCase(
                incidenteGateway,
                historialGateway,
                calculadoraRiesgo
        );
    }

    @Bean
    public ConsultarLineaTiempoUseCase
    consultarLineaTiempoUseCase(
            IncidenteGateway incidenteGateway,
            HistorialEstadoGateway historialGateway
    ) {

        return new ConsultarLineaTiempoUseCase(
                incidenteGateway,
                historialGateway
        );
    }

    @Bean
    public ConsultarEstadisticasIncidentesUseCase
    consultarEstadisticasIncidentesUseCase(
            EstadisticasIncidenteGateway
                    estadisticasGateway
    ) {

        return new ConsultarEstadisticasIncidentesUseCase(
                estadisticasGateway
        );
    }

    @Bean
    public ConsultarRiesgoMeteorologicoUseCase
    consultarRiesgoMeteorologicoUseCase(
            IncidenteGateway incidenteGateway,
            ServicioMeteorologicoGateway
                    meteorologicoGateway
    ) {

        return new ConsultarRiesgoMeteorologicoUseCase(
                incidenteGateway,
                meteorologicoGateway
        );
    }

    @Bean
    public GeocodificarDireccionUseCase
    geocodificarDireccionUseCase(
            ServicioGeocodificacionGateway
                    geocodificacionGateway
    ) {

        return new GeocodificarDireccionUseCase(
                geocodificacionGateway
        );
    }

    @Bean
    public IncidenteUseCase incidenteUseCase(
            RegistrarIncidenteUseCase registrar,
            ConsultarIncidenteUseCase consultar,
            ListarIncidentesUseCase listar,
            CambiarEstadoIncidenteUseCase cambiarEstado,
            ClasificarIncidenteUseCase clasificar,
            ConsultarLineaTiempoUseCase lineaTiempo,
            ConsultarEstadisticasIncidentesUseCase estadisticas,
            ConsultarRiesgoMeteorologicoUseCase meteorologia,
            GeocodificarDireccionUseCase geocodificacion,
            TransaccionGateway transaccionGateway
    ) {
        return new IncidenteUseCase(
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

    @Bean
    public AsignarRecursoUseCase asignarRecursoUseCase(
            IncidenteGateway incidenteGateway,
            HistorialEstadoGateway historialEstadoGateway,
            ServicioRecursosGateway servicioRecursosGateway
    ) {
        return new AsignarRecursoUseCase(
                incidenteGateway,
                historialEstadoGateway,
                servicioRecursosGateway
        );
    }
}