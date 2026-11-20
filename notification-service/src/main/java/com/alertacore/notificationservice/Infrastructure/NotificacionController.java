package com.alertacore.notificationservice.Infrastructure;

import com.alertacore.notificationservice.Application.RegistrarDespachoUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class NotificacionController {

    private final RegistrarDespachoUseCase registrarDespacho;

    public NotificacionController(RegistrarDespachoUseCase registrarDespacho) {
        this.registrarDespacho = registrarDespacho;
    }

    @PostMapping("/api/v1/notificaciones/despachos")
    public RegistrarDespachoUseCase.AvisoRecibido recibir(
            @Valid @RequestBody DespachoRequest request
    ) {
        return registrarDespacho.ejecutar(request.incidenteId(), request.recursoId());
    }

    public record DespachoRequest(
            @NotNull UUID incidenteId,
            @NotNull UUID recursoId
    ) {}
}