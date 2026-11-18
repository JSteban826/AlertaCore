package com.alertacore.incidentservice.infrastructure.driver_adapter.transaction;

import com.alertacore.incidentservice.infrastructure.driver_adapter.transaction.SpringTransaccionAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpringTransaccionAdapterTest {

    @Test
    void debeEjecutarOperacionDentroDeTransactionTemplate() {
        TransactionTemplate transactionTemplate =
                mock(TransactionTemplate.class);

        TransactionStatus estado =
                mock(TransactionStatus.class);

        Object resultadoEsperado =
                new Object();

        when(
                transactionTemplate.execute(
                        any()
                )
        ).thenAnswer(
                invocacion -> {
                    @SuppressWarnings("unchecked")
                    TransactionCallback<Object> callback =
                            invocacion.getArgument(0);

                    return callback.doInTransaction(
                            estado
                    );
                }
        );

        SpringTransaccionAdapter adapter =
                new SpringTransaccionAdapter(
                        transactionTemplate
                );

        Object resultado =
                adapter.ejecutar(
                        () -> resultadoEsperado
                );

        assertSame(
                resultadoEsperado,
                resultado
        );

        verify(transactionTemplate)
                .execute(any());
    }

    @Test
    void debePropagarExcepcionDeLaOperacion() {
        TransactionTemplate transactionTemplate =
                mock(TransactionTemplate.class);

        TransactionStatus estado =
                mock(TransactionStatus.class);

        when(
                transactionTemplate.execute(
                        any()
                )
        ).thenAnswer(
                invocacion -> {
                    @SuppressWarnings("unchecked")
                    TransactionCallback<Object> callback =
                            invocacion.getArgument(0);

                    return callback.doInTransaction(
                            estado
                    );
                }
        );

        SpringTransaccionAdapter adapter =
                new SpringTransaccionAdapter(
                        transactionTemplate
                );

        RuntimeException excepcion =
                assertThrows(
                        RuntimeException.class,
                        () -> adapter.ejecutar(
                                () -> {
                                    throw new RuntimeException(
                                            "Fallo dentro de la transaccion"
                                    );
                                }
                        )
                );

        assertEquals(
                "Fallo dentro de la transaccion",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarTransactionTemplateNulo() {
        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> new SpringTransaccionAdapter(
                                null
                        )
                );

        assertEquals(
                "El administrador transaccional es obligatorio",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRechazarOperacionNula() {
        TransactionTemplate transactionTemplate =
                mock(TransactionTemplate.class);

        SpringTransaccionAdapter adapter =
                new SpringTransaccionAdapter(
                        transactionTemplate
                );

        NullPointerException excepcion =
                assertThrows(
                        NullPointerException.class,
                        () -> adapter.ejecutar(
                                null
                        )
                );

        assertEquals(
                "La operacion transaccional es obligatoria",
                excepcion.getMessage()
        );
    }

    @Test
    void debeRetornarNuloCuandoLaOperacionRetornaNulo() {
        TransactionTemplate transactionTemplate =
                mock(TransactionTemplate.class);

        TransactionStatus estado =
                mock(TransactionStatus.class);

        when(
                transactionTemplate.execute(
                        any()
                )
        ).thenAnswer(
                invocacion -> {
                    @SuppressWarnings("unchecked")
                    TransactionCallback<Object> callback =
                            invocacion.getArgument(0);

                    return callback.doInTransaction(
                            estado
                    );
                }
        );

        SpringTransaccionAdapter adapter =
                new SpringTransaccionAdapter(
                        transactionTemplate
                );

        Object resultado =
                adapter.ejecutar(
                        () -> null
                );

        assertEquals(
                null,
                resultado
        );
    }
}