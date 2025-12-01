import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from './uuid.js';

export const options = {
  vus: 30,
  duration: '45s',
};

// ----------------------------
// LOGIN SEGURO
// ----------------------------
function login() {
  const payload = JSON.stringify({
    username: "Login",
    password: "1234",
  });

  const headers = { 'Content-Type': 'application/json' };

  const res = http.post(
    'http://app:8080/api/v1/auth/login',
    payload,
    { headers }
  );

  if (!res.body) {
    console.error("❌ Login retornou body vazio!");
    return null;
  }

  if (!res.headers['Content-Type']?.includes('application/json')) {
    console.error("❌ Login não retornou JSON:", res.body);
    return null;
  }

  try {
    const json = JSON.parse(res.body);
    return json.token;
  } catch (err) {
    console.error("❌ Falha ao parsear JSON do login:", err, res.body);
    return null;
  }
}

// ----------------------------
// TESTE PRINCIPAL
// ----------------------------
export default function () {
  const token = login();

  if (!token) {
    console.error("❌ Abortando: sem token");
    sleep(1);
    return;
  }

  const payload = JSON.stringify({
    messageId: uuidv4(),
    conversationId: uuidv4(), // sua API exige conversationId
    payload: {
      type: "text",
      text: "Load test message"
    }
  });

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };

  const res = http.post(
    'http://app:8080/api/v1/messages',
    payload,
    { headers }
  );

  check(res, {
    "status é 202": (r) => r.status === 202,
  });

  if (res.status !== 202) {
    console.error(`❌ ERRO AO ENVIAR MSG — Status ${res.status} | Body: ${res.body}`);
  }

  sleep(0.5);
}
