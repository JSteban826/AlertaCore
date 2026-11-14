package com.alertacore.incidentservice.infrastructure.entrypoint.rest;

import com.alertacore.incidentservice.application.dto.PaginaIncidentesResultado;
import com.alertacore.incidentservice.application.dto.ResultadoClasificacion;
import com.alertacore.incidentservice.application.dto.ResultadoIncidente;
import com.alertacore.incidentservice.application.port.in.CambiarEstadoIncidentePort;
import com.alertacore.incidentservice.application.port.in.ClasificarIncidentePort;
import com.alertacore.incidentservice.application.port.in.ConsultarIncidentePort;
import com.alertacore.incidentservice.application.port.in.ListarIncidentesPort;
import com.alertacore.incidentservice.application.port.in.RegistrarIncidentePort;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.CambiarEstadoIncidenteRequest;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.ClasificarIncidenteRequest;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.RegistrarIncidenteRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidentes")
public class IncidenteController {

    private final RegistrarIncidentePort registrarIncidente;
    private final ConsultarIncidentePort consultarIncidente;
    private final ListarIncidentesPort listarIncidentes;
    private final ClasificarIncidentePort clasificarIncidente;
    private final CambiarEstadoIncidentePort cambiarEstadoIncidente;

    public IncidenteController(
            RegistrarIncidentePort registrarIncidente,
            ConsultarIncidentePort consultarIncidente,
            ListarIncidentesPort listarIncidentes,
            ClasificarIncidentePort clasificarIncidente,
            CambiarEstadoIncidentePort cambiarEstadoIncidente
    ) {
        this.registrarIncidente = Objects.requireNonNull(
                registrarIncidente,
                "El caso de uso para registrar es obligatorio"
        );

        this.consultarIncidente = Objects.requireNonNull(
                consultarIncidente,
                "El caso de uso para consultar es obligatorio"
        );

        this.listarIncidentes = Objects.requireNonNull(
                listarIncidentes,
                "El caso de uso para listar es obligatorio"
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

    @GetMapping("/{incidenteId}")
    public ResponseEntity<ResultadoIncidente> consultarPorId(
            @PathVariable
            UUID incidenteId
    ) {
        ResultadoIncidente resultado =
                consultarIncidente.ejecutar(incidenteId);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping
    public ResponseEntity<PaginaIncidentesResultado> listar(
            @RequestParam(required = false)
            EstadoIncidente estado,

            @RequestParam(required = false)
            TipoIncidente tipo,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        PaginaIncidentesResultado resultado =
                listarIncidentes.ejecutar(
                        estado,
                        tipo,
                        page,
                        size
                );

        return ResponseEntity.ok(resultado);
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

        return ResponseEntity.ok(resultado);
    }
}