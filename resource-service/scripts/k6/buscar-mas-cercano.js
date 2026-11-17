import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {

    vus: 100,

    duration: '30s',

    thresholds: {

        http_req_failed: ['rate<0.01'],

        http_req_duration: ['p(95)<500']
    }
};

export default function () {

    const payload = JSON.stringify({

        incidenteId:
            '11111111-1111-1111-1111-111111111111',

        tipoRecurso:
            'AMBULANCIA',

        latitudIncidente:
            4.6579,

        longitudIncidente:
            -74.0596,

        distanciaMaximaKm:
            30
    });

    const params = {

        headers: {

            'Content-Type':
                'application/json'
        }
    };

    const response =
        http.post(
            'http://localhost:8082/api/v1/recursos/buscar-mas-cercano',
            payload,
            params
        );

    check(response, {

        'status 200':
            (r) => r.status === 200
    });

    sleep(1);
}