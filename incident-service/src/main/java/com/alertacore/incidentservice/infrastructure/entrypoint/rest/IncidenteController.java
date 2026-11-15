package com.alertacore.incidentservice.infrastructure.entrypoint.rest;

import com.alertacore.incidentservice.application.dto.*;
import com.alertacore.incidentservice.application.port.in.*;
import com.alertacore.incidentservice.domain.enums.EstadoIncidente;
import com.alertacore.incidentservice.domain.enums.TipoIncidente;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.CambiarEstadoIncidenteRequest;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.ClasificarIncidenteRequest;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.RegistrarIncidenteRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.alertacore.incidentservice.application.dto.RiesgoMeteorologicoResultado;
import com.alertacore.incidentservice.application.port.in.ConsultarRiesgoMeteorologicoPort;
import com.alertacore.incidentservice.application.dto.GeocodificacionResultado;
import com.alertacore.incidentservice.application.port.in.GeocodificarDireccionPort;
import com.alertacore.incidentservice.infrastructure.entrypoint.rest.request.GeocodificarDireccionRequest;
import com.alertacore.incidentservice.application.port.in.GeocodificarDireccionPort;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.alertacore.incidentservice.application.port.in.GeocodificarDireccionPort;

@RestController
@RequestMapping("/api/v1/incidentes")
public class IncidenteController {

    private final RegistrarIncidentePort registrarIncidente;
    private final ConsultarIncidentePort consultarIncidente;
    private final ConsultarLineaTiempoPort consultarLineaTiempo;
    private final ListarIncidentesPort listarIncidentes;
    private final ClasificarIncidentePort clasificarIncidente;
    private final CambiarEstadoIncidentePort cambiarEstadoIncidente;
    private final ConsultarEstadisticasIncidentesPort  consultarEstadisticas;
    private final ConsultarRiesgoMeteorologicoPort
            consultarRiesgoMeteorologico;
    private final GeocodificarDireccionPort geocodificarDireccion;

    public IncidenteController(
            RegistrarIncidentePort registrarIncidente,
            ConsultarIncidentePort consultarIncidente,
            ConsultarLineaTiempoPort consultarLineaTiempo,
            ListarIncidentesPort listarIncidentes,
            ConsultarEstadisticasIncidentesPort consultarEstadisticas,
            ConsultarRiesgoMeteorologicoPort consultarRiesgoMeteorologico,
            GeocodificarDireccionPort geocodificarDireccion,
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

        this.consultarLineaTiempo = Objects.requireNonNull(
                consultarLineaTiempo,
                "El caso de uso para consultar la linea de tiempo es obligatorio"
        );

        this.listarIncidentes = Objects.requireNonNull(
                listarIncidentes,
                "El caso de uso para listar es obligatorio"
        );

        this.consultarEstadisticas = Objects.requireNonNull(
                consultarEstadisticas,
                "El caso de uso de estadisticas es obligatorio"
        );

        this.consultarRiesgoMeteorologico =
                Objects.requireNonNull(
                        consultarRiesgoMeteorologico,
                        "El caso de uso meteorologico es obligatorio"
                );

        this.geocodificarDireccion =
                Objects.requireNonNull(
                        geocodificarDireccion,
                        "El caso de uso para geocodificar es obligatorio"
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
    @PostMapping("/geocodificar")
    public ResponseEntity<GeocodificacionResultado>
    geocodificar(
            @Valid
            @RequestBody
            GeocodificarDireccionRequest request
    ) {

        GeocodificacionResultado resultado =
                geocodificarDireccion.ejecutar(
                        request.aComando()
                );

        return ResponseEntity.ok(resultado);
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
    @GetMapping("/{incidenteId}/linea-tiempo")
    public ResponseEntity<List<RegistroLineaTiempoResultado>>
    consultarLineaTiempo(
            @PathVariable
            UUID incidenteId
    ) {

        List<RegistroLineaTiempoResultado> resultado =
                consultarLineaTiempo.ejecutar(
                        incidenteId
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
    @GetMapping("/estadisticas/tiempos-respuesta")
    public ResponseEntity<EstadisticasIncidentesResultado>
    consultarEstadisticas() {

        return ResponseEntity.ok(
                consultarEstadisticas.ejecutar()
        );
    }
    @GetMapping("/{incidenteId}/riesgo-meteorologico")
    public ResponseEntity<RiesgoMeteorologicoResultado>
    consultarRiesgoMeteorologico(
            @PathVariable
            UUID incidenteId
    ) {

        RiesgoMeteorologicoResultado resultado =
                consultarRiesgoMeteorologico.ejecutar(
                        incidenteId
                );

        return ResponseEntity.ok(resultado);
    }
}