package com.alertacore.notificationservice.Domain;

public interface SmsGateway {
    void enviar(String numeroDestino, String mensaje);
}