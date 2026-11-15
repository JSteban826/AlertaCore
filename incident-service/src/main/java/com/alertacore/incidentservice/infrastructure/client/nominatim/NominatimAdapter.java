package com.alertacore.incidentservice.infrastructure.client.nominatim;

import com.alertacore.incidentservice.domain.exception.DireccionNoEncontradaException;
import com.alertacore.incidentservice.domain.exception.ServicioGeocodificacionException;
import com.alertacore.incidentservice.domain.model.ResultadoGeocodificacion;
import com.alertacore.incidentservice.domain.port.out.ServicioGeocodificacionPort;
import com.alertacore.incidentservice.infrastructure.client.nominatim.dto.NominatimSearchResponse;
import org.springframework.web.client.RestClient;

import java.util.Objects;

public final class NominatimAdapter
        implements ServicioGeocodificacionPort {

    private static final String FUENTE =
            "OpenStreetMap Nominatim";

    private static final String ATRIBUCION =
            "© OpenStreetMap contributors";

    private final RestClient restClient;

    public NominatimAdapter(
            RestClient restClient
    ) {
        this.restClient = Objects.requireNonNull(
                restClient,
                "El cliente HTTP es obligatorio"
        );
    }

    @Override
    public ResultadoGeocodificacion geocodificar(
            String direccion
    ) {
        Objects.requireNonNull(
                direccion,
                "La direccion es obligatoria"
        );

        try {
            NominatimSearchResponse[] respuesta =
                    restClient.get()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path("/search")
                                            .queryParam(
                                                    "q",
                                                    direccion
                                            )
                                            .queryParam(
                                                    "format",
                                                    "jsonv2"
                                            )
                                            .queryParam(
                                                    "limit",
                                                    1
                                            )
                                            .queryParam(
                                                    "addressdetails",
                                                    1
                                            )
                                            .queryParam(
                                                    "countrycodes",
                                                    "co"
                                            )
                                            .build()
                            )
                            .retrieve()
                            .body(
                                    NominatimSearchResponse[].class
                            );

            if (respuesta == null
                    || respuesta.length == 0) {

                throw new DireccionNoEncontradaException(
                        direccion
                );
            }

            NominatimSearchResponse primerResultado =
                    respuesta[0];

            double latitud = Double.parseDouble(
                    primerResultado.lat()
            );

            double longitud = Double.parseDouble(
                    primerResultado.lon()
            );

            return new ResultadoGeocodificacion(
                    direccion,
                    primerResultado.display_name(),
                    latitud,
                    longitud,
                    FUENTE,
                    ATRIBUCION
            );
        } catch (DireccionNoEncontradaException excepcion) {
            throw excepcion;
        } catch (Exception excepcion) {
            throw new ServicioGeocodificacionException(
                    "No fue posible consultar Nominatim",
                    excepcion
            );
        }
    }
}