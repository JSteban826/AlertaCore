package com.alertacore.notificationservice.domain;

public interface EmailGateway {
    void enviar(String destinatario, String asunto, String mensaje);
}