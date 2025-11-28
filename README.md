# 🚀 Plataforma de Comunicação Ubíqua

## Visão Geral 

Este projeto é uma **Plataforma de Mensageria Distribuída** construída para demonstrar princípios de arquitetura moderna, escalabilidade horizontal e comunicação assíncrona/síncrona (Hybrid Architecture).

O sistema suporta:
* **Comunicação Bidirecional:** Receber e enviar mensagens(ex: Telegram, Instagram, WhatsApp).
* **Tempo Real:** Entrega via WebSocket e controle de presença (`ONLINE`/`OFFLINE`).
* **Segurança:** Autenticação via JWT (JSON Web Tokens).
* **Observabilidade:** Observabilidade Completa com Prometheus + Grafana + métricas customizadas.
* **Entrega em Tempo**  Real via WebSocket.
* **Gerenciamento de Presença** (ONLINE / OFFLINE) via Redis + gRPC.
* **Arquitetura Assíncrona**  com Kafka para resiliência e desacoplamento.
* **Upload Multipart para S3/MinIO.**
* **Documentação via OpenAPI**

---

### 1. Arquitetura Distribuida

A plataforma utiliza um modelo de Arquitetura Orientada a Eventos (EDA) para garantir a resiliência e o desacoplamento dos serviços.


### Componentes Chave
| Componente | Função | Tecnologias |
| :--- | :--- | :--- |
| **API Gateway** | Ingestão de mensagens, Autenticação, Orquestração de Upload. | Spring Boot, JWT, PostgreSQL |
| **Message Broker** | Núcleo Assíncrono. Garante que as mensagens não se percam. | Apache Kafka |
| **Router Worker** | Consome mensagens do Kafka, verifica a presença via gRPC e roteia a mensagem para o tópico de destino. | Spring Boot, gRPC Client |
| **Presence Service** | Armazenamento de estado de presença (`ONLINE`/`OFFLINE`). | **Redis** (Consultado via gRPC) |
| **Object Storage** | Armazenamento de arquivos grandes (fotos, vídeos). | **MinIO** (S3-Compatível) |
| **Real-time Handler** | Entrega mensagens para o cliente Web via conexão WebSocket. | Spring WebSocket |
| **Connector Mocks** |Simuladores de canais externos (WhatsApp / Instagram)| python |

## 2. Configuração e Execução( Getting Started)

## Pré requisitos

*Docker e Docker Compose instalados
*Java 21+ para build local da plataforma
*PowerShell ou Bash para testes de upload

### 1. Clonar o repositório
git clone SEU_REPOSITORIO_AQUI/ubiquitous-comunication-plataform.git

### 2. Subir toda a infraestrutura

O projeto é iniciado com um único comando que constrói a aplicação Java e sobe toda a infraestrutura (DB, Kafka, MinIO, Prometheus, Grafana).

1.  **Na pasta raiz do projeto (`ubiquitous-comunication-plataform`), execute:**
    ```bash
    docker-compose up -d --build
    ```
2.  **Verifique a Saúde:** Após ~60 segundos, todos os contêineres devem estar rodados (`docker ps`).
3.  **Logs:** Monitore a aplicação Java: `docker logs -f platform_core_app`

---

## 3. Uso da API e Teste

### A. Autenticação JWT (Mandatório)

Todos os endpoints da API são protegidos. O primeiro passo é obter um token.

| Endpoint | Ação | Status Esperado |
| :--- | :--- | :--- |
| `POST /api/v1/auth/register` | Criar um novo usuário (Ex: username: `tester`, password: `123`) | `201 Created` |
| `POST /api/v1/auth/login` | Logar com o novo usuário. | `200 OK` + **Token JWT** |

### B. Teste de Mensageria (Fluxo gRPC e WebSocket)

Este teste valida a **Arquitetura v3 (gRPC + Redis)**.

1.  **Conectar e Ficar Online:** Abra o PieSocket (`ws://localhost:8080/ws/connect`). Isso marca seu usuário como `ONLINE` no Redis (via `PresenceService`).

