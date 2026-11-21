package com.alertacore.resourceservice.application.config;

import com.alertacore.resourceservice.domain.model.gateway.DespachoRecursoGateway;
import com.alertacore.resourceservice.domain.model.gateway.NotificacionDespachoGateway;
import com.alertacore.resourceservice.domain.model.gateway.RecursoGateway;
import com.alertacore.resourceservice.domain.model.gateway.ReservaRecursoGateway;
import com.alertacore.resourceservice.domain.service.CalculadorDistanciaHaversine;
import com.alertacore.resourceservice.domain.usecase.ActualizarUbicacionRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.BuscarRecursoMasCercanoUseCase;
import com.alertacore.resourceservice.domain.usecase.BuscarRecursosDisponiblesUseCase;
import com.alertacore.resourceservice.domain.usecase.ConsultarRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.DespacharRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.GenerarReporteUtilizacionUseCase;
import com.alertacore.resourceservice.domain.usecase.LiberarRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.ListarRecursosUseCase;
import com.alertacore.resourceservice.domain.usecase.RegistrarRecursoUseCase;
import com.alertacore.resourceservice.domain.usecase.ReservarRecursoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UseCaseConfigTest {

    @Mock
    private RecursoGateway recursoGateway;

    @Mock
    private ReservaRecursoGateway reservaRecursoGateway;

    @Mock
    private DespachoRecursoGateway despachoRecursoGateway;

    @Mock
    private NotificacionDespachoGateway notificacionDespachoGateway;

    @Mock
    private PlatformTransactionManager transactionManager;

    private UseCaseConfig config;

    @BeforeEach
    void setUp() {
        config = new UseCaseConfig();
    }

    @Test
    void deberiaCrearCalculadorDistanciaHaversine() {
        CalculadorDistanciaHaversine calculador =
                config.calculadorDistanciaHaversine();

        assertNotNull(calculador);
    }

    @Test
    void deberiaCrearRegistrarRecursoUseCase() {
        RegistrarRecursoUseCase useCase =
                config.registrarRecursoUseCase(
                        recursoGateway
                );

        assertNotNull(useCase);
    }

    @Test
    void deberiaCrearBuscarRecursoMasCercanoUseCase() {
        CalculadorDistanciaHaversine calculador =
                new CalculadorDistanciaHaversine();

        BuscarRecursoMasCercanoUseCase useCase =
                config.buscarRecursoMasCercanoUseCase(
                        recursoGateway,
                        calculador
                );

        assertNotNull(useCase);
    }

    @Test
    void deberiaCrearReservarRecursoUseCase() {
        ReservarRecursoUseCase useCase =
                config.reservarRecursoUseCase(
                        recursoGateway,
                        reservaRecursoGateway
                );

        assertNotNull(useCase);
    }

    @Test
    void deberiaCrearDespacharRecursoUseCase() {
        DespacharRecursoUseCase useCase =
                config.despacharRecursoUseCase(
                        recursoGateway,
                        despachoRecursoGateway,
                        notificacionDespachoGateway,
                        transactionManager
                );

        assertNotNull(useCase);
        assertInstanceOf(
                DespacharRecursoUseCase.class,
                useCase
        );
    }

    @Test
    void deberiaCrearLiberarRecursoUseCase() {
        LiberarRecursoUseCase useCase =
                config.liberarRecursoUseCase(
                        recursoGateway,
                        reservaRecursoGateway
                );

        assertNotNull(useCase);
    }

    @Test
    void deberiaCrearActualizarUbicacionRecursoUseCase() {
        ActualizarUbicacionRecursoUseCase useCase =
                config.actualizarUbicacionRecursoUseCase(
                        recursoGateway
                );

        assertNotNull(useCase);
    }

    @Test
    void deberiaCrearGenerarReporteUtilizacionUseCase() {
        GenerarReporteUtilizacionUseCase useCase =
                config.generarReporteUtilizacionUseCase(
                        recursoGateway
                );

        assertNotNull(useCase);
    }

    @Test
    void deberiaCrearConsultarRecursoUseCase() {
        ConsultarRecursoUseCase useCase =
                config.consultarRecursoUseCase(
                        recursoGateway
                );

        assertNotNull(useCase);
    }

    @Test
    void deberiaCrearBuscarRecursosDisponiblesUseCase() {
        BuscarRecursosDisponiblesUseCase useCase =
                config.buscarRecursosDisponiblesUseCase(
                        recursoGateway
                );

        assertNotNull(useCase);
    }

    @Test
    void deberiaCrearListarRecursosUseCase() {
        ListarRecursosUseCase useCase =
                config.listarRecursosUseCase(
                        recursoGateway
                );

        assertNotNull(useCase);
    }

    @Test
    void deberiaCrearTodosLosCasosDeUsoSimples() {
        CalculadorDistanciaHaversine calculador =
                config.calculadorDistanciaHaversine();

        assertAll(
                () -> assertNotNull(calculador),
                () -> assertNotNull(
                        config.registrarRecursoUseCase(
                                recursoGateway
                        )
                ),
                () -> assertNotNull(
                        config.buscarRecursoMasCercanoUseCase(
                                recursoGateway,
                                calculador
                        )
                ),
                () -> assertNotNull(
                        config.reservarRecursoUseCase(
                                recursoGateway,
                                reservaRecursoGateway
                        )
                ),
                () -> assertNotNull(
                        config.liberarRecursoUseCase(
                                recursoGateway,
                                reservaRecursoGateway
                        )
                ),
                () -> assertNotNull(
                        config.actualizarUbicacionRecursoUseCase(
                                recursoGateway
                        )
                ),
                () -> assertNotNull(
                        config.generarReporteUtilizacionUseCase(
                                recursoGateway
                        )
                ),
                () -> assertNotNull(
                        config.consultarRecursoUseCase(
                                recursoGateway
                        )
                ),
                () -> assertNotNull(
                        config.buscarRecursosDisponiblesUseCase(
                                recursoGateway
                        )
                ),
                () -> assertNotNull(
                        config.listarRecursosUseCase(
                                recursoGateway
                        )
                )
        );
    }
}