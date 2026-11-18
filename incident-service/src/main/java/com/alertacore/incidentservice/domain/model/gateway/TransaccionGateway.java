package com.alertacore.incidentservice.domain.model.gateway;

import java.util.function.Supplier;

public interface TransaccionGateway {

    <T> T ejecutar(
            Supplier<T> operacion
    );
}