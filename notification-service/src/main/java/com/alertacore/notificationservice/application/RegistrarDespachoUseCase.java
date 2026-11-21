package com.alertacore.notificationservice.application;

import com.alertacore.notificationservice.domain.model.gateway.ConsultaDespachoGateway;
import com.alertacore.notificationservice.domain.model.gateway.EmailGateway;
import com.alertacore.notificationservice.domain.model.gateway.NotificacionGateway;
import com.alertacore.notificationservice.domain.model.gateway.TelegramGateway;

import java.util.UUID;

public class RegistrarDespachoUseCase {

    private final NotificacionGateway notificacionGateway;
    private final EmailGateway emailGateway;
    private final String destinatario;
    private final TelegramGateway telegramGateway;
    private final ConsultaDespachoGateway consultaDespachoGateway;

    public RegistrarDespachoUseCase(
            NotificacionGateway notificacionGateway,
            EmailGateway emailGateway,
            String destinatario,
            TelegramGateway telegramGateway,
            ConsultaDespachoGateway consultaDespachoGateway
    ) {
        this.notificacionGateway = notificacionGateway;
        this.emailGateway = emailGateway;
        this.destinatario = destinatario;
        this.telegramGateway = telegramGateway;
        this.consultaDespachoGateway = consultaDespachoGateway;
    }

    public AvisoRecibido ejecutar(UUID incidenteId, UUID recursoId) {
        var incidente =
                consultaDespachoGateway.consultarIncidente(incidenteId);

        var recurso =
                consultaDespachoGateway.consultarRecurso(recursoId);

        String mensaje = """
                AlertaCore - Aviso de despacho

                DETALLES DEL INCIDENTE
                Tipo: %s
                Descripción: %s
                Dirección: %s
                Estado: %s
                Nivel de riesgo: %s
                Personas afectadas: %s
                ¿Hay personas heridas?: %s
                Coordenadas: %s, %s
                Fecha de reporte: %s

                DETALLES DEL RECURSO
                Código: %s
                Nombre: %s
                Tipo: %s
                Estado: %s
                Coordenadas: %s, %s

                ID del incidente: %s
                ID del recurso: %s
                """.formatted(
                texto(incidente.tipo()),
                texto(incidente.descripcion()),
                texto(incidente.direccion()),
                texto(incidente.estado()),
                texto(incidente.nivelRiesgo()),
                incidente.cantidadPersonasAfectadas(),
                incidente.hayPersonasHeridas() ? "Sí" : "No",
                incidente.latitud(),
                incidente.longitud(),
                texto(incidente.fechaReporte()),
                texto(recurso.codigo()),
                texto(recurso.nombre()),
                texto(recurso.tipo()),
                texto(recurso.estado()),
                recurso.latitud(),
                recurso.longitud(),
                incidenteId,
                recursoId
        );

        notificacionGateway.guardarDespacho(incidenteId, recursoId);

        if (emailGateway != null
                && destinatario != null
                && !destinatario.isBlank()) {
            emailGateway.enviar(
                    destinatario,
                    "AlertaCore - Aviso de despacho",
                    mensaje
            );
        }

        if (telegramGateway != null) {
            telegramGateway.enviar(mensaje);
        }

        return new AvisoRecibido(
                incidenteId,
                recursoId,
                "RECIBIDO"
        );
    }

    private static String texto(String valor) {
        return valor == null || valor.isBlank()
                ? "Sin información"
                : valor.strip();
    }

    public record AvisoRecibido(
            UUID incidenteId,
            UUID recursoId,
            String estado
    ) {}
}