package com.alertacore.resourceservice.infrastructure.entrypoint.rest.controller;

import com.alertacore.resourceservice.application.command.ActualizarUbicacionRecursoCommand;
import com.alertacore.resourceservice.application.command.BuscarRecursoMasCercanoCommand;
import com.alertacore.resourceservice.application.command.DespacharRecursoCommand;
import com.alertacore.resourceservice.application.command.LiberarRecursoCommand;
import com.alertacore.resourceservice.application.command.RegistrarRecursoCommand;
import com.alertacore.resourceservice.application.command.ReservarRecursoCommand;

import com.alertacore.resourceservice.application.dto.RecursoCercanoDto;
import com.alertacore.resourceservice.application.dto.RecursoDto;
import com.alertacore.resourceservice.application.dto.ReporteUtilizacionDto;

import com.alertacore.resourceservice.application.port.in.ActualizarUbicacionRecursoPort;
import com.alertacore.resourceservice.application.port.in.BuscarRecursoMasCercanoPort;
import com.alertacore.resourceservice.application.port.in.DespacharRecursoPort;
import com.alertacore.resourceservice.application.port.in.GenerarReporteUtilizacionPort;
import com.alertacore.resourceservice.application.port.in.LiberarRecursoPort;
import com.alertacore.resourceservice.application.port.in.RegistrarRecursoPort;
import com.alertacore.resourceservice.application.port.in.ReservarRecursoPort;

import com.alertacore.resourceservice.application.port.in.ConsultarRecursoPort;
import com.alertacore.resourceservice.application.port.in.BuscarRecursosDisponiblesPort;
import com.alertacore.resourceservice.application.port.in.ListarRecursosPort;


import com.alertacore.resourceservice.infrastructure.entrypoint.rest.request.ActualizarUbicacionRequest;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.request.BuscarRecursoMasCercanoRequest;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.request.DespacharRecursoRequest;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.request.LiberarRecursoRequest;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.request.RegistrarRecursoRequest;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.request.ReservarRecursoRequest;

import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.ActualizarUbicacionResponse;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.DespachoRecursoResponse;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.LiberarRecursoResponse;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.RecursoCercanoResponse;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.RecursoResponse;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.ReservaRecursoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import java.util.List;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "Recursos",
        description = "Gestión de recursos de emergencia"
)

@RestController
@RequestMapping("/api/v1/recursos")
@RequiredArgsConstructor
public class RecursoController {

    private final RegistrarRecursoPort registrarRecursoPort;

    private final BuscarRecursoMasCercanoPort buscarRecursoMasCercanoPort;

    private final ReservarRecursoPort reservarRecursoPort;

    private final DespacharRecursoPort despacharRecursoPort;

    private final LiberarRecursoPort liberarRecursoPort;

    private final ActualizarUbicacionRecursoPort
            actualizarUbicacionRecursoPort;

    private final GenerarReporteUtilizacionPort
            generarReporteUtilizacionPort;

    private final ConsultarRecursoPort consultarRecursoPort;

    private final BuscarRecursosDisponiblesPort
            buscarRecursosDisponiblesPort;

    private final ListarRecursosPort listarRecursosPort;

