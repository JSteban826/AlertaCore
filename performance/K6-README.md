# Prueba de rendimiento de incident-service con k6

## Objetivo

- 20 usuarios virtuales durante 30 segundos.
- p95 menor a 500 ms.
- Menos de 1 % de solicitudes fallidas.
- Menos de 1 % de verificaciones inesperadas.

## Endpoints incluidos

- GET /api/v1/incidentes
- GET /api/v1/incidentes/{id}
- GET /api/v1/incidentes/{id}/linea-tiempo
- GET /api/v1/incidentes/estadisticas/tiempos-respuesta

No se prueban Open-Meteo ni Nominatim para evitar generar carga contra proveedores externos.

## Ejecución en PowerShell

1. Inicie incident-service.
2. Use el UUID de un incidente existente.
3. Ejecute:

```powershell
k6 run `
  -e BASE_URL=http://localhost:8081 `
  -e INCIDENT_ID=REEMPLAZAR_UUID `
  .\performance\incident-service-load.js
```

Resultado correcto:

- `http_req_failed` menor a 1 %.
- `http_req_duration p(95)` menor a 500 ms.
- Todos los thresholds terminan con marca verde.
