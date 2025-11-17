import http from 'k6/http';
import { check, sleep } from 'k6';
import { crypto } from 'k6/crypto';

// Configuração do Teste
export const options = {
  stages: [
    { duration: '10s', target: 5 },  // Aquecimento rápido
    { duration: '30s', target: 200 }, // Carga moderada
    { duration: '10s', target: 0 },  // Resfriamento
  ],
};

// --- CONFIGURAÇÃO ---

// Usuário para login (garanta que ele existe via /auth/register)
const BASE_URL = 'http://host.docker.internal:8080/api/v1'; // host.docker.internal acessa o localhost do Windows
const USERNAME = 'cavalo de troia da grecia'; 
const PASSWORD = 'senha123456';

// Um ID de conversa válido (copie do seu banco ou Thunder Client)
const CONVERSATION_ID = 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'; 

// Função simples para gerar UUID v4 (fake, mas válido para o Java)
function uuidv4() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

// Função de Login
function login() {
  const payload = JSON.stringify({
    username: USERNAME,
    password: PASSWORD,
  });
  
  const params = {
    headers: { 'Content-Type': 'application/json' },
  };

  const res = http.post(`${BASE_URL}/auth/login`, payload, params);
  
  if (res.status !== 200) {
    console.error(`FALHA LOGIN: ${res.status} - ${res.body}`);
    return null;
  }
  
  return res.json('token');
}

export default function () {
  // 1. Login
  const token = login();
  if (!token) return;

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  // 2. Payload com UUID Válido
  const messagePayload = JSON.stringify({
    messageId: uuidv4(), // <-- AGORA GERA UM UUID CORRETO
    content: 'Load Test Message ' + new Date().toISOString(),
  });

  // 3. Enviar Mensagem
  const res = http.post(
    `${BASE_URL}/conversations/${CONVERSATION_ID}/messages`,
    messagePayload,
    params
  );

  // 4. Verificar se deu 202
  const success = check(res, {
    'status é 202': (r) => r.status === 202,
  });

  if (!success) {
      console.error(`FALHA MSG: ${res.status} - ${res.body}`);
  }

  sleep(1);
}