package com.alertacore.notificationservice.Infrastructure;

import com.alertacore.notificationservice.Domain.EmailGateway;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailSmtpGateway implements EmailGateway {

    private final JavaMailSender mailSender;
    private final String remitente;

    public EmailSmtpGateway(JavaMailSender mailSender, String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    @Override
    public void enviar(String destinatario, String asunto, String mensaje) {
        var correo = new SimpleMailMessage();
        correo.setFrom(remitente);
        correo.setTo(destinatario);
        correo.setSubject(asunto);
        correo.setText(mensaje);

        mailSender.send(correo);
    }
}