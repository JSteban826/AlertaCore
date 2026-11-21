package com.alertacore.notificationservice.domain.model.gateway;

public interface EmailGateway {
    void enviar(String destinatario, String asunto, String mensaje);
}