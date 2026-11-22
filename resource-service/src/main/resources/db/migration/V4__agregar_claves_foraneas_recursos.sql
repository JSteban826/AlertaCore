-- Indices de apoyo para consultas y validacion de claves foraneas.
CREATE INDEX IF NOT EXISTS idx_reservas_recurso
    ON resources.reservas_recursos(recurso_id);

CREATE INDEX IF NOT EXISTS idx_despachos_recurso
    ON resources.despachos_recursos(recurso_id);

-- FK interna: una reserva debe pertenecer a un recurso existente.
ALTER TABLE resources.reservas_recursos
    ADD CONSTRAINT fk_reservas_recursos_recurso
        FOREIGN KEY (recurso_id)
            REFERENCES resources.recursos(id)
            ON UPDATE RESTRICT
            ON DELETE RESTRICT;

-- FK interna: un despacho debe pertenecer a un recurso existente.
ALTER TABLE resources.despachos_recursos
    ADD CONSTRAINT fk_despachos_recursos_recurso
        FOREIGN KEY (recurso_id)
            REFERENCES resources.recursos(id)
            ON UPDATE RESTRICT
            ON DELETE RESTRICT;