const express = require('express');
const { exec } = require('child_process');
const path = require('path');
const cors = require('cors');

const app = express();
const PORT = 3333;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static('public'));

// Utilitário para executar comandos shell
function executeCommand(command) {
  return new Promise((resolve, reject) => {
    exec(command, { maxBuffer: 10 * 1024 * 1024 }, (error, stdout, stderr) => {
      if (error && error.code !== 0) {
        reject({ error: error.message, stderr });
      } else {
        resolve(stdout || stderr);
      }
    });
  });
}

// ===== ENDPOINTS DOCKER =====

// GET - Status dos containers
app.get('/api/docker/status', async (req, res) => {
  try {
    const output = await executeCommand('docker ps --format "{{.Names}}|{{.Status}}|{{.Ports}}"');
    const containers = output
      .split('\n')
      .filter(line => line.trim())
      .map(line => {
        const [name, status, ports] = line.split('|');
        return { name, status: status.trim(), ports: ports.trim() };
      });
    res.json({ success: true, containers });
  } catch (err) {
    res.status(500).json({ success: false, error: err.error || err });
  }
});

// POST - Iniciar containers (docker-compose up)
app.post('/api/docker/start', async (req, res) => {
  try {
    const output = await executeCommand('cd c:\\Users\\perfe\\Trabakho_Final_SD\\ubiquitous-comunication-plataform && docker compose up -d --build');
    res.json({ success: true, message: 'Containers iniciados', output });
  } catch (err) {
    res.status(500).json({ success: false, error: err.error || err });
  }
});

// POST - Parar containers (docker-compose down)
app.post('/api/docker/stop', async (req, res) => {
  try {
    const output = await executeCommand('cd c:\\Users\\perfe\\Trabakho_Final_SD\\ubiquitous-comunication-plataform && docker compose down');
    res.json({ success: true, message: 'Containers parados', output });
  } catch (err) {
    res.status(500).json({ success: false, error: err.error || err });
  }
});

// GET - Logs da aplicação
app.get('/api/docker/logs', async (req, res) => {
  try {
    const lines = req.query.lines || 50;
    const output = await executeCommand(`docker logs --tail ${lines} platform_core_app`);
    res.json({ success: true, logs: output });
  } catch (err) {
    res.status(500).json({ success: false, error: err.error || err });
  }
});

// ===== ENDPOINTS API =====

// POST - Registrar usuário
app.post('/api/platform/register', async (req, res) => {
  const { username, password } = req.body;
  try {
    const curlCmd = `curl.exe -X POST http://localhost:8080/api/v1/auth/register -H "Content-Type: application/json" -d "{\\"username\\":\\"${username}\\",\\"password\\":\\"${password}\\"}"`;
    const output = await executeCommand(curlCmd);
    res.json({ success: true, data: JSON.parse(output) });
  } catch (err) {
    res.status(500).json({ success: false, error: err.error || err });
  }
});

// POST - Login
app.post('/api/platform/login', async (req, res) => {
  const { username, password } = req.body;
  try {
    const curlCmd = `curl.exe -X POST http://localhost:8080/api/v1/auth/login -H "Content-Type: application/json" -d "{\\"username\\":\\"${username}\\",\\"password\\":\\"${password}\\"}"`;
    const output = await executeCommand(curlCmd);
    res.json({ success: true, data: JSON.parse(output) });
  } catch (err) {
    res.status(500).json({ success: false, error: err.error || err });
  }
});

// POST - Iniciar upload
app.post('/api/platform/upload/initiate', async (req, res) => {
  const { filename, mimeType, fileSize, token } = req.body;
  try {
    const curlCmd = `curl.exe -X POST http://localhost:8080/api/v1/uploads/initiate -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d "{\\"filename\\":\\"${filename}\\",\\"mimeType\\":\\"${mimeType}\\",\\"fileSize\\":${fileSize}}"`;
    const output = await executeCommand(curlCmd);
    res.json({ success: true, data: JSON.parse(output) });
  } catch (err) {
    res.status(500).json({ success: false, error: err.error || err });
  }
});

// POST - Fazer upload (PUT para presigned URL)
app.post('/api/platform/upload/file', async (req, res) => {
  const { presignedUrl, content } = req.body;
  try {
    const curlCmd = `curl.exe -i -X PUT "${presignedUrl}" -d "${content}"`;
    const output = await executeCommand(curlCmd);
    // Extrair ETag do header (case-insensitive)
    const etagMatch = output.match(/etag:\s*"?([^"\r\n]+)"?/i);
    const eTag = etagMatch ? etagMatch[1].trim() : 'not-found';
    res.json({ success: true, eTag, output });
  } catch (err) {
    res.status(500).json({ success: false, error: err.error || err });
  }
});

// POST - Completar upload
app.post('/api/platform/upload/complete', async (req, res) => {
  const { attachmentId, eTag, token } = req.body;
  try {
    const body = JSON.stringify({ parts: [{ partNumber: 1, eTag }] }).replace(/"/g, '\\"');
    const curlCmd = `curl.exe -X POST http://localhost:8080/api/v1/uploads/${attachmentId}/complete -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d "${body}"`;
    const output = await executeCommand(curlCmd);
    res.json({ success: true, message: 'Upload completado', output });
  } catch (err) {
    res.status(500).json({ success: false, error: err.error || err });
  }
});

// POST - Enviar mensagem
app.post('/api/platform/messages/send', async (req, res) => {
  const { conversationId, payload, token } = req.body;
  try {
    const messageId = require('crypto').randomUUID();
    const body = JSON.stringify({
      messageId,
      conversationId,
      payload
    }).replace(/"/g, '\\"');
    
    const curlCmd = `curl.exe -X POST http://localhost:8080/api/v1/messages -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d "${body}"`;
    const output = await executeCommand(curlCmd);
    res.json({ success: true, message: 'Mensagem enviada', output });
  } catch (err) {
    res.status(500).json({ success: false, error: err.error || err });
  }
});

// Iniciar servidor
app.listen(PORT, () => {
  console.log(`Dashboard rodando em http://localhost:${PORT}`);
});