    @Operation(
            summary = "Registrar recurso",
            description = "Registra un nuevo recurso de emergencia"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Recurso creado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida"
            )
    })

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecursoResponse registrar(

            @Valid
            @RequestBody
            RegistrarRecursoRequest request

    ) {

        RecursoDto dto =
                registrarRecursoPort.registrar(
                        new RegistrarRecursoCommand(
                                request.codigo(),
                                request.nombre(),
                                request.tipo(),
                                request.latitud(),
                                request.longitud()
                        )
                );

        return new RecursoResponse(
                dto.id(),
                dto.codigo(),
                dto.nombre(),
                dto.tipo(),
                dto.estado(),
                dto.latitud(),
                dto.longitud()
        );
    }

    @Operation(
            summary = "Buscar recurso más cercano",
            description = "Busca el recurso disponible más cercano para un incidente"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurso encontrado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existen recursos disponibles"
            )
    })

    @PostMapping("/buscar-mas-cercano")
    public RecursoCercanoResponse buscarMasCercano(

            @Valid
            @RequestBody
            BuscarRecursoMasCercanoRequest request

    ) {

        RecursoCercanoDto dto =
                buscarRecursoMasCercanoPort.buscar(
                        new BuscarRecursoMasCercanoCommand(
                                request.incidenteId(),
                                request.tipoRecurso(),
                                request.latitudIncidente(),
                                request.longitudIncidente(),
                                request.distanciaMaximaKm()
                        )
                );

        return new RecursoCercanoResponse(
                dto.recursoId(),
                dto.codigo(),
                dto.tipo(),
                dto.distanciaKm(),
                dto.tiempoEstimadoLlegadaMinutos(),
                dto.estado()
        );
    }

    @Operation(
            summary = "Reservar recurso",
            description = "Reserva un recurso disponible usando Idempotency-Key"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva creada correctamente"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El recurso no está disponible"
            )
    })

    @PostMapping("/{id}/reservar")
    public ReservaRecursoResponse reservar(

            @PathVariable UUID id,

            @RequestHeader("Idempotency-Key")
            String idempotencyKey,

            @Valid
            @RequestBody
            ReservarRecursoRequest request

    ) {

        var dto =
                reservarRecursoPort.reservar(
                        new ReservarRecursoCommand(
                                id,
                                request.incidenteId(),
                                idempotencyKey,
                                request.duracionReservaMinutos()
                        )
                );

        return new ReservaRecursoResponse(
                dto.reservaId(),
                dto.recursoId(),
                dto.incidenteId(),
                dto.estadoRecurso(),
                dto.fechaReserva(),
                dto.fechaVencimiento()
        );
    }

    @Operation(
            summary = "Despachar recurso",
            description = "Despacha un recurso previamente reservado"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurso despachado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Transición inválida"
            )
    })

    @PostMapping("/{id}/despachar")
    public DespachoRecursoResponse despachar(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            DespacharRecursoRequest request

    ) {

        var dto =
                despacharRecursoPort.despachar(
                        new DespacharRecursoCommand(
                                id,
                                request.incidenteId(),
                                request.responsable()
                        )
                );

        return new DespachoRecursoResponse(
                dto.recursoId(),
                dto.incidenteId(),
                dto.estado(),
                dto.fechaDespacho()
        );
    }

    @Operation(
            summary = "Liberar recurso",
            description = "Libera un recurso para dejarlo disponible nuevamente"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurso liberado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El recurso no puede liberarse"
            )
    })

    @PostMapping("/{id}/liberar")
    public LiberarRecursoResponse liberar(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            LiberarRecursoRequest request

    ) {

        var dto =
                liberarRecursoPort.liberar(
                        new LiberarRecursoCommand(
                                id,
                                request.incidenteId(),
                                request.motivo()
                        )
                );

        return new LiberarRecursoResponse(
                dto.recursoId(),
                dto.estado(),
                dto.fechaLiberacion()
        );
    }

    @Operation(
            summary = "Actualizar ubicación",
            description = "Actualiza la ubicación geográfica de un recurso"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ubicación actualizada"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso no encontrado"
            )
    })

    @PatchMapping("/{id}/ubicacion")
    public ActualizarUbicacionResponse actualizarUbicacion(

            @PathVariable UUID id,

            @Valid
            @RequestBody
            ActualizarUbicacionRequest request

    ) {

        var dto =
                actualizarUbicacionRecursoPort.actualizar(
                        new ActualizarUbicacionRecursoCommand(
                                id,
                                request.latitud(),
                                request.longitud()
                        )
                );

        return new ActualizarUbicacionResponse(
                dto.recursoId(),
                dto.latitud(),
                dto.longitud(),
                dto.fechaActualizacion()
        );
    }

    @Operation(
            summary = "Reporte de utilización",
            description = "Retorna indicadores de utilización de recursos"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Reporte generado"
    )


    @GetMapping("/reporte-utilizacion")
    public ReporteUtilizacionDto reporte() {

        return generarReporteUtilizacionPort
                .generar();
    }

    @Operation(
            summary = "Consultar recurso",
            description = "Consulta un recurso por identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recurso encontrado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso no encontrado"
            )
    })

    @GetMapping("/{id}")
    public RecursoResponse consultar(
            @PathVariable UUID id
    ) {

        RecursoDto dto =
                consultarRecursoPort.consultar(id);

        return new RecursoResponse(
                dto.id(),
                dto.codigo(),
                dto.nombre(),
                dto.tipo(),
                dto.estado(),
                dto.latitud(),
                dto.longitud()
        );
    }

    @Operation(
            summary = "Listar disponibles",
            description = "Lista recursos disponibles por tipo"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista obtenida"
    )

    @GetMapping("/disponibles")
    public List<RecursoResponse> disponibles(
            @RequestParam TipoRecurso tipo
    ) {

        return buscarRecursosDisponiblesPort
                .buscar(tipo)
                .stream()
                .map(dto ->
                        new RecursoResponse(
                                dto.id(),
                                dto.codigo(),
                                dto.nombre(),
                                dto.tipo(),
                                dto.estado(),
                                dto.latitud(),
                                dto.longitud()
                        )
                )
                .toList();
    }

    @Operation(
            summary = "Listar recursos",
            description = "Obtiene todos los recursos registrados"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista obtenida"
    )

    @GetMapping
    public List<RecursoResponse> listar() {

        return listarRecursosPort
                .listar()
                .stream()
                .map(dto ->
                        new RecursoResponse(
                                dto.id(),
                                dto.codigo(),
                                dto.nombre(),
                                dto.tipo(),
                                dto.estado(),
                                dto.latitud(),
                                dto.longitud()
                        )
                )
                .toList();
    }


}