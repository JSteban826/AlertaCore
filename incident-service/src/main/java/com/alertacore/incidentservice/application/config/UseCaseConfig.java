package com.alertacore.incidentservice.application.config;

import com.alertacore.incidentservice.application.port.in
        .CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.application.port.in
        .ClasificarIncidentePort;
import com.alertacore.incidentservice.application.usecase.*;
import com.alertacore.incidentservice.domain.model.gateway.*;
import com.alertacore.incidentservice.domain.usecase.risk.*;
import com.alertacore.incidentservice.infrastructure.persistence.adapter
        .EstadisticasIncidentePersistenceAdapter;
import com.alertacore.incidentservice.infrastructure.persistence.adapter
        .HistorialEstadoPersistenceAdapter;
import com.alertacore.incidentservice.infrastructure.persistence.adapter
        .IncidentePersistenceAdapter;
import com.alertacore.incidentservice.infrastructure.persistence.mapper
        .HistorialEstadoPersistenceMapper;
import com.alertacore.incidentservice.infrastructure.persistence.mapper
        .IncidentePersistenceMapper;
import com.alertacore.incidentservice.infrastructure.persistence.repository
        .JpaEstadisticasIncidenteRepository;
import com.alertacore.incidentservice.infrastructure.persistence.repository
        .JpaHistorialEstadoRepository;
import com.alertacore.incidentservice.infrastructure.persistence.repository
        .JpaIncidenteRepository;
import com.alertacore.incidentservice.infrastructure.transaction
        .CambiarEstadoTransaccional;
import com.alertacore.incidentservice.infrastructure.transaction
        .ClasificarIncidenteTransaccional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
    @Primary
    public ClasificarIncidentePort
    clasificarIncidenteTransaccional(
            ClasificarIncidenteUseCase casoUso
    ) {

        return new ClasificarIncidenteTransaccional(
                casoUso
        );
    }

    @Bean
    @Primary
    public CambiarEstadoIncidentePort
    cambiarEstadoTransaccional(
            CambiarEstadoIncidenteUseCase casoUso
    ) {

        return new CambiarEstadoTransaccional(
                casoUso
        );
    }
}