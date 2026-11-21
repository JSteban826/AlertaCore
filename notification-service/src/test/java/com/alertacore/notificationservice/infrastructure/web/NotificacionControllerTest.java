package com.alertacore.notificationservice.infrastructure.web;

import com.alertacore.notificationservice.application.RegistrarDespachoUseCase;
import com.alertacore.notificationservice.application.RegistrarDespachoUseCase.AvisoRecibido;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock
    private RegistrarDespachoUseCase registrarDespacho;

    @Test
    void debePasarLosIdentificadoresAlCasoDeUsoYDevolverSuRespuesta() {
        // Preparar
        UUID incidenteId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();

        NotificacionController.DespachoRequest request =
                new NotificacionController.DespachoRequest(
                        incidenteId,
                        recursoId
                );

        AvisoRecibido respuestaEsperada = new AvisoRecibido(
                incidenteId,
                recursoId,
                "RECIBIDO"
        );

        when(registrarDespacho.ejecutar(incidenteId, recursoId))
                .thenReturn(respuestaEsperada);

        NotificacionController controller =
                new NotificacionController(registrarDespacho);

        // Ejecutar
        AvisoRecibido resultado = controller.recibir(request);

        // Comprobar
        assertSame(respuestaEsperada, resultado);

        verify(registrarDespacho)
                .ejecutar(incidenteId, recursoId);

        verifyNoMoreInteractions(registrarDespacho);
    }
}