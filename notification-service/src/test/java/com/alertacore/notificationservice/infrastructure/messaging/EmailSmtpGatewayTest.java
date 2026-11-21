package com.alertacore.notificationservice.infrastructure.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class EmailSmtpGatewayTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void debeEnviarCorreoConLosDatosCorrectos() {
        // Preparar
        String remitente = "notificaciones@example.com";
        String destinatario = "operador@example.com";
        String asunto = "AlertaCore - Aviso de despacho";
        String mensaje = "Se ha despachado el recurso BOM-001.";

        EmailSmtpGateway gateway =
                new EmailSmtpGateway(mailSender, remitente);

        // Ejecutar
        gateway.enviar(destinatario, asunto, mensaje);

        // Capturar el correo entregado al mailSender
        ArgumentCaptor<SimpleMailMessage> correoCaptor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(correoCaptor.capture());

        SimpleMailMessage correo = correoCaptor.getValue();

        // Comprobar los datos del correo
        assertEquals(remitente, correo.getFrom());

        assertArrayEquals(
                new String[]{destinatario},
                correo.getTo()
        );

        assertEquals(asunto, correo.getSubject());
        assertEquals(mensaje, correo.getText());

        // Comprobar que no hubo otras llamadas al mailSender
        verifyNoMoreInteractions(mailSender);
    }
}