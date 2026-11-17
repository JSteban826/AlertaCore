package com.alertacore.incidentservice.infrastructure.client.openmeteo;

import com.alertacore.incidentservice.domain.model.CondicionMeteorologica;
import com.alertacore.incidentservice.domain.gateway.ServicioMeteorologicoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(
        ServicioMeteorologicoCacheIntegrationTest
                .ConfiguracionPrueba.class
)
class ServicioMeteorologicoCacheIntegrationTest {

    @Autowired
    @Qualifier("servicioCacheado")
    private ServicioMeteorologicoPort servicio;

    @Autowired
    @Qualifier("servicioDelegado")
    private ServicioMeteorologicoPort delegado;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void limpiarCache() {
        Cache cache = cacheManager.getCache(
                "condicionesMeteorologicas"
        );

        if (cache != null) {
            cache.clear();
        }

        clearInvocations(delegado);
    }

    @Test
    void debeUsarCacheEnLaSegundaConsulta() {
        CondicionMeteorologica condicion =
                new CondicionMeteorologica(
                        4.6579,
                        -74.0596,
                        15.4,
                        0,
                        0,
                        0,
                        8.5,
                        3,
                        LocalDateTime.now(),
                        "Open-Meteo"
                );

        when(
                delegado.consultarCondicionActual(
                        4.6579,
                        -74.0596
                )
        ).thenReturn(condicion);

        CondicionMeteorologica primera =
                servicio.consultarCondicionActual(
                        4.6579,
                        -74.0596
                );

        CondicionMeteorologica segunda =
                servicio.consultarCondicionActual(
                        4.6579,
                        -74.0596
                );

        assertSame(primera, segunda);

        verify(
                delegado,
                times(1)
        ).consultarCondicionActual(
                4.6579,
                -74.0596
        );
    }

    @Configuration
    @EnableCaching
    static class ConfiguracionPrueba {

        @Bean("servicioDelegado")
        public ServicioMeteorologicoPort servicioDelegado() {
            return mock(
                    ServicioMeteorologicoPort.class
            );
        }

        @Bean("servicioCacheado")
        @Primary
        public ServicioMeteorologicoPort servicioCacheado(
                @Qualifier("servicioDelegado")
                ServicioMeteorologicoPort delegado
        ) {
            return new ServicioMeteorologicoCacheado(
                    delegado
            );
        }

        @Bean
        public CacheManager cacheManager() {
            ConcurrentMapCacheManager manager =
                    new ConcurrentMapCacheManager();

            manager.setCacheNames(
                    List.of(
                            "condicionesMeteorologicas"
                    )
            );

            return manager;
        }
    }
}