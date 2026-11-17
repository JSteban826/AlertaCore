CREATE TABLE IF NOT EXISTS resources.reservas_recursos (

    id UUID PRIMARY KEY,

    recurso_id UUID NOT NULL,

    incidente_id UUID NOT NULL,

    clave_idempotencia VARCHAR(255) NOT NULL,

    fecha_reserva TIMESTAMP NOT NULL,

    fecha_vencimiento TIMESTAMP NOT NULL,

    estado VARCHAR(50) NOT NULL
    );

CREATE UNIQUE INDEX ux_reserva_idempotencia
    ON resources.reservas_recursos(
                                   clave_idempotencia
        );

CREATE INDEX idx_reserva_incidente
    ON resources.reservas_recursos(
                                   incidente_id
        );

CREATE INDEX idx_reserva_vencimiento
    ON resources.reservas_recursos(
                                   fecha_vencimiento
        );