# 🎯 Dashboard Completo - Guia de Uso

## ✨ Funcionalidades Implementadas

O novo dashboard **index-complete.html** inclui **8 abas principais** com todas as funcionalidades da plataforma:

### 📊 **Aba 1: Infraestrutura**
- ✅ Status dos Containers Docker em tempo real
- ✅ Verificar status do Apache Kafka
- ✅ Monitorar Redis Cache
- ✅ Verificar conexão com PostgreSQL
- ✅ Iniciar/Parar todos os containers
- ✅ Rebuild de containers
- ✅ Limpeza de Docker

### 🔐 **Aba 2: Autenticação JWT**
- ✅ Registrar novo usuário
- ✅ Fazer login com JWT
- ✅ Logout
- ✅ Copiar token JWT
- ✅ Validar token atual
- ✅ Visualizar informações do usuário

### 💬 **Aba 3: Mensageria em Tempo Real**
- ✅ Enviar mensagens de texto, imagem, vídeo, arquivo
- ✅ WebSocket para comunicação bidirecional
- ✅ Histórico de mensagens com timestamps
- ✅ Status de conexão WebSocket em tempo real
- ✅ Gerar ID de conversa automaticamente
- ✅ Suporte para múltiplos tipos de payload

### 📍 **Aba 4: Gerenciamento de Presença**
- ✅ Mudar status para ONLINE/OFFLINE
- ✅ Verificar presença de outro usuário
- ✅ Listar usuários online
- ✅ Indicador visual de status
- ✅ Atualizar lista de usuários em tempo real

### 📁 **Aba 5: Upload de Arquivos (S3/MinIO)**
- ✅ 3-step upload workflow (Iniciar → Enviar → Completar)
- ✅ Suporte para múltiplos tipos de arquivo
- ✅ Captura automática de ETag
- ✅ Acesso direto ao MinIO Console
- ✅ Listar buckets disponíveis
- ✅ Feedback visual do progresso

### 📱 **Aba 6: Integração Telegram**
- ✅ Configurar token e chat ID
- ✅ Enviar mensagens para Telegram
- ✅ Testar conexão com bot
- ✅ Receber mensagens do Telegram
- ✅ Histórico de conversas
- ✅ Status de conexão

### 📊 **Aba 7: Monitoramento (Prometheus + Grafana)**
- ✅ Acesso ao Prometheus UI
- ✅ Carregar métricas de performance
- ✅ Dashboard Grafana integrado
- ✅ Verificar saúde do Grafana
- ✅ Métricas da aplicação (JVM, threads, cache, etc.)
- ✅ Visualização de health checks

### ⚙️ **Aba 8: Avançado**
- ✅ Visualizar logs em tempo real (50/100/200 linhas)
- ✅ Reiniciar aplicação
- ✅ Rebuild de containers
- ✅ Limpeza de Docker
- ✅ Informações do sistema
- ✅ Testes de performance:
  - ⏱️ Teste de Latência
  - 📊 Teste de Throughput
  - 🔀 Teste de Concorrência

---

## 🚀 Como Usar

### 1. **Iniciar o Dashboard**

```powershell
cd c:\Users\perfe\Trabakho_Final_SD\ubiquitous-comunication-plataform\dashboard
npm start
```

Dashboard rodará em: **http://localhost:3333**

### 2. **Navegar pelas Abas**

Clique nos botões de aba no topo da página para alternar entre as funcionalidades.

### 3. **Fluxo Completo de Teste**

#### **Passo 1: Infraestrutura**
```
1. Abra "Infraestrutura"
2. Clique "🔄 Atualizar" para ver status dos containers
3. Todos devem estar "Up"
```

#### **Passo 2: Autenticação**
```
1. Abra "Autenticação"
2. Preencha Username: "testuser"
3. Preencha Password: "Teste123!"
4. Clique "📝 Registrar"
5. Clique "🔓 Login"
6. Copie o Token JWT
```

#### **Passo 3: Presença**
```
1. Abra "Presença"
2. Clique "🟢 Online"
3. Status muda para "Online"
4. Clique "🔄 Atualizar Lista" para ver outros usuários
```

#### **Passo 4: Mensageria**
```
1. Abra "Mensageria"
2. Clique "🔄 Gerar ID" para criar conversa
3. Escreva uma mensagem
4. Clique "📤 Enviar"
5. Conecte WebSocket: "🔌 Conectar WebSocket"
6. Mensagens aparecem no histórico
```

#### **Passo 5: Upload de Arquivo**
```
1. Abra "Armazenamento"
2. Preencha nome do arquivo e MIME type
3. Clique "1️⃣ Iniciar Upload"
4. Clique "2️⃣ Enviar Arquivo"
5. Clique "3️⃣ Completar"
6. Arquivo salvo no MinIO
```

#### **Passo 6: Telegram**
```
1. Abra "Telegram"
2. Cole Token do Bot e Chat ID
3. Clique "💾 Salvar Configuração"
4. Escreva mensagem
5. Clique "📤 Enviar"
```

