import http from 'k6/http';
import { check, sleep } from 'k6';
import { crypto } from 'k6/crypto'; 

// --- 1. CONSTANTES CRUCIAIS ---
// ATENÇÃO: host.docker.internal acessa o host do Windows/Mac (onde o servidor deve responder)
const BASE_URL = 'http://host.docker.internal:8080/api/v1'; 

// ATUALIZE COM AS SUAS CREDENCIAIS DO BANCO DOCKER
const USERNAME = "Login"; 
const PASSWORD = "1234"; 
const CONVERSATION_ID = "7a5b764f-c37f-4ab2-b407-aafef773b638";
// ------------------------------------


// --- 2. FUNÇÃO UTILITÁRIA UUID (Estava faltando) ---
function uuidv4() {
  // Gera um UUID v4 pseudo-aleatório (funciona no Spring UUID)
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}
// --------------------------------------------------


// --- 3. FUNÇÃO SETUP (Login - Executa uma vez) ---
export const options = { /* ... (Suas opções de VUs) ... */ };

export function setup() {
  const payload = JSON.stringify({
    username: USERNAME,
    password: PASSWORD,
  });
  
  const params = { headers: { 'Content-Type': 'application/json' } };
  
  // Login na nova rota /auth/login
  const res = http.post(`${BASE_URL}/auth/login`, payload, params);

  if (res.status !== 200) {
    console.error(`ERRO CRÍTICO NO SETUP: Login falhou! Status: ${res.status}. Certifique-se de que o usuário existe no banco de dados Docker.`);
    return { token: null }; 
  }
  
  // Retorna o token para os VUs (Virtual Users)
  return { token: res.json('token') };
}

// --- 4. FUNÇÃO DEFAULT (Envio de Mensagem) ---
export default function (data) {
  if (!data.token) {
      // Se o setup falhou (erro 500 no login), os VUs param aqui.
      return; 
  }

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${data.token}`, // Usa o token do setup
    },
  };

  // 3. Payload com UUID Válido
  const messagePayload = JSON.stringify({
    messageId: uuidv4(), // <-- AGORA É DINÂMICO
    content: 'Load Test Message ' + new Date().toISOString(),
  });

  // 4. Enviar Mensagem (Para a rota protegida)
  const res = http.post(
    `${BASE_URL}/conversations/${CONVERSATION_ID}/messages`,
    messagePayload,
    params
  );

  // 5. Verificar se deu 202 (Accepted)
  const success = check(res, {
    'status é 202': (r) => r.status === 202,
  });

  if (!success) {
      console.error(`FALHA MSG: Status ${res.status}. Body: ${res.body}`);
  }

  sleep(1);
}