2.  **Enviar a Mensagem (com TOKEN):** Use o token de login para enviar.

    ```bash
    # Use o ID da conversa e o token que você gerou
    curl -X POST http://localhost:8080/api/v1/messages \
         -H "Content-Type: application/json" \
         -H "Authorization: Bearer SEU_TOKEN_JWT" \
         -d '{
               "messageId": "UUID-VÁLIDO-AQUI",
               "conversationId": "UUID-DA-SUA-CONVERSA",
               "payload": {"type": "text", "text": "gRPC check OK!"}
             }'
    ```

3.  **Verificação no Log:** Se a arquitetura funcionar, você verá esta prova da comunicação gRPC:
    `INFO [...] gRPC Check: User [UUID] está online? true`
    `INFO [...] Status da Msg ... atualizado para DELIVERED`
    A mensagem **aparecerá instantaneamente** no seu PieSocket.

### C. Upload de Arquivos para S3 (MinIO)

Este fluxo demonstra o upload multipart de arquivos para o object storage MinIO (compatível com S3).

#### PowerShell - Fluxo Completo

```powershell
# 1. FAZER LOGIN E OBTER TOKEN
$loginResponse = curl.exe -X POST http://localhost:8080/api/v1/auth/login `
    -H "Content-Type: application/json" `
    -d '{\"username\":\"tester\",\"password\":\"123\"}' | ConvertFrom-Json

$token = $loginResponse.token

# 2. INICIAR UPLOAD
$body1 = @{
    filename = "teste-final.txt"
    mimeType = "text/plain"
    fileSize = 1024
} | ConvertTo-Json

$response1 = Invoke-WebRequest `
    -Uri "http://localhost:8080/api/v1/uploads/initiate" `
    -Method POST `
    -Headers @{"Content-Type"="application/json"; "Authorization"="Bearer $token"} `
    -Body $body1

$uploadData = $response1.Content | ConvertFrom-Json

# 3. FAZER UPLOAD DO ARQUIVO
$presignedUrl = $uploadData.presignedUrls[0]
$fileContent = "Este e um teste de upload para o MinIO!"

$response2 = Invoke-WebRequest -Uri $presignedUrl -Method PUT -Body $fileContent
$eTag = $response2.Headers.ETag -replace '"', ''

# 4. COMPLETAR UPLOAD
$attachmentId = $uploadData.attachmentId

$body3 = @{
    parts = @(
        @{
            partNumber = 1
            eTag = $eTag
        }
    )
} | ConvertTo-Json -Depth 3

$response3 = Invoke-WebRequest `
    -Uri "http://localhost:8080/api/v1/uploads/$attachmentId/complete" `
    -Method POST `
    -Headers @{"Content-Type"="application/json"; "Authorization"="Bearer $token"} `
    -Body $body3

Write-Host "Upload completo! Status: $($response3.StatusCode)"
```

#### Bash/cURL - Fluxo Completo

```bash
# 1. FAZER LOGIN E OBTER TOKEN
TOKEN=$(curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"tester","password":"123"}' | jq -r '.token')

# 2. INICIAR UPLOAD
UPLOAD_DATA=$(curl -X POST http://localhost:8080/api/v1/uploads/initiate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"filename":"teste-final.txt","mimeType":"text/plain","fileSize":1024}')

ATTACHMENT_ID=$(echo $UPLOAD_DATA | jq -r '.attachmentId')
PRESIGNED_URL=$(echo $UPLOAD_DATA | jq -r '.presignedUrls[0]')

# 3. FAZER UPLOAD DO ARQUIVO
ETAG=$(curl -X PUT "$PRESIGNED_URL" \
  -H "Content-Type: text/plain" \
  -d "Este e um teste de upload para o MinIO!" \
  -i | grep -i etag | awk '{print $2}' | tr -d '\r"')

# 4. COMPLETAR UPLOAD
curl -X POST "http://localhost:8080/api/v1/uploads/$ATTACHMENT_ID/complete" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"parts\":[{\"partNumber\":1,\"eTag\":\"$ETAG\"}]}"

#5. REALIZAR O DAWLOADS
curl -X GET "http://localhost:8080/api/v1/uploads/{ATTACHMENT_ID}/download"
```

#### Verificar no MinIO Console

