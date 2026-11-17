import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

const responseTime =
    new Trend('incident_response_time', true);

const unexpectedErrors =
    new Rate('unexpected_errors');

export const options = {
  scenarios: {
    incident_read_load: {
      executor: 'constant-vus',
      vus: 20,
      duration: '30s',
      gracefulStop: '5s',
    },
  },

  thresholds: {
    /*
     * Solo se mide la carga real.
     * La comprobación inicial de salud queda excluida.
     */
    'http_req_failed{request_type:load}': [
      'rate<0.01',
    ],

    'http_req_duration{request_type:load}': [
      'p(95)<500',
    ],

    incident_response_time: [
      'p(95)<500',
    ],

    unexpected_errors: [
      'rate<0.01',
    ],
  },
};

const BASE_URL =
    __ENV.BASE_URL || 'http://localhost:8081';

const INCIDENT_ID =
    __ENV.INCIDENT_ID;

export function setup() {
  if (!INCIDENT_ID) {
    throw new Error(
        'Falta INCIDENT_ID. Ejecute k6 con '
        + '-e INCIDENT_ID=<uuid-existente>'
    );
  }

  const health = http.get(
      `${BASE_URL}/actuator/health`,
      {
        tags: {
          request_type: 'setup',
          endpoint: 'health',
        },
        timeout: '10s',
      }
  );

  const disponible = check(
      health,
      {
        'servicio disponible antes de la carga':
            (respuesta) =>
                respuesta.status === 200
                && respuesta.json('status') === 'UP',
      }
  );

  if (!disponible) {
    throw new Error(
        `incident-service no esta disponible en ${BASE_URL}. `
        + `Estado recibido: ${health.status}`
    );
  }

  return {
    baseUrl: BASE_URL,
    incidentId: INCIDENT_ID,
  };
}

export default function (data) {
  const solicitudes = [
    {
      nombre: 'listar_incidentes',
      url:
          `${data.baseUrl}`
          + '/api/v1/incidentes?page=0&size=20',
    },
    {
      nombre: 'consultar_incidente',
      url:
          `${data.baseUrl}`
          + `/api/v1/incidentes/${data.incidentId}`,
    },
    {
      nombre: 'linea_tiempo',
      url:
          `${data.baseUrl}`
          + `/api/v1/incidentes/`
          + `${data.incidentId}/linea-tiempo`,
    },
    {
      nombre: 'estadisticas',
      url:
          `${data.baseUrl}`
          + '/api/v1/incidentes/'
          + 'estadisticas/tiempos-respuesta',
    },
  ];

  const indice =
      Math.floor(
          Math.random() * solicitudes.length
      );

  const solicitud =
      solicitudes[indice];

  const respuesta = http.get(
      solicitud.url,
      {
        headers: {
          Accept: 'application/json',
        },

        tags: {
          request_type: 'load',
          endpoint: solicitud.nombre,
        },

        timeout: '5s',
      }
  );

  responseTime.add(
      respuesta.timings.duration,
      {
        endpoint: solicitud.nombre,
      }
  );

  const estadoCorrecto = check(
      respuesta,
      {
        'estado HTTP 200':
            (resultado) =>
                resultado.status === 200,

        'respuesta application/json':
            (resultado) => {
              const contentType =
                  resultado.headers[
                      'Content-Type'
                      ] || '';

              return contentType.includes(
                  'application/json'
              );
            },

        'respuesta menor a 500 ms':
            (resultado) =>
                resultado.timings.duration < 500,
      },
      {
        endpoint: solicitud.nombre,
      }
  );

  unexpectedErrors.add(
      !estadoCorrecto,
      {
        endpoint: solicitud.nombre,
      }
  );

  /*
   * Simula un usuario que espera medio segundo
   * antes de realizar la siguiente consulta.
   */
  sleep(0.5);
}