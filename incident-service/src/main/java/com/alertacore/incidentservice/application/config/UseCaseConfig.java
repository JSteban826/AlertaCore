package com.alertacore.incidentservice.application.config;

import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.application.port.in.ClasificarIncidentePort;
import com.alertacore.incidentservice.application.usecase.*;
import com.alertacore.incidentservice.domain.port.out.*;
import com.alertacore.incidentservice.domain.usecase.risk.*;
import com.alertacore.incidentservice.infrastructure.persistence.adapter.EstadisticasIncidentePersistenceAdapter;
import com.alertacore.incidentservice.infrastructure.persistence.adapter.HistorialEstadoPersistenceAdapter;
import com.alertacore.incidentservice.infrastructure.persistence.adapter.IncidentePersistenceAdapter;
import com.alertacore.incidentservice.infrastructure.persistence.mapper.HistorialEstadoPersistenceMapper;
import com.alertacore.incidentservice.infrastructure.persistence.mapper.IncidentePersistenceMapper;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaEstadisticasIncidenteRepository;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaHistorialEstadoRepository;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaIncidenteRepository;
import com.alertacore.incidentservice.infrastructure.transaction.CambiarEstadoTransaccional;
import com.alertacore.incidentservice.infrastructure.transaction.ClasificarIncidenteTransaccional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class UseCaseConfig {

    @Bean
    public IncidentePersistenceMapper incidentePersistenceMapper() {
        return new IncidentePersistenceMapper();
    }

    @Bean
    public RepositorioIncidentePort repositorioIncidentePort(
            JpaIncidenteRepository repositorioJpa,
            IncidentePersistenceMapper mapper
    ) {
        return new IncidentePersistenceAdapter(
                repositorioJpa,
                mapper
        );
    }
    @Bean
    @Primary
    public ClasificarIncidentePort clasificarIncidenteTransaccional(
            ClasificarIncidenteUseCase casoUso
    ) {
        return new ClasificarIncidenteTransaccional(
                casoUso
        );
    }
    @Bean
    @Primary
    public CambiarEstadoIncidentePort cambiarEstadoTransaccional(
            CambiarEstadoIncidenteUseCase casoUso
    ) {
        return new CambiarEstadoTransaccional(
                casoUso
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
    public RegistrarIncidenteUseCase registrarIncidenteUseCase(
            RepositorioIncidentePort repositorio
    ) {
        return new RegistrarIncidenteUseCase(
                repositorio
        );
    }

    @Bean
    public ClasificarIncidenteUseCase clasificarIncidenteUseCase(
            RepositorioIncidentePort repositorioIncidente,
            RepositorioHistorialEstadoPort repositorioHistorial,
            CalculadoraRiesgo calculadoraRiesgo
    ) {
        return new ClasificarIncidenteUseCase(
                repositorioIncidente,
                repositorioHistorial,
                calculadoraRiesgo
        );
    }

    @Bean
    public ConsultarIncidenteUseCase consultarIncidenteUseCase(
            RepositorioIncidentePort repositorio
    ) {
        return new ConsultarIncidenteUseCase(
                repositorio
        );
    }
    @Bean
    public ListarIncidentesUseCase listarIncidentesUseCase(
            RepositorioIncidentePort repositorio
    ) {
        return new ListarIncidentesUseCase(repositorio);
    }
    @Bean
    public HistorialEstadoPersistenceMapper
    historialEstadoPersistenceMapper() {
        return new HistorialEstadoPersistenceMapper();
    }

    @Bean
    public RepositorioHistorialEstadoPort
    repositorioHistorialEstadoPort(
            JpaHistorialEstadoRepository
                    repositorioHistorialJpa,
            JpaIncidenteRepository
                    repositorioIncidenteJpa,
            HistorialEstadoPersistenceMapper mapper
    ) {
        return new HistorialEstadoPersistenceAdapter(
                repositorioHistorialJpa,
                repositorioIncidenteJpa,
                mapper
        );
    }

    @Bean
    public CambiarEstadoIncidenteUseCase cambiarEstadoIncidenteUseCase(
            RepositorioIncidentePort repositorioIncidente,
            RepositorioHistorialEstadoPort repositorioHistorial
    ) {
        return new CambiarEstadoIncidenteUseCase(
                repositorioIncidente,
                repositorioHistorial
        );
    }
    @Bean
    public ConsultarLineaTiempoUseCase consultarLineaTiempoUseCase(
            RepositorioIncidentePort repositorioIncidente,
            RepositorioHistorialEstadoPort repositorioHistorial
    ) {
        return new ConsultarLineaTiempoUseCase(
                repositorioIncidente,
                repositorioHistorial
        );
    }
    @Bean
    public RepositorioEstadisticasIncidentePort
    repositorioEstadisticasIncidentePort(
            JpaEstadisticasIncidenteRepository repositorioJpa
    ) {
        return new EstadisticasIncidentePersistenceAdapter(
                repositorioJpa
        );
    }

    @Bean
    public ConsultarEstadisticasIncidentesUseCase
    consultarEstadisticasIncidentesUseCase(
            RepositorioEstadisticasIncidentePort repositorio
    ) {
        return new ConsultarEstadisticasIncidentesUseCase(
                repositorio
        );
    }
    @Bean
    public ConsultarRiesgoMeteorologicoUseCase
    consultarRiesgoMeteorologicoUseCase(
            RepositorioIncidentePort repositorioIncidente,
            ServicioMeteorologicoPort servicioMeteorologico
    ) {
        return new ConsultarRiesgoMeteorologicoUseCase(
                repositorioIncidente,
                servicioMeteorologico
        );
    }
    @Bean
    public GeocodificarDireccionUseCase geocodificarDireccionUseCase(
            ServicioGeocodificacionPort servicioGeocodificacion
    ) {
        return new GeocodificarDireccionUseCase(
                servicioGeocodificacion
        );
    }

}