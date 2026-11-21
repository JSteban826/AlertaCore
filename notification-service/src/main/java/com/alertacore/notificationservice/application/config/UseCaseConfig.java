package com.alertacore.notificationservice.application.config;
import com.alertacore.notificationservice.application.RegistrarDespachoUseCase;
import com.alertacore.notificationservice.domain.TelegramGateway;
import com.alertacore.notificationservice.infrastructure.messaging.TelegramHttpGateway;
import com.alertacore.notificationservice.domain.EmailGateway;
import com.alertacore.notificationservice.domain.NotificacionGateway;
import com.alertacore.notificationservice.infrastructure.messaging.EmailSmtpGateway;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import com.alertacore.notificationservice.domain.ConsultaDespachoGateway;
import com.alertacore.notificationservice.infrastructure.client.ConsultaDespachoHttpGateway;
@Configuration
public class UseCaseConfig {
    @Bean
    public ConsultaDespachoGateway consultaDespachoGateway(
            @Value("${notificaciones.incidentes.base-url:http://localhost:8081}")
            String incidentBaseUrl,
            @Value("${notificaciones.recursos.base-url:http://localhost:8082}")
            String resourceBaseUrl
    ) {
        return new ConsultaDespachoHttpGateway(
                incidentBaseUrl,
                resourceBaseUrl
        );
    }
    @Bean
    public RegistrarDespachoUseCase registrarDespachoUseCase(
            NotificacionGateway notificacionGateway,
            ObjectProvider<EmailGateway> emailGateway,
            ObjectProvider<TelegramGateway> telegramGateway,
            ConsultaDespachoGateway consultaDespachoGateway,
            @Value("${spring.mail.username:}") String destinatario
    ) {
        return new RegistrarDespachoUseCase(
                notificacionGateway,
                emailGateway.getIfAvailable(),
                destinatario,
                telegramGateway.getIfAvailable(),
                consultaDespachoGateway
        );
    }

    @Bean
    @ConditionalOnProperty(name = "spring.mail.host")
    public EmailGateway emailGateway(
            JavaMailSender mailSender,
            @Value("${notificaciones.email.remitente}") String remitente
    ) {
        return new EmailSmtpGateway(mailSender, remitente);
    }
    @Bean
    @ConditionalOnProperty(
            name = "notificaciones.telegram.habilitado",
            havingValue = "true"
    )
    public TelegramGateway telegramGateway(
            @Value("${notificaciones.telegram.token:}") String token,
            @Value("${notificaciones.telegram.chat-id:}") String chatId
    ) {
        if (token.isBlank() || chatId.isBlank()) {
            throw new IllegalStateException(
                    "Telegram esta habilitado, pero falta configurar el token o el chat-id."
            );
        }

        return new TelegramHttpGateway(token.trim(), chatId.trim());
    }
}