package com.alertacore.notificationservice.Domain;

public interface EmailGateway {
    void enviar(String destinatario, String asunto, String mensaje);
}