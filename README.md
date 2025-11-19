# 🚀 Plataforma de Comunicação Ubíqua

## Visão Geral 

Este projeto é uma **Plataforma de Mensageria Distribuída** construída para demonstrar princípios de arquitetura moderna, escalabilidade horizontal e comunicação assíncrona/síncrona (Hybrid Architecture).

O sistema suporta:
* **Comunicação Bidirecional:** Receber e enviar mensagens para canais externos (Telegram).
* **Tempo Real:** Entrega via WebSocket e controle de presença (`ONLINE`/`OFFLINE`).
* **Segurança:** Autenticação via JWT (JSON Web Tokens).
* **Observabilidade:** Monitoramento de saúde e performance via Prometheus e Grafana.

---

#1. Arquitetura Distribuida

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


## 2. Configuração e Execução( Getting Started)

## Pré requisitos

Docker e Docker Compose Instalados e em execução

* Docker e Docker Compose instalados e em execução.
* Java 21 ou superior para compilar o projeto.

# 1. Clonar o repositório
git clone SEU_REPOSITORIO_AQUI/ubiquitous-comunication-plataform.git

Navegar para a pasta raiz (onde está o docker-compose.yml)
cd ubiquitous-comunication-plataform/

### Inicialização

O projeto é iniciado com um único comando que constrói a aplicação Java e sobe toda a infraestrutura (DB, Kafka, MinIO, Prometheus, Grafana).

1.  **Na pasta raiz do projeto (`ubiquitous-comunication-plataform`), execute:**
    ```bash
    docker-compose up -d --build
    ```
2.  **Verifique a Saúde:** Após ~60 segundos, todos os contêineres devem estar rodados (`docker ps`).
3.  **Logs:** Monitore a aplicação Java: `docker logs -f platform_core_app`


### Observabilidade e Monitoramento
Você pode acessar os dashboards de monitoramento para ver a saúde do sistema:
* **Prometheus UI:** `http://localhost:9090/targets`
* **Grafana UI:** `http://localhost:3000` (Login: `admin` / `admin`)

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

### C. Download de Histórico

* **Endpoint:** `GET /api/v1/conversations/{id}/messages`
* **Ação:** Use o token JWT e o ID da conversa. Você receberá a lista de mensagens paginadas do PostgreSQL.

---

## 4. CI/CD

* **Tarefa 4.3 - CI/CD:** Implementação de pipeline de entrega contínua via GitHub Actions, configurando o build e o push da imagem Docker para o Docker Hub.


(Para que o passo de Login no Docker Hub funcione, você precisa configurar duas Secrets no seu repositório GitHub (Settings -> Secrets -> Actions) )

|Nome de Secret | Status Esperado |
| :--- | :--- |
| DOCKER_USERNAME  | Seu nome de user do docker Hub.|
| DOCKER_PASSWORD  | Token do Docker Hub |

