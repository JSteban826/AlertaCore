package com.alertacore.notificationservice.application;
import static org.mockito.Mockito.verifyNoInteractions;
import com.alertacore.notificationservice.domain.model.gateway.ConsultaDespachoGateway;
import com.alertacore.notificationservice.domain.model.gateway.ConsultaDespachoGateway.IncidenteDetalle;
import com.alertacore.notificationservice.domain.model.gateway.ConsultaDespachoGateway.RecursoDetalle;
import com.alertacore.notificationservice.domain.model.gateway.EmailGateway;
import com.alertacore.notificationservice.domain.model.gateway.NotificacionGateway;
import com.alertacore.notificationservice.domain.model.gateway.TelegramGateway;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.doThrow;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarDespachoUseCaseTest {

    @Mock
    private NotificacionGateway notificacionGateway;

    @Mock
    private EmailGateway emailGateway;

    @Mock
    private TelegramGateway telegramGateway;

    @Mock
    private ConsultaDespachoGateway consultaDespachoGateway;

    @Test
    void debeGuardarDespachoYEnviarAvisoPorCorreoYTelegram() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        String destinatario = "operador@example.com";

        IncidenteDetalle incidente = crearIncidente(incidenteId);
        RecursoDetalle recurso = crearRecurso(recursoId);

        when(consultaDespachoGateway.consultarIncidente(incidenteId))
                .thenReturn(incidente);

        when(consultaDespachoGateway.consultarRecurso(recursoId))
                .thenReturn(recurso);

        RegistrarDespachoUseCase casoDeUso =
                new RegistrarDespachoUseCase(
                        notificacionGateway,
                        emailGateway,
                        destinatario,
                        telegramGateway,
                        consultaDespachoGateway
                );

        // Ejecutar
        RegistrarDespachoUseCase.AvisoRecibido resultado =
                casoDeUso.ejecutar(incidenteId, recursoId);

        // Comprobar la respuesta
        assertEquals(incidenteId, resultado.incidenteId());
        assertEquals(recursoId, resultado.recursoId());
        assertEquals("RECIBIDO", resultado.estado());

        // Comprobar las consultas y el guardado
        verify(consultaDespachoGateway)
                .consultarIncidente(incidenteId);

        verify(consultaDespachoGateway)
                .consultarRecurso(recursoId);

        verify(notificacionGateway)
                .guardarDespacho(incidenteId, recursoId);

        // Capturar el mensaje enviado por correo
        ArgumentCaptor<String> mensajeCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(emailGateway).enviar(
                eq(destinatario),
                eq("AlertaCore - Aviso de despacho"),
                mensajeCaptor.capture()
        );

        String mensaje = mensajeCaptor.getValue();

        // Comprobar algunos datos importantes del aviso
        assertTrue(mensaje.contains("Tipo: INCENDIO"));
        assertTrue(mensaje.contains(
                "Descripción: Incendio en una vivienda"
        ));
        assertTrue(mensaje.contains(
                "Dirección: Calle 10 # 20-30"
        ));
        assertTrue(mensaje.contains("Personas afectadas: 3"));
        assertTrue(mensaje.contains("¿Hay personas heridas?: Sí"));
        assertTrue(mensaje.contains("Código: BOM-001"));
        assertTrue(mensaje.contains(
                "Nombre: Unidad de bomberos 1"
        ));
        assertTrue(mensaje.contains(
                "ID del incidente: " + incidenteId
        ));
        assertTrue(mensaje.contains(
                "ID del recurso: " + recursoId
        ));

        // Comprobar que Telegram recibe el mismo mensaje
        verify(telegramGateway).enviar(mensaje);
    }
    @Test
    void noDebeEnviarCorreoCuandoElDestinatarioEstaEnBlanco() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        IncidenteDetalle incidente = new IncidenteDetalle(
                incidenteId,
                "INCENDIO",
                "Incendio en una vivienda",
                "REPORTADO",
                "ALTO",
                3,
                true,
                4.6097,
                -74.0817,
                "Calle 10 # 20-30",
                "2026-11-21T12:00:00",
                "2026-11-21T12:05:00"
        );

        RecursoDetalle recurso = new RecursoDetalle(
                recursoId,
                "BOM-001",
                "Unidad de bomberos 1",
                "BOMBEROS",
                "DESPACHADO",
                4.6100,
                -74.0800
        );

        when(consultaDespachoGateway.consultarIncidente(incidenteId))
                .thenReturn(incidente);

        when(consultaDespachoGateway.consultarRecurso(recursoId))
                .thenReturn(recurso);

        RegistrarDespachoUseCase casoDeUso =
                new RegistrarDespachoUseCase(
                        notificacionGateway,
                        emailGateway,
                        "   ",
                        telegramGateway,
                        consultaDespachoGateway
                );

        // Ejecutar
        RegistrarDespachoUseCase.AvisoRecibido resultado =
                casoDeUso.ejecutar(incidenteId, recursoId);

        // Comprobar
        assertEquals(incidenteId, resultado.incidenteId());
        assertEquals(recursoId, resultado.recursoId());
        assertEquals("RECIBIDO", resultado.estado());

        verify(notificacionGateway)
                .guardarDespacho(incidenteId, recursoId);

        verifyNoInteractions(emailGateway);

        ArgumentCaptor<String> mensajeCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(telegramGateway).enviar(mensajeCaptor.capture());

        assertTrue(
                mensajeCaptor.getValue().contains("Código: BOM-001")
        );
    }
    @Test
    void noDebeGuardarNiEnviarAvisosCuandoFallaLaConsultaDelIncidente() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        IllegalStateException falloConsulta =
                new IllegalStateException(
                        "No se pudo consultar el incidente"
                );

        when(consultaDespachoGateway.consultarIncidente(incidenteId))
                .thenThrow(falloConsulta);

        RegistrarDespachoUseCase casoDeUso =
                new RegistrarDespachoUseCase(
                        notificacionGateway,
                        emailGateway,
                        "operador@example.com",
                        telegramGateway,
                        consultaDespachoGateway
                );

        // Ejecutar y comprobar la excepción
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> casoDeUso.ejecutar(incidenteId, recursoId)
        );

        assertSame(falloConsulta, excepcion);

        // Comprobar que no hubo guardado ni envíos
        verifyNoInteractions(
                notificacionGateway,
                emailGateway,
                telegramGateway
        );

        // Comprobar que no se consultó el recurso
        verify(consultaDespachoGateway, org.mockito.Mockito.never())
                .consultarRecurso(recursoId);
    }
    @Test
    void noDebeEnviarAvisosCuandoFallaElGuardadoDelDespacho() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        IncidenteDetalle incidente = crearIncidente(incidenteId);
        RecursoDetalle recurso = crearRecurso(recursoId);

        when(consultaDespachoGateway.consultarIncidente(incidenteId))
                .thenReturn(incidente);

        when(consultaDespachoGateway.consultarRecurso(recursoId))
                .thenReturn(recurso);

        IllegalStateException falloGuardado =
                new IllegalStateException(
                        "No se pudo guardar el despacho"
                );

        doThrow(falloGuardado)
                .when(notificacionGateway)
                .guardarDespacho(incidenteId, recursoId);

        RegistrarDespachoUseCase casoDeUso =
                new RegistrarDespachoUseCase(
                        notificacionGateway,
                        emailGateway,
                        "operador@example.com",
                        telegramGateway,
                        consultaDespachoGateway
                );

        // Ejecutar y comprobar la excepción
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> casoDeUso.ejecutar(incidenteId, recursoId)
        );

        assertSame(falloGuardado, excepcion);

        // Comprobar las consultas y el intento de guardado
        verify(consultaDespachoGateway)
                .consultarIncidente(incidenteId);

        verify(consultaDespachoGateway)
                .consultarRecurso(recursoId);

        verify(notificacionGateway)
                .guardarDespacho(incidenteId, recursoId);

        // Comprobar que no se enviaron avisos
        verifyNoInteractions(
                emailGateway,
                telegramGateway
        );
    }
    @Test
    void debeReemplazarTextosVaciosYQuitarEspaciosEnElAviso() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        IncidenteDetalle incidente = new IncidenteDetalle(
                incidenteId,
                null,
                "   ",
                "  REPORTADO  ",
                "",
                0,
                false,
                4.6097,
                -74.0817,
                "  Calle 10 # 20-30  ",
                null,
                null
        );

        RecursoDetalle recurso = new RecursoDetalle(
                recursoId,
                "  BOM-001  ",
                null,
                "  BOMBEROS  ",
                "   ",
                4.6100,
                -74.0800
        );

        when(consultaDespachoGateway.consultarIncidente(incidenteId))
                .thenReturn(incidente);

        when(consultaDespachoGateway.consultarRecurso(recursoId))
                .thenReturn(recurso);

        RegistrarDespachoUseCase casoDeUso =
                new RegistrarDespachoUseCase(
                        notificacionGateway,
                        emailGateway,
                        "operador@example.com",
                        telegramGateway,
                        consultaDespachoGateway
                );

        // Ejecutar
        casoDeUso.ejecutar(incidenteId, recursoId);

        // Capturar el mensaje generado
        ArgumentCaptor<String> mensajeCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(emailGateway).enviar(
                eq("operador@example.com"),
                eq("AlertaCore - Aviso de despacho"),
                mensajeCaptor.capture()
        );

        String mensaje = mensajeCaptor.getValue();

        // Comprobar los datos del incidente
        assertTrue(mensaje.contains(
                "Tipo: Sin información\n"
        ));

        assertTrue(mensaje.contains(
                "Descripción: Sin información\n"
        ));

        assertTrue(mensaje.contains(
                "Estado: REPORTADO\n"
        ));

        assertTrue(mensaje.contains(
                "Nivel de riesgo: Sin información\n"
        ));

        assertTrue(mensaje.contains(
                "Dirección: Calle 10 # 20-30\n"
        ));

        assertTrue(mensaje.contains(
                "Fecha de reporte: Sin información\n"
        ));

        assertTrue(mensaje.contains(
                "¿Hay personas heridas?: No\n"
        ));

        // Comprobar los datos del recurso
        assertTrue(mensaje.contains(
                "Código: BOM-001\n"
        ));

        assertTrue(mensaje.contains(
                "Nombre: Sin información\n"
        ));

        assertTrue(mensaje.contains(
                "Tipo: BOMBEROS\n"
        ));

        assertTrue(mensaje.contains(
                "Estado: Sin información\n"
        ));

        // Comprobar el guardado y el mismo mensaje en Telegram
        verify(notificacionGateway)
                .guardarDespacho(incidenteId, recursoId);

        verify(telegramGateway).enviar(mensaje);
    }
    @Test
    void debeGuardarYEnviarCorreoCuandoTelegramNoEstaConfigurado() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        IncidenteDetalle incidente = crearIncidente(incidenteId);
        RecursoDetalle recurso = crearRecurso(recursoId);

        when(consultaDespachoGateway.consultarIncidente(incidenteId))
                .thenReturn(incidente);

        when(consultaDespachoGateway.consultarRecurso(recursoId))
                .thenReturn(recurso);

        RegistrarDespachoUseCase casoDeUso =
                new RegistrarDespachoUseCase(
                        notificacionGateway,
                        emailGateway,
                        "operador@example.com",
                        null,
                        consultaDespachoGateway
                );

        // Ejecutar
        RegistrarDespachoUseCase.AvisoRecibido resultado =
                casoDeUso.ejecutar(incidenteId, recursoId);

        // Comprobar la respuesta
        assertEquals(incidenteId, resultado.incidenteId());
        assertEquals(recursoId, resultado.recursoId());
        assertEquals("RECIBIDO", resultado.estado());

        // Comprobar el guardado
        verify(notificacionGateway)
                .guardarDespacho(incidenteId, recursoId);

        // Comprobar el correo y su contenido
        ArgumentCaptor<String> mensajeCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(emailGateway).enviar(
                eq("operador@example.com"),
                eq("AlertaCore - Aviso de despacho"),
                mensajeCaptor.capture()
        );

        assertTrue(
                mensajeCaptor.getValue().contains("Código: BOM-001")
        );
    }
    @Test
    void debeGuardarYEnviarTelegramCuandoEmailNoEstaConfigurado() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        IncidenteDetalle incidente = new IncidenteDetalle(
                incidenteId,
                "INCENDIO",
                "Incendio en una vivienda",
                "REPORTADO",
                "ALTO",
                3,
                true,
                4.6097,
                -74.0817,
                "Calle 10 # 20-30",
                "2026-11-21T12:00:00",
                "2026-11-21T12:05:00"
        );

        RecursoDetalle recurso = new RecursoDetalle(
                recursoId,
                "BOM-001",
                "Unidad de bomberos 1",
                "BOMBEROS",
                "DESPACHADO",
                4.6100,
                -74.0800
        );

        when(consultaDespachoGateway.consultarIncidente(incidenteId))
                .thenReturn(incidente);

        when(consultaDespachoGateway.consultarRecurso(recursoId))
                .thenReturn(recurso);

        RegistrarDespachoUseCase casoDeUso =
                new RegistrarDespachoUseCase(
                        notificacionGateway,
                        null,
                        "operador@example.com",
                        telegramGateway,
                        consultaDespachoGateway
                );

        // Ejecutar
        RegistrarDespachoUseCase.AvisoRecibido resultado =
                casoDeUso.ejecutar(incidenteId, recursoId);

        // Comprobar la respuesta
        assertEquals(incidenteId, resultado.incidenteId());
        assertEquals(recursoId, resultado.recursoId());
        assertEquals("RECIBIDO", resultado.estado());

        // Comprobar el guardado
        verify(notificacionGateway)
                .guardarDespacho(incidenteId, recursoId);

        // Comprobar el mensaje enviado a Telegram
        ArgumentCaptor<String> mensajeCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(telegramGateway).enviar(mensajeCaptor.capture());

        String mensaje = mensajeCaptor.getValue();

        assertTrue(mensaje.contains("Código: BOM-001"));
        assertTrue(mensaje.contains(
                "ID del incidente: " + incidenteId
        ));
        assertTrue(mensaje.contains(
                "ID del recurso: " + recursoId
        ));
    }
    @Test
    void noDebeGuardarNiEnviarAvisosCuandoFallaLaConsultaDelRecurso() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        IncidenteDetalle incidente = new IncidenteDetalle(
                incidenteId,
                "INCENDIO",
                "Incendio en una vivienda",
                "REPORTADO",
                "ALTO",
                3,
                true,
                4.6097,
                -74.0817,
                "Calle 10 # 20-30",
                "2026-11-21T12:00:00",
                "2026-11-21T12:05:00"
        );

        when(consultaDespachoGateway.consultarIncidente(incidenteId))
                .thenReturn(incidente);

        IllegalStateException falloConsulta =
                new IllegalStateException(
                        "No se pudo consultar el recurso"
                );

        when(consultaDespachoGateway.consultarRecurso(recursoId))
                .thenThrow(falloConsulta);

        RegistrarDespachoUseCase casoDeUso =
                new RegistrarDespachoUseCase(
                        notificacionGateway,
                        emailGateway,
                        "operador@example.com",
                        telegramGateway,
                        consultaDespachoGateway
                );

        // Ejecutar y comprobar la excepción
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> casoDeUso.ejecutar(incidenteId, recursoId)
        );

        assertSame(falloConsulta, excepcion);

        // Comprobar que se hicieron ambas consultas
        verify(consultaDespachoGateway)
                .consultarIncidente(incidenteId);

        verify(consultaDespachoGateway)
                .consultarRecurso(recursoId);

        // Comprobar que no hubo guardado ni envíos
        verifyNoInteractions(
                notificacionGateway,
                emailGateway,
                telegramGateway
        );
    }
    private IncidenteDetalle crearIncidente(UUID incidenteId) {
        return new IncidenteDetalle(
                incidenteId,
                "INCENDIO",
                "Incendio en una vivienda",
                "REPORTADO",
                "ALTO",
                3,
                true,
                4.6097,
                -74.0817,
                "Calle 10 # 20-30",
                "2026-11-21T12:00:00",
                "2026-11-21T12:05:00"
        );
    }

    private RecursoDetalle crearRecurso(UUID recursoId) {
        return new RecursoDetalle(
                recursoId,
                "BOM-001",
                "Unidad de bomberos 1",
                "BOMBEROS",
                "DESPACHADO",
                4.6100,
                -74.0800
        );
    }
}