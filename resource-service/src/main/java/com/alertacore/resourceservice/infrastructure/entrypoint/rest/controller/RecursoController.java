package com.alertacore.resourceservice.infrastructure.entrypoint.rest.controller;

import com.alertacore.resourceservice.application.command.BuscarRecursoMasCercanoCommand;
import com.alertacore.resourceservice.application.command.RegistrarRecursoCommand;
import com.alertacore.resourceservice.application.dto.RecursoCercanoDto;
import com.alertacore.resourceservice.application.dto.RecursoDto;
import com.alertacore.resourceservice.application.port.in.BuscarRecursoMasCercanoPort;
import com.alertacore.resourceservice.application.port.in.RegistrarRecursoPort;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.request.BuscarRecursoMasCercanoRequest;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.request.RegistrarRecursoRequest;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.RecursoCercanoResponse;
import com.alertacore.resourceservice.infrastructure.entrypoint.rest.response.RecursoResponse;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecursoResponse registrar(
            @RequestBody
            @Valid
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

    @PostMapping("/buscar-mas-cercano")
    public RecursoCercanoResponse buscarMasCercano(
            @RequestBody
            @Valid
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

    @PostMapping("/{recursoId}/reservar")
    public ReservaRecursoResponse reservar(
            @PathVariable UUID recursoId,

            @RequestHeader("Idempotency-Key")
            String idempotencyKey,

            @Valid
            @RequestBody
            ReservarRecursoRequest request
    ) {

        var dto =
                reservarRecursoPort.reservar(
                        new ReservarRecursoCommand(
                                recursoId,
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

    @PostMapping("/{recursoId}/despachar")
    public DespachoRecursoResponse despachar(
            @PathVariable UUID recursoId,

            @Valid
            @RequestBody
            DespacharRecursoRequest request
    ) {

        var dto =
                despacharRecursoPort.despachar(
                        new DespacharRecursoCommand(
                                recursoId,
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

    @PostMapping("/{recursoId}/liberar")
    public LiberarRecursoResponse liberar(
            @PathVariable UUID recursoId,

            @Valid
            @RequestBody
            LiberarRecursoRequest request
    ) {

        var dto =
                liberarRecursoPort.liberar(
                        new LiberarRecursoCommand(
                                recursoId,
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

    @PatchMapping("/{recursoId}/ubicacion")
    public ActualizarUbicacionResponse actualizarUbicacion(
            @PathVariable UUID recursoId,

            @Valid
            @RequestBody
            ActualizarUbicacionRequest request
    ) {

        var dto =
                actualizarUbicacionRecursoPort.actualizar(
                        new ActualizarUbicacionRecursoCommand(
                                recursoId,
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

    @GetMapping("/reporte-utilizacion")
    public ReporteUtilizacionDto reporte() {

        return generarReporteUtilizacionPort
                .generar();
    }
}