import http from 'k6/http';

export let options = {
  stages: [
    { duration: '20s', target: 50 },   // rampa
    { duration: '30s', target: 150 },  // carga alta
    { duration: '10s', target: 0 },    // rampa para baixo
  ],
};

export default function () {
  http.get('http://app:8080/actuator/health');
}
