# 📊 Dashboard - Plataforma Ubíqua

Um painel web local para gerenciar e testar sua Plataforma de Comunicação Ubíqua.

## 🚀 Como usar

### 1. Instalar dependências

```bash
cd dashboard
npm install
```

### 2. Iniciar o Dashboard

```bash
npm start
```

O dashboard estará disponível em: **http://localhost:3333**

### 3. Funcionalidades

#### 📊 Status dos Containers
- Ver status de todos os containers Docker
- Iniciar containers (`docker compose up -d --build`)
- Parar containers (`docker compose down`)

#### 🔐 Autenticação JWT
- Registrar novo usuário
- Fazer login e obter token JWT
- Token é exibido para uso em requisições

#### 📁 Upload S3/MinIO
Fluxo de 3 passos via interface:
1. **Iniciar Upload** - Gera presigned URL
2. **Upload Arquivo** - Envia arquivo e recebe ETag
3. **Completar Upload** - Finaliza upload multipart

#### 📜 Logs
- Visualizar últimos 50, 100 ou 200 logs
- Logs da aplicação Java em tempo real

## 📋 Estrutura

```
dashboard/
├── server.js              # Backend Express (endpoints Docker + API)
├── package.json           # Dependências Node
└── public/
    └── index.html         # Frontend (HTML + CSS + JavaScript)
```

## 🔌 Endpoints do Dashboard

### Docker
- `GET /api/docker/status` - Status dos containers
- `POST /api/docker/start` - Iniciar containers
- `POST /api/docker/stop` - Parar containers
- `GET /api/docker/logs` - Logs da aplicação

### Plataforma (passa-through para a API)
- `POST /api/platform/register` - Registrar usuário
- `POST /api/platform/login` - Fazer login
- `POST /api/platform/upload/initiate` - Iniciar upload
- `POST /api/platform/upload/file` - Fazer upload de arquivo
- `POST /api/platform/upload/complete` - Completar upload

## 💡 Exemplos de uso

### Workflow completo de teste:

1. **Iniciar containers**: Clique em "Iniciar"
2. **Aguarde ~60s** e clique em "Atualizar Status"
3. **Registrar usuário**: Defina username/password e clique "Registrar"
4. **Login**: Clique "Login" para obter token
5. **Upload**: 
   - Clique "1️⃣ Iniciar Upload"
   - Clique "2️⃣ Upload Arquivo"
   - Clique "3️⃣ Completar Upload"
6. **Logs**: Visualize os logs em tempo real

## 🐛 Troubleshooting

### "Erro de conexão ao iniciar containers"
- Verifique se Docker Desktop está rodando
- Verifique caminho do diretório do projeto em `server.js`

### "Token inválido no upload"
- Faça login novamente antes de testar upload
- Certifique-se que a API está rodando em `http://localhost:8080`

### "ETag vazio"
- Verifique se o arquivo foi realmente enviado para o MinIO
- Consulte os logs da aplicação

## 📝 Notas

- O dashboard é **apenas para uso local** (não tem autenticação)
- Os comandos são executados no shell do sistema
- Windows PowerShell é necessário para os comandos curl

---

**Desenvolvido para facilitar testes e desenvolvimento da Plataforma de Comunicação Ubíqua** 🚀
