package com.alertacore.notificationservice.infrastructure.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramHttpGatewayTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpClient.Builder builder;

    @Mock
    private HttpResponse<Void> respuesta;

    private TelegramHttpGateway gateway;

    @BeforeEach
    void prepararGateway() {
        try (MockedStatic<HttpClient> httpClientStatic =
                     mockStatic(HttpClient.class)) {

            httpClientStatic.when(HttpClient::newBuilder)
                    .thenReturn(builder);

            when(builder.connectTimeout(Duration.ofSeconds(10)))
                    .thenReturn(builder);

            when(builder.build()).thenReturn(httpClient);

            gateway = new TelegramHttpGateway(
                    "token-de-prueba",
                    "123456"
            );
        }
    }

    @Test
    void debeEnviarSolicitudPostCuandoTelegramResponde200()
            throws IOException, InterruptedException {

        // Preparar
        when(httpClient.send(
                any(HttpRequest.class),
                org.mockito.ArgumentMatchers
                        .<HttpResponse.BodyHandler<Void>>any()
        )).thenReturn(respuesta);

        when(respuesta.statusCode()).thenReturn(200);

        // Ejecutar
        gateway.enviar("Aviso de despacho");

        // Comprobar la solicitud enviada
        ArgumentCaptor<HttpRequest> solicitudCaptor =
                ArgumentCaptor.forClass(HttpRequest.class);

        verify(httpClient).send(
                solicitudCaptor.capture(),
                org.mockito.ArgumentMatchers
                        .<HttpResponse.BodyHandler<Void>>any()
        );

        HttpRequest solicitud = solicitudCaptor.getValue();

        assertEquals("POST", solicitud.method());

        assertEquals(
                URI.create(
                        "https://api.telegram.org/"
                                + "bottoken-de-prueba/sendMessage"
                ),
                solicitud.uri()
        );

        assertEquals(
                "application/x-www-form-urlencoded",
                solicitud.headers()
                        .firstValue("Content-Type")
                        .orElseThrow()
        );

        assertEquals(
                Duration.ofSeconds(20),
                solicitud.timeout().orElseThrow()
        );

        assertTrue(solicitud.bodyPublisher().isPresent());
    }

    @Test
    void debeLanzarExcepcionCuandoTelegramRechazaElEnvio()
            throws IOException, InterruptedException {

        // Preparar
        when(httpClient.send(
                any(HttpRequest.class),
                org.mockito.ArgumentMatchers
                        .<HttpResponse.BodyHandler<Void>>any()
        )).thenReturn(respuesta);

        when(respuesta.statusCode()).thenReturn(400);

        // Ejecutar
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> gateway.enviar("Aviso de despacho")
        );

        // Comprobar
        assertEquals(
                "Telegram rechazo el envio. HTTP 400",
                excepcion.getMessage()
        );
    }

    @Test
    void debeLanzarExcepcionCuandoFallaLaConexion()
            throws IOException, InterruptedException {

        // Preparar
        when(httpClient.send(
                any(HttpRequest.class),
                org.mockito.ArgumentMatchers
                        .<HttpResponse.BodyHandler<Void>>any()
        )).thenThrow(new IOException("Fallo simulado"));

        // Ejecutar
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> gateway.enviar("Aviso de despacho")
        );

        // Comprobar
        assertEquals(
                "No fue posible conectar con Telegram.",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRestaurarLaInterrupcionCuandoElEnvioSeInterrumpe()
            throws IOException, InterruptedException {

        // Preparar
        assertFalse(Thread.currentThread().isInterrupted());

        when(httpClient.send(
                any(HttpRequest.class),
                org.mockito.ArgumentMatchers
                        .<HttpResponse.BodyHandler<Void>>any()
        )).thenThrow(new InterruptedException("Interrupcion simulada"));

        try {
            // Ejecutar
            IllegalStateException excepcion = assertThrows(
                    IllegalStateException.class,
                    () -> gateway.enviar("Aviso de despacho")
            );

            // Comprobar
            assertEquals(
                    "Se interrumpio el envio a Telegram.",
                    excepcion.getMessage()
            );

            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            // Limpiar la interrupción para no afectar otras pruebas
            Thread.interrupted();
        }
    }
}