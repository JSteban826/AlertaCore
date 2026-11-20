package com.alertacore.notificationservice.Application;
import com.alertacore.notificationservice.Domain.TelegramGateway;
import com.alertacore.notificationservice.Infrastructure.TelegramHttpGateway;
import com.alertacore.notificationservice.Domain.EmailGateway;
import com.alertacore.notificationservice.Domain.NotificacionGateway;
import com.alertacore.notificationservice.Infrastructure.EmailSmtpGateway;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegistrarDespachoUseCase registrarDespachoUseCase(
            NotificacionGateway notificacionGateway,
            ObjectProvider<EmailGateway> emailGateway,
            ObjectProvider<TelegramGateway> telegramGateway,
            @Value("${spring.mail.username:}") String destinatario
    ) {
        return new RegistrarDespachoUseCase(
                notificacionGateway,
                emailGateway.getIfAvailable(),
                destinatario,
                telegramGateway.getIfAvailable()
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