#### **Passo 7: Monitoramento**
```
1. Abra "Monitoramento"
2. Clique "🌐 Prometheus UI" - abre em nova aba
3. Clique "🌐 Grafana UI" - abre em nova aba
4. Clique "🔄 Atualizar Métricas" para ver stats da app
```

---

## 🔗 Endpoints Disponíveis

### Docker
- `GET /api/docker/status` - Status dos containers
- `POST /api/docker/start` - Iniciar containers
- `POST /api/docker/stop` - Parar containers
- `POST /api/docker/rebuild` - Rebuild containers
- `POST /api/docker/prune` - Limpar Docker
- `GET /api/docker/logs?lines=50` - Carregar logs

### Autenticação
- `POST /api/platform/register` - Registrar usuário
- `POST /api/platform/login` - Fazer login
- `POST /api/platform/validate-token` - Validar token

### Mensageria
- `POST /api/platform/messages/send` - Enviar mensagem

### Presença
- `POST /api/presence/online` - Marcar como online
- `POST /api/presence/offline` - Marcar como offline
- `GET /api/presence/check/:userId` - Verificar presença
- `GET /api/presence/online-users` - Listar usuários online

### Upload
- `POST /api/platform/upload/initiate` - Iniciar upload
- `POST /api/platform/upload/file` - Enviar arquivo
- `POST /api/platform/upload/complete` - Completar upload
- `GET /api/storage/buckets` - Listar buckets

### Health Checks
- `GET /api/health/kafka` - Status Kafka
- `GET /api/health/redis` - Status Redis
- `GET /api/health/database` - Status PostgreSQL

### Métricas
- `GET /api/metrics/prometheus` - Métricas Prometheus
- `GET /api/metrics/grafana-health` - Saúde Grafana
- `GET /api/metrics/app` - Métricas da aplicação

### Testes
- `GET /api/test/ping` - Teste de latência
- `POST /api/test/throughput` - Teste de throughput
- `POST /api/test/concurrency` - Teste de concorrência

---

## 🎨 Interface

### Tema
- **Gradiente Roxo**: Background moderno
- **Cards Brancos**: Boa legibilidade
- **Cores Visuais**:
  - 🟢 Verde: Sucesso/Online/Rodando
  - 🔴 Vermelho: Erro/Offline/Parado
  - 🔵 Azul: Info/Ação

### Responsividade
- Desktop: Grid 2+ colunas
- Tablet: Grid 1-2 colunas
- Mobile: Grid 1 coluna

---

## 📋 Características Principais

### ✨ Realtime
- WebSocket para mensageria bidirecional
- Status de conexão em tempo real
- Histórico de mensagens atualizado
- Logs em tempo real

### 🔒 Segurança
- JWT tokens para autenticação
- Bearer token em requisições
- Validação de token

### 📊 Observabilidade
- Logs do Docker
- Métricas Prometheus
- Dashboards Grafana
- Health checks de todos serviços

### ⚡ Performance
- API assíncrona
- Testes de latência
- Testes de throughput
- Testes de concorrência

### 🔄 Escalabilidade
- Suporta múltiplos usuários
- Presença distribuída (Redis)
- Mensageria via Kafka
- Object storage (MinIO)

---

## 🐛 Troubleshooting

### Dashboard não carrega
```powershell
# Verifique se está rodando
netstat -ano | findstr :3333

# Reinicie
taskkill /F /IM node.exe
npm start
```

### Login falha
```
Verifique se:
1. Platform Core está rodando (docker ps)
2. PostgreSQL está conectado
3. Dados do usuário estão corretos
```

### WebSocket não conecta
```
Verifique se:
1. API está rodando em http://localhost:8080
2. Porta 8080 não está bloqueada
3. Firewalls não bloqueiam WebSocket
```

### Upload falha
```
Verifique se:
1. MinIO está rodando
2. Bucket 'ubiquitous-attachments' existe
3. Token JWT é válido
```

---

## 📚 Recursos Adicionais

### URLs Importantes
- **Dashboard**: http://localhost:3333
- **API**: http://localhost:8080
- **MinIO Console**: http://localhost:9001 (minioadmin/minioadmin123)
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### Arquivos Relacionados
- Frontend: `dashboard/public/index-complete.html`
- Backend: `dashboard/server-complete.js`
- Config: `dashboard/package.json`

### Próximos Passos
1. ✅ Todas as funcionalidades implementadas
2. ⏭️ Fazer commit no Git
3. ⏭️ Atualizar README principal

---

## 🎉 Sucesso!

O dashboard agora oferece **controle completo** sobre a plataforma com interface visual intuitiva!

**Tudo funcionando? Faça o commit:**

```bash
git add dashboard/
git commit -m "feat: dashboard completo com todas as funcionalidades

- 8 abas principais para gerenciar plataforma
- Infraestrutura, autenticação, mensageria, presença
- Upload de arquivos, Telegram, monitoramento
- Testes de performance e operações avançadas"
git push origin master
```
