package com.alertacore.notificationservice.Application;

import com.alertacore.notificationservice.Domain.EmailGateway;
import com.alertacore.notificationservice.Domain.NotificacionGateway;
import com.alertacore.notificationservice.Domain.TelegramGateway;

import java.util.UUID;

public class RegistrarDespachoUseCase {

    private final NotificacionGateway notificacionGateway;
    private final EmailGateway emailGateway;
    private final String destinatario;
    private final TelegramGateway telegramGateway;

    public RegistrarDespachoUseCase(
            NotificacionGateway notificacionGateway,
            EmailGateway emailGateway,
            String destinatario,
            TelegramGateway telegramGateway
    ) {
        this.notificacionGateway = notificacionGateway;
        this.emailGateway = emailGateway;
        this.destinatario = destinatario;
        this.telegramGateway = telegramGateway;
    }

    public AvisoRecibido ejecutar(UUID incidenteId, UUID recursoId) {
        notificacionGateway.guardarDespacho(incidenteId, recursoId);

        if (emailGateway != null && !destinatario.isBlank()) {
            emailGateway.enviar(
                    destinatario,
                    "AlertaCore - Aviso de despacho",
                    """
                    Se registró un despacho.

                    Incidente: %s
                    Recurso: %s
                    """.formatted(incidenteId, recursoId)
            );
        }

        if (telegramGateway != null) {
            telegramGateway.enviar(
                    """
                    AlertaCore - Aviso de despacho

                    Se registró un despacho.

                    Incidente: %s
                    Recurso: %s
                    """.formatted(incidenteId, recursoId)
            );
        }

        return new AvisoRecibido(incidenteId, recursoId, "RECIBIDO");
    }

    public record AvisoRecibido(
            UUID incidenteId,
            UUID recursoId,
            String estado
    ) {}
}