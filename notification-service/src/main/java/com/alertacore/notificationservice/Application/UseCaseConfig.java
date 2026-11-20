package com.alertacore.notificationservice.Application;

import com.alertacore.notificationservice.Domain.NotificacionGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegistrarDespachoUseCase registrarDespachoUseCase(
            NotificacionGateway notificacionGateway
    ) {
        return new RegistrarDespachoUseCase(notificacionGateway);
    }
}