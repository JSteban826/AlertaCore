CREATE TABLE IF NOT EXISTS resources.recursos (

    id UUID PRIMARY KEY,

    codigo VARCHAR(50) NOT NULL UNIQUE,

    nombre VARCHAR(150) NOT NULL,

    tipo VARCHAR(50) NOT NULL,

    estado VARCHAR(50) NOT NULL,

    latitud DOUBLE PRECISION NOT NULL,

    longitud DOUBLE PRECISION NOT NULL,

    fecha_disponible_desde TIMESTAMP,

    fecha_ultima_actualizacion TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0
    );

CREATE INDEX idx_recurso_tipo_estado
    ON resources.recursos(tipo, estado);

CREATE INDEX idx_recurso_estado
    ON resources.recursos(estado);

CREATE INDEX idx_recurso_fecha_disponible
    ON resources.recursos(fecha_disponible_desde);