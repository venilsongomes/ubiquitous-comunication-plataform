import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  vus: 50,
  duration: '30s',
};

export default function () {
  const url = 'http://app:8080/api/v1/auth/login';

  const payload = JSON.stringify({
    username: "Login",
    password: "1234"
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
  };

  http.post(url, payload, params);
  sleep(1);
}
