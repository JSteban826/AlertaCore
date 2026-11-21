package com.alertacore.resourceservice.infrastructure.client.notificaciones;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacionDespachoHttpGatewayTest {

    private HttpServer servidor;
    private String baseUrl;

    private final AtomicReference<String> metodoRecibido =
            new AtomicReference<>();

    private final AtomicReference<String> rutaRecibida =
            new AtomicReference<>();

    private final AtomicReference<String> contentTypeRecibido =
            new AtomicReference<>();

    private final AtomicReference<String> cuerpoRecibido =
            new AtomicReference<>();

    @BeforeEach
    void setUp() throws IOException {
        servidor = HttpServer.create(
                new InetSocketAddress(0),
                0
        );

        servidor.createContext(
                "/api/v1/notificaciones/despachos",
                this::procesarSolicitud
        );

        servidor.start();

        int puerto = servidor.getAddress().getPort();
        baseUrl = "http://localhost:" + puerto;
    }

    @AfterEach
    void tearDown() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    void deberiaNotificarElDespachoPorHttp() {
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        NotificacionDespachoHttpGateway gateway =
                new NotificacionDespachoHttpGateway(baseUrl);

        gateway.notificarDespacho(
                incidenteId,
                recursoId
        );

        String cuerpo = cuerpoRecibido.get();

        assertAll(
                () -> assertEquals(
                        "POST",
                        metodoRecibido.get()
                ),
                () -> assertEquals(
                        "/api/v1/notificaciones/despachos",
                        rutaRecibida.get()
                ),
                () -> assertTrue(
                        contentTypeRecibido.get()
                                .startsWith("application/json")
                ),
                () -> assertTrue(
                        cuerpo.contains(
                                incidenteId.toString()
                        )
                ),
                () -> assertTrue(
                        cuerpo.contains(
                                recursoId.toString()
                        )
                ),
                () -> assertTrue(
                        cuerpo.contains("\"incidenteId\"")
                ),
                () -> assertTrue(
                        cuerpo.contains("\"recursoId\"")
                )
        );
    }

    private void procesarSolicitud(
            HttpExchange exchange
    ) throws IOException {
        metodoRecibido.set(
                exchange.getRequestMethod()
        );

        rutaRecibida.set(
                exchange.getRequestURI().getPath()
        );

        contentTypeRecibido.set(
                exchange.getRequestHeaders()
                        .getFirst("Content-Type")
        );

        String cuerpo = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        cuerpoRecibido.set(cuerpo);

        exchange.sendResponseHeaders(
                204,
                -1
        );

        exchange.close();
    }
}