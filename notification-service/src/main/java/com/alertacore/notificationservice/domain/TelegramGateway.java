package com.alertacore.notificationservice.domain;

public interface TelegramGateway {

    void enviar(String mensaje);
}