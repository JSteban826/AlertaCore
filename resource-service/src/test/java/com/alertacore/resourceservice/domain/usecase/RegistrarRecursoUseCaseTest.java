package com.alertacore.resourceservice.domain.usecase;

import com.alertacore.resourceservice.domain.model.command.RegistrarRecursoCommand;
import com.alertacore.resourceservice.domain.model.dto.RecursoDto;
import com.alertacore.resourceservice.domain.enums.EstadoRecurso;
import com.alertacore.resourceservice.domain.enums.TipoRecurso;
import com.alertacore.resourceservice.domain.exception.CodigoRecursoDuplicadoException;
import com.alertacore.resourceservice.domain.model.Recurso;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarRecursoUseCaseTest {

    @Mock
    private RecursoGateway recursoGateway;

    private RegistrarRecursoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarRecursoUseCase(
                recursoGateway
        );
    }

    @Test
    void deberiaRegistrarUnRecursoDisponible() {
        RegistrarRecursoCommand command =
                crearCommand("AMB-001");

        when(
                recursoGateway.existePorCodigo("AMB-001")
        ).thenReturn(false);

        when(
                recursoGateway.guardar(any(Recurso.class))
        ).thenAnswer(invocacion -> invocacion.getArgument(0));

        RecursoDto resultado = useCase.registrar(command);

        assertNotNull(resultado.id());
        assertEquals("AMB-001", resultado.codigo());
        assertEquals(
                "Ambulancia principal",
                resultado.nombre()
        );
        assertEquals(
                TipoRecurso.AMBULANCIA,
                resultado.tipo()
        );
        assertEquals(
                EstadoRecurso.DISPONIBLE,
                resultado.estado()
        );
        assertEquals(4.6579, resultado.latitud());
        assertEquals(-74.0596, resultado.longitud());
        assertNotNull(resultado.fechaDisponibleDesde());
        assertNotNull(
                resultado.fechaUltimaActualizacion()
        );

        verify(recursoGateway)
                .existePorCodigo("AMB-001");

        verify(recursoGateway)
                .guardar(any(Recurso.class));
    }

    @Test
    void deberiaNormalizarElCodigoAMayusculas() {
        RegistrarRecursoCommand command =
                crearCommand("amb-001");

        when(
                recursoGateway.existePorCodigo("AMB-001")
        ).thenReturn(false);

        when(
                recursoGateway.guardar(any(Recurso.class))
        ).thenAnswer(invocacion -> invocacion.getArgument(0));

        RecursoDto resultado = useCase.registrar(command);

        assertEquals("AMB-001", resultado.codigo());

        verify(recursoGateway)
                .existePorCodigo("AMB-001");
    }

    @Test
    void deberiaEnviarAlRepositorioElRecursoCreado() {
        RegistrarRecursoCommand command =
                crearCommand("AMB-001");

        when(
                recursoGateway.existePorCodigo("AMB-001")
        ).thenReturn(false);

        when(
                recursoGateway.guardar(any(Recurso.class))
        ).thenAnswer(invocacion -> invocacion.getArgument(0));

        useCase.registrar(command);

        ArgumentCaptor<Recurso> captor =
                ArgumentCaptor.forClass(Recurso.class);

        verify(recursoGateway).guardar(
                captor.capture()
        );

        Recurso recursoGuardado = captor.getValue();

        assertEquals(
                "AMB-001",
                recursoGuardado.getCodigo()
        );
        assertEquals(
                EstadoRecurso.DISPONIBLE,
                recursoGuardado.getEstado()
        );
        assertTrue(recursoGuardado.estaDisponible());
    }

    @Test
    void noDeberiaRegistrarUnCodigoDuplicado() {
        RegistrarRecursoCommand command =
                crearCommand("AMB-001");

        when(
                recursoGateway.existePorCodigo("AMB-001")
        ).thenReturn(true);

        CodigoRecursoDuplicadoException excepcion =
                assertThrows(
                        CodigoRecursoDuplicadoException.class,
                        () -> useCase.registrar(command)
                );

        assertEquals(
                "CODIGO_RECURSO_DUPLICADO",
                excepcion.getCodigo()
        );

        verify(recursoGateway)
                .existePorCodigo("AMB-001");

        verify(
                recursoGateway,
                never()
        ).guardar(any(Recurso.class));
    }

    @Test
    void deberiaRechazarUnComandoNulo() {
        assertThrows(
                NullPointerException.class,
                () -> useCase.registrar(null)
        );

        verifyNoInteractions(recursoGateway);
    }

    private RegistrarRecursoCommand crearCommand(
            String codigo
    ) {
        return new RegistrarRecursoCommand(
                codigo,
                "Ambulancia principal",
                TipoRecurso.AMBULANCIA,
                4.6579,
                -74.0596
        );
    }
}