package com.alertacore.incidentservice.infrastructure
        .driver_adapter.resource;

import com.alertacore.incidentservice.domain.enums.TipoRecurso;
import com.alertacore.incidentservice.domain.exception.RecursoDisponibleNoEncontradoException;
import com.alertacore.incidentservice.domain.exception.RecursoNoDisponibleException;
import com.alertacore.incidentservice.domain.exception.ServicioRecursosException;
import com.alertacore.incidentservice.domain.model.RecursoDisponible;
import com.alertacore.incidentservice.domain.model.ReservaRecurso;
import com.alertacore.incidentservice.domain.model.gateway.ServicioRecursosGateway;
import com.alertacore.incidentservice.infrastructure.driver_adapter.resource.dto.BuscarRecursoMasCercanoRequest;
import com.alertacore.incidentservice.infrastructure.driver_adapter.resource.dto.LiberarRecursoRequest;
import com.alertacore.incidentservice.infrastructure.driver_adapter.resource.dto.LiberarRecursoResponse;
import com.alertacore.incidentservice.infrastructure.driver_adapter.resource.dto.RecursoCercanoResponse;
import com.alertacore.incidentservice.infrastructure.driver_adapter.resource.dto.ReservaRecursoResponse;
import com.alertacore.incidentservice.infrastructure.driver_adapter.resource.dto.ReservarRecursoRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Objects;
import java.util.UUID;

public final class ResourceServiceHttpAdapter
        implements ServicioRecursosGateway {

    private final RestClient restClient;

    public ResourceServiceHttpAdapter(
            RestClient restClient
    ) {
        this.restClient = Objects.requireNonNull(
                restClient,
                "El cliente HTTP de recursos es obligatorio"
        );
    }

    @Override
    public RecursoDisponible buscarMasCercano(
            UUID incidenteId,
            TipoRecurso tipoRecurso,
            double latitudIncidente,
            double longitudIncidente,
            double distanciaMaximaKm
    ) {
        BuscarRecursoMasCercanoRequest request =
                new BuscarRecursoMasCercanoRequest(
                        incidenteId,
                        tipoRecurso,
                        latitudIncidente,
                        longitudIncidente,
                        distanciaMaximaKm
                );

        try {
            RecursoCercanoResponse response =
                    restClient.post()
                            .uri(
                                    "/api/v1/recursos/"
                                            + "buscar-mas-cercano"
                            )
                            .body(request)
                            .retrieve()
                            .onStatus(
                                    estado ->
                                            estado.value() == 404,
                                    (solicitud, respuesta) -> {
                                        throw new RecursoDisponibleNoEncontradoException(
                                                tipoRecurso
                                        );
                                    }
                            )
                            .onStatus(
                                    HttpStatusCode::is5xxServerError,
                                    (solicitud, respuesta) -> {
                                        throw new ServicioRecursosException(
                                                "Resource Service no pudo buscar recursos"
                                        );
                                    }
                            )
                            .body(
                                    RecursoCercanoResponse.class
                            );

            if (response == null) {
                throw new ServicioRecursosException(
                        "Resource Service devolvio una respuesta vacia "
                                + "al buscar recursos"
                );
            }

            return response.aDominio();
        } catch (
                RecursoDisponibleNoEncontradoException
                | ServicioRecursosException excepcion
        ) {
            throw excepcion;
        } catch (RestClientException excepcion) {
            throw new ServicioRecursosException(
                    "No fue posible comunicarse con Resource Service",
                    excepcion
            );
        }
    }

    @Override
    public ReservaRecurso reservar(
            UUID recursoId,
            UUID incidenteId,
            int duracionReservaMinutos,
            String idempotencyKey
    ) {
        Objects.requireNonNull(
                recursoId,
                "El identificador del recurso es obligatorio"
        );

        Objects.requireNonNull(
                idempotencyKey,
                "La llave de idempotencia es obligatoria"
        );

        ReservarRecursoRequest request =
                new ReservarRecursoRequest(
                        incidenteId,
                        duracionReservaMinutos
                );

        try {
            ReservaRecursoResponse response =
                    restClient.post()
                            .uri(
                                    "/api/v1/recursos/{id}/reservar",
                                    recursoId
                            )
                            .header(
                                    "Idempotency-Key",
                                    idempotencyKey
                            )
                            .body(request)
                            .retrieve()
                            .onStatus(
                                    estado ->
                                            estado.value() == 409,
                                    (solicitud, respuesta) -> {
                                        throw new RecursoNoDisponibleException(
                                                recursoId
                                        );
                                    }
                            )
                            .onStatus(
                                    HttpStatusCode::is5xxServerError,
                                    (solicitud, respuesta) -> {
                                        throw new ServicioRecursosException(
                                                "Resource Service no pudo reservar el recurso"
                                        );
                                    }
                            )
                            .body(
                                    ReservaRecursoResponse.class
                            );

            if (response == null) {
                throw new ServicioRecursosException(
                        "Resource Service devolvio una respuesta vacia "
                                + "al reservar el recurso"
                );
            }

            return response.aDominio();
        } catch (
                RecursoNoDisponibleException
                | ServicioRecursosException excepcion
        ) {
            throw excepcion;
        } catch (RestClientException excepcion) {
            throw new ServicioRecursosException(
                    "No fue posible reservar el recurso en Resource Service",
                    excepcion
            );
        }
    }

    @Override
    public void liberar(
            UUID recursoId,
            UUID incidenteId,
            String motivo
    ) {
        Objects.requireNonNull(
                recursoId,
                "El identificador del recurso es obligatorio"
        );

        LiberarRecursoRequest request =
                new LiberarRecursoRequest(
                        incidenteId,
                        motivo
                );

        try {
            LiberarRecursoResponse response =
                    restClient.post()
                            .uri(
                                    "/api/v1/recursos/{id}/liberar",
                                    recursoId
                            )
                            .body(request)
                            .retrieve()
                            .onStatus(
                                    estado ->
                                            estado.value() == 409,
                                    (solicitud, respuesta) -> {
                                        throw new ServicioRecursosException(
                                                "El recurso no pudo liberarse: "
                                                        + recursoId
                                        );
                                    }
                            )
                            .onStatus(
                                    HttpStatusCode::is5xxServerError,
                                    (solicitud, respuesta) -> {
                                        throw new ServicioRecursosException(
                                                "Resource Service no pudo liberar el recurso"
                                        );
                                    }
                            )
                            .body(
                                    LiberarRecursoResponse.class
                            );

            if (response == null) {
                throw new ServicioRecursosException(
                        "Resource Service devolvio una respuesta vacia "
                                + "al liberar el recurso"
                );
            }
        } catch (ServicioRecursosException excepcion) {
            throw excepcion;
        } catch (RestClientException excepcion) {
            throw new ServicioRecursosException(
                    "No fue posible liberar el recurso en Resource Service",
                    excepcion
            );
        }
    }
}