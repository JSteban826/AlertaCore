package com.alertacore.incidentservice.infrastructure.entrypoint.rest;

import com.alertacore.incidentservice.application.dto.ResultadoClasificacion;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;
import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.application.port.in.ClasificarIncidentePort;
import com.alertacore.incidentservice.application.port.in.RegistrarIncidentePort;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.CambiarEstadoIncidenteRequest;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.ClasificarIncidenteRequest;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.RegistrarIncidenteRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidentes")
public class IncidenteController {

    private final RegistrarIncidentePort registrarIncidente;
    private final ClasificarIncidentePort clasificarIncidente;
    private final CambiarEstadoIncidentePort cambiarEstadoIncidente;

    public IncidenteController(
            RegistrarIncidentePort registrarIncidente,
            ClasificarIncidentePort clasificarIncidente,
            CambiarEstadoIncidentePort cambiarEstadoIncidente
    ) {
        this.registrarIncidente = Objects.requireNonNull(
                registrarIncidente,
                "El caso de uso para registrar es obligatorio"
        );

        this.clasificarIncidente = Objects.requireNonNull(
                clasificarIncidente,
                "El caso de uso para clasificar es obligatorio"
        );

        this.cambiarEstadoIncidente = Objects.requireNonNull(
                cambiarEstadoIncidente,
                "El caso de uso para cambiar estado es obligatorio"
        );
    }

    @PostMapping
    public ResponseEntity<ResultadoIncidente> registrar(
            @Valid
            @RequestBody
            RegistrarIncidenteRequest request
    ) {
        ResultadoIncidente resultado =
                registrarIncidente.ejecutar(
                        request.aComando()
                );

        URI ubicacionRecurso = URI.create(
                "/api/v1/incidentes/" + resultado.id()
        );

        return ResponseEntity
                .created(ubicacionRecurso)
                .body(resultado);
    }

    @PostMapping("/{incidenteId}/clasificar")
    public ResponseEntity<ResultadoClasificacion> clasificar(
            @PathVariable
            UUID incidenteId,

            @Valid
            @RequestBody
            ClasificarIncidenteRequest request
    ) {
        ResultadoClasificacion resultado =
                clasificarIncidente.ejecutar(
                        request.aComando(incidenteId)
                );

        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/{incidenteId}/estado")
    public ResponseEntity<ResultadoIncidente> cambiarEstado(
            @PathVariable
            UUID incidenteId,

            @Valid
            @RequestBody
            CambiarEstadoIncidenteRequest request
    ) {
        ResultadoIncidente resultado =
                cambiarEstadoIncidente.ejecutar(
                        request.aComando(incidenteId)
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resultado);
    }
}