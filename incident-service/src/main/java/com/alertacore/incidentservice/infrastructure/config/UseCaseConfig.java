package com.alertacore.incidentservice.infrastructure.config;

import com.alertacore.incidentservice.application.usecase.CambiarEstadoIncidenteUseCase;
import com.alertacore.incidentservice.application.usecase.ClasificarIncidenteUseCase;
import com.alertacore.incidentservice.application.usecase.RegistrarIncidenteUseCase;
import com.alertacore.incidentservice.domain.port.out.RepositorioIncidentePort;
import com.alertacore.incidentservice.domain.service.CalculadoraRiesgo;
import com.alertacore.incidentservice.domain.service.ReglaAccesoDificil;
import com.alertacore.incidentservice.domain.service.ReglaCantidadAfectados;
import com.alertacore.incidentservice.domain.service.ReglaIncendio;
import com.alertacore.incidentservice.domain.service.ReglaInundacion;
import com.alertacore.incidentservice.domain.service.ReglaLluviaIntensa;
import com.alertacore.incidentservice.domain.service.ReglaPersonasHeridas;
import com.alertacore.incidentservice.domain.service.ReglaRiesgo;
import com.alertacore.incidentservice.infrastructure.persistence.adapter.IncidentePersistenceAdapter;
import com.alertacore.incidentservice.infrastructure.persistence.mapper.IncidentePersistenceMapper;
import com.alertacore.incidentservice.infrastructure.persistence.repository.JpaIncidenteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            RepositorioIncidentePort repositorio,
            CalculadoraRiesgo calculadoraRiesgo
    ) {
        return new ClasificarIncidenteUseCase(
                repositorio,
                calculadoraRiesgo
        );
    }

    @Bean
    public CambiarEstadoIncidenteUseCase cambiarEstadoIncidenteUseCase(
            RepositorioIncidentePort repositorio
    ) {
        return new CambiarEstadoIncidenteUseCase(
                repositorio
        );
    }
}