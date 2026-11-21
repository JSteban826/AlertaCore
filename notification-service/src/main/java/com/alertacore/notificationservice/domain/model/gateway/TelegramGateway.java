package com.alertacore.notificationservice.domain.model.gateway;

public interface TelegramGateway {

    void enviar(String mensaje);
}