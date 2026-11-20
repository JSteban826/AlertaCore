package com.alertacore.notificationservice.Domain;

public interface TelegramGateway {

    void enviar(String mensaje);
}