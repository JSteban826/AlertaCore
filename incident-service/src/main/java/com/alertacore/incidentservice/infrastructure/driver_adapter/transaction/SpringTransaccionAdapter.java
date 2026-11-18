package com.alertacore.incidentservice.infrastructure.driver_adapter.transaction;

import com.alertacore.incidentservice.domain.model.gateway
        .TransaccionGateway;
import org.springframework.transaction.support
        .TransactionTemplate;

import java.util.Objects;
import java.util.function.Supplier;

public final class SpringTransaccionAdapter
        implements TransaccionGateway {

    private final TransactionTemplate transactionTemplate;

    public SpringTransaccionAdapter(
            TransactionTemplate transactionTemplate
    ) {
        this.transactionTemplate =
                Objects.requireNonNull(
                        transactionTemplate,
                        "El administrador transaccional es obligatorio"
                );
    }

    @Override
    public <T> T ejecutar(
            Supplier<T> operacion
    ) {
        Objects.requireNonNull(
                operacion,
                "La operacion transaccional es obligatoria"
        );

        return transactionTemplate.execute(
                estado -> operacion.get()
        );
    }
}