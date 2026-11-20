CREATE TABLE notifications.notificaciones (
                                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                              incidente_id UUID NOT NULL,
                                              recurso_id UUID NOT NULL,
                                              fecha_recepcion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);