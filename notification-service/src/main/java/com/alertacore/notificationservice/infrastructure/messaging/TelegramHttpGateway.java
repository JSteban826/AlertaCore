package com.alertacore.notificationservice.infrastructure.messaging;

import com.alertacore.notificationservice.domain.model.gateway.TelegramGateway;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class TelegramHttpGateway implements TelegramGateway {

    private final HttpClient httpClient;
    private final String token;
    private final String chatId;

    public TelegramHttpGateway(String token, String chatId) {
        this.token = token;
        this.chatId = chatId;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public void enviar(String mensaje) {
        String cuerpo = "chat_id=" + codificar(chatId)
                + "&text=" + codificar(mensaje);

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://api.telegram.org/bot" + token + "/sendMessage"
                ))
                .timeout(Duration.ofSeconds(20))
                .header(
                        "Content-Type",
                        "application/x-www-form-urlencoded"
                )
                .POST(HttpRequest.BodyPublishers.ofString(
                        cuerpo,
                        StandardCharsets.UTF_8
                ))
                .build();

        try {
            HttpResponse<Void> respuesta = httpClient.send(
                    solicitud,
                    HttpResponse.BodyHandlers.discarding()
            );

            if (respuesta.statusCode() != 200) {
                throw new IllegalStateException(
                        "Telegram rechazo el envio. HTTP "
                                + respuesta.statusCode()
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Se interrumpio el envio a Telegram."
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No fue posible conectar con Telegram."
            );
        }
    }

    private static String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }
}