package com.alertacore.incidentservice.infrastructure.entry_points.rest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI incidentServiceOpenAPI() {
        Contact contacto = new Contact()
                .name("Equipo AlertaCore")
                .email("contacto@alertacore.local");

        License licencia = new License()
                .name("Uso académico");

        Info informacion = new Info()
                .title("AlertaCore Incident Service API")
                .version("1.0.0")
                .description(
                        """
                        API para registrar, consultar, validar y clasificar
                        incidentes de emergencia.

                        Funcionalidades principales:

                        - Registro y consulta de incidentes.
                        - Clasificación automática del riesgo.
                        - Máquina de estados.
                        - Línea de tiempo y auditoría.
                        - Estadísticas de tiempos de respuesta.
                        - Consulta meteorológica con Open-Meteo.
                        - Geocodificación con OpenStreetMap Nominatim.
                        """
                )
                .contact(contacto)
                .license(licencia);

        Server servidorLocal = new Server()
                .url("http://localhost:8081")
                .description("Servidor local de desarrollo");

        ExternalDocumentation documentacionExterna =
                new ExternalDocumentation()
                        .description(
                                "Documentación del proyecto AlertaCore"
                        );

        return new OpenAPI()
                .info(informacion)
                .servers(List.of(servidorLocal))
                .externalDocs(documentacionExterna)
                .components(new Components());
    }
}