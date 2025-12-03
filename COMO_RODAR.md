# 🚀 GUIA COMPLETO - COMO RODAR O PROJETO DO ZERO

## ✅ PRÉ-REQUISITOS

Antes de começar, certifique-se de ter instalado:

1. **Docker Desktop** - https://www.docker.com/products/docker-desktop
2. **Git** - https://git-scm.com/
3. **PowerShell** ou **CMD** (Windows)

---

## 📋 PASSO 1: CLONAR O REPOSITÓRIO

```bash
git clone https://github.com/venilsongomes/ubiquitous-comunication-plataform.git
cd ubiquitous-comunication-plataform
```

---

## 🐳 PASSO 2: SUBIR A INFRAESTRUTURA (Docker Compose)

Na raiz do projeto, execute:

```bash
docker-compose up -d --build
```

**O que acontece:**
- Constrói a imagem Docker do Java/Spring Boot
- Inicia 9 containers:
  - ✅ `platform_core_app` (Spring Boot API - porta 8080)
  - ✅ `postgres` (Banco de dados - porta 5432)
  - ✅ `kafka` (Message Broker - porta 9092)
  - ✅ `zookeeper` (Coordenador Kafka)
  - ✅ `redis` (Cache & Presença - porta 6379)
  - ✅ `minio` (S3 Storage - porta 9000)
  - ✅ `prometheus` (Métricas - porta 9090)
  - ✅ `grafana` (Dashboards - porta 3000)

**Tempo esperado:** 60-90 segundos

**Verificar status:**
```bash
docker ps
```

Todos os containers devem estar com status `Up`.

---

## 📊 PASSO 3: ABRIR O DASHBOARD GERENCIAL

Assim que os containers estiverem rodando, abra **em outro terminal**:

```bash
cd dashboard
npm install
npm start
```

Ou simplesmente use:
```bash
node server-complete.js
```

**Resultado esperado:**
```
✅ Dashboard rodando em http://localhost:3333
📊 Todas as funcionalidades disponíveis!
```

**Acesse em seu navegador:**
- 🌐 **http://localhost:3333** - Dashboard completo (8 abas)

---

## 🎯 PASSO 4: ACESSAR OS SERVIÇOS

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| **Dashboard Gerencial** | http://localhost:3333 | Sem login |
| **API Spring Boot** | http://localhost:8080 | Usar JWT |
| **Kafka UI** (opcional) | http://localhost:9101 | Sem login |
| **MinIO Console** | http://localhost:9001 | minioadmin / minioadmin |
| **Prometheus** | http://localhost:9090 | Sem login |
| **Grafana** | http://localhost:3000 | admin / admin |
| **PostgreSQL** | localhost:5432 | postgres / postgres |
| **Redis** | localhost:6379 | Sem senha |

---

## 🔐 PASSO 5: AUTENTICAÇÃO & PRIMEIRO TESTE

### Via Dashboard (Recomendado):

1. Abra http://localhost:3333
2. Vá até a aba **🔐 Autenticação**
3. Em "Criar Novo Usuário":
   - Username: `user`
   - Email: `user@example.com`
   - Senha: `Senha123!`
4. Clique em **📝 Registrar**
5. Em "Fazer Login":
   - Username: `user`
   - Senha: `Senha123!`
6. Clique em **🔓 Login** - você receberá o **JWT Token**

### Via cURL (Linha de Comando):

**Registrar:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register `
  -H "Content-Type: application/json" `
  -d "{\"username\":\"user\",\"email\":\"user@example.com\",\"password\":\"Senha123!\"}"
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login `
  -H "Content-Type: application/json" `
  -d "{\"username\":\"user\",\"password\":\"Senha123!\"}"
```

Copie o `token` retornado.

---

## 💬 PASSO 6: TESTAR FUNCIONALIDADES

### 📍 Presença (ONLINE/OFFLINE)

**Via Dashboard:**
1. Aba **📍 Presença**
2. Clique **🟢 Online**
3. Clique **🔄 Atualizar Lista** para ver usuários online

**Via cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/presence/online `
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

### 💬 Mensageria em Tempo Real

**Via Dashboard:**
1. Aba **💬 Mensageria**
2. Clique **🔄 Gerar ID** para criar um conversation ID
3. Digite sua mensagem em "Conteúdo da mensagem"
4. Clique **📤 Enviar**

### 📁 Upload de Arquivo

**Via Dashboard:**
1. Aba **📁 Armazenamento**
2. Digite nome do arquivo: `teste.pdf`
3. MIME Type: `application/pdf`
4. Clique **1️⃣ Iniciar Upload**
5. Clique **2️⃣ Enviar Arquivo**
6. Clique **3️⃣ Completar**

### 📊 Infraestrutura

**Via Dashboard:**
1. Aba **📊 Infraestrutura**
2. Clique **🔄 Atualizar** para ver status de todos os containers
3. Clique em cada card para detalhes (Kafka, Redis, PostgreSQL)

---

## 📜 LOGS EM TEMPO REAL

### Ver logs da aplicação Spring Boot:
```bash
docker logs -f platform_core_app
```

### Ver logs do dashboard:
Já aparecem no terminal onde você rodou `npm start`

### Ver logs de um container específico:
```bash
docker logs -f <container_name>
```

---

## 🛑 PARAR A INFRAESTRUTURA

```bash
docker-compose down
```

Para remover volumes (apaga dados):
```bash
docker-compose down -v
```

---

## 🔧 TROUBLESHOOTING

### ❌ Porta 3333 já em uso
```bash
# Encontrar processo usando a porta
netstat -ano | findstr :3333

# Matar o processo (substitua PID)
taskkill /F /PID <PID>
```

### ❌ Containers não iniciam
```bash
# Ver logs de erro
docker-compose logs platform_core_app

# Reiniciar tudo do zero
docker-compose down -v
docker-compose up -d --build
```

### ❌ Dashboard branco
- Aguarde 60s para a aplicação iniciar
- Limpe o cache do navegador (Ctrl+Shift+Del)
- Recarregue a página (Ctrl+F5)

### ❌ Erro "Cannot connect to Docker"
- Abra o Docker Desktop
- Aguarde a inicialização
- Tente novamente

---

## 📱 INTEGRAÇÃO TELEGRAM (Opcional)

1. Aba **📱 Telegram** no dashboard
2. Insira seu **Bot Token** e **Chat ID**
3. Clique **💾 Salvar Configuração**
4. Teste com **📤 Enviar**

---

## 🎓 ESTRUTURA DO PROJETO

```
ubiquitous-comunication-plataform/
├── docker-compose.yml          # Orquestração dos containers
├── platform-core/              # Aplicação Spring Boot (porta 8080)
│   ├── pom.xml
│   ├── src/main/java/
│   └── Dockerfile
├── dashboard/                  # Interface web (porta 3333)
│   ├── server-complete.js
│   ├── public/
│   │   └── index.html
│   └── package.json
├── monitoring/
│   └── prometheus.yml          # Configuração de métricas
├── docs/                       # Documentação
└── README.md
```

---

## 📚 DOCUMENTAÇÃO ADICIONAL

- **API Completa:** http://localhost:8080/swagger-ui.html
- **Prometheus:** http://localhost:9090/graph
- **Grafana Dashboards:** http://localhost:3000/d/

---

## ✨ RESUMO RÁPIDO

```bash
# 1. Clonar
git clone https://github.com/venilsongomes/ubiquitous-comunication-plataform.git
cd ubiquitous-comunication-plataform

# 2. Subir infraestrutura
docker-compose up -d --build

# 3. Abrir dashboard (em outro terminal)
cd dashboard
npm install
node server-complete.js

# 4. Acessar
# Dashboard: http://localhost:3333
# API: http://localhost:8080
# MinIO: http://localhost:9001
# Grafana: http://localhost:3000
```