Após o upload, você pode visualizar o arquivo:
* **MinIO Console:** `http://localhost:9001`
* **Login:** `minioadmin` / `minioadmin123`
* **Bucket:** `ubiquitous-attachments`

### D. Download de Histórico

* **Endpoint:** `GET /api/v1/conversations/{id}/messages`
* **Ação:** Use o token JWT e o ID da conversa. Você receberá a lista de mensagens paginadas do PostgreSQL.

---

Abaixo está **um passo a passo claro, profissional e direto** para colocar no seu README, explicando como rodar os containers dos conectores (Instagram/WhatsApp) e como enviar a requisição de teste usando `Invoke-WebRequest` no PowerShell.

Você pode copiar e colar exatamente como está.

---

## 4. Connectores Instagram / WhatsApp
Este guia explica como rodar os containers mockados dos conectores e testar o fluxo enviando uma mensagem simulada.

---

##  **1. Certifique-se que o Kafka está rodando**

Se estiver usando Docker Compose:

```powershell
docker-compose up -d kafka zookeeper
```

---

##  **2. Suba os containers dos connectors**

Eles devem estar rodando antes de enviar as mensagens.

### Conector Instagram

```powershell
docker-compose up -d connector_instagram_mock
```

### Conector WhatsApp

```powershell
docker-compose up -d connector_whatsapp_mock
```

Verifique se subiram:

```powershell
docker ps
```

---

##  **3. Enviar mensagens simuladas via HTTP**

Cada conector mock expõe um endpoint HTTP que simula o envio de uma mensagem para a plataforma.

###  Instagram Mock — Porta **3002**

Exemplo usando PowerShell:

```powershell
Invoke-WebRequest `
  -Uri "http://localhost:3002/send_message" `
  -Method POST `
  -Headers @{ "Content-Type" = "application/json" } `
  -Body '{"message": "Hello from Instagram"}'
```

---

###  WhatsApp Mock — Porta **3001**

```powershell
Invoke-WebRequest `
  -Uri "http://localhost:3001/send_message" `
  -Method POST `
  -Headers @{ "Content-Type" = "application/json" } `
  -Body '{"message": "Hello from WhatsApp"}'
```

---

##  **4. O que deve acontecer**

Quando você chamar `/send_message`, o fluxo será:

```
HTTP → Connector Mock → Kafka (msg_*_outbound) → Worker → Kafka → Connector → Kafka (status_updates) → Core App
```

Nos logs do container, você verá:

* Conector recebendo a mensagem simulada
* Worker processando
* Status "DELIVERED" e "READ" sendo enviados
* Callbacks GRPC retornando para o servidor

---

##  **5. Logs importantes**

###  Ver logs do Instagram:

```powershell
docker logs connector_instagram_mock -f
```

###  Ver logs do WhatsApp:

```powershell
docker logs connector_whatsapp_mock -f
```

---

##  Observações importantes

* Os containers dos conectores **precisam estar rodando** antes de executar o `Invoke-WebRequest`.
* As portas devem corresponder às definidas no projeto (`3002` para Instagram, `3001` para WhatsApp).
* Esse endpoint é apenas para **simulação de mensagem INBOUND** (vinda da rede social para a plataforma).
* Mensagens OUTBOUND (da plataforma para o conector) fluem via Kafka apenas.

---

## 5. CI/CD

* **Tarefa 4.3 - CI/CD:** Implementação de pipeline de entrega contínua via GitHub Actions, configurando o build e o push da imagem Docker para o Docker Hub.


(Para que o passo de Login no Docker Hub funcione, você precisa configurar duas Secrets no seu repositório GitHub (Settings -> Secrets -> Actions) )

|Nome de Secret | Status Esperado |
| :--- | :--- |
| DOCKER_USERNAME  | Seu nome de user do docker Hub.|
| DOCKER_PASSWORD  | Token do Docker Hub |

---

## 6. Observabilidade e Monitoramento

Você pode acessar os dashboards de monitoramento para ver a saúde do sistema:
* **Prometheus UI:** `http://localhost:9090/targets`
* **Grafana UI:** `http://localhost:3000` (Login: `admin` / `admin`)

## 7. Documentação OpenAPI
* Vc poderar consultar a documentação acessando **http://localhost:8080/swagger-ui/index.html**


