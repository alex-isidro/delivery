# 🍽️ Delivery System

Projeto de Microsserviços para Delivery desenvolvido para a disciplina de **Java Advanced** (Projeto Diamante 02 — FIAP).

---


### 📂 Estrutura de Diretórios e Pacotes

```text
delivery
├── eureka-server
│   └── src
│       ├── main
│       │   ├── java/br/com/fiap/delivery/eureka
│       │   │   └── EurekaServerApplication.java    # Servidor de Service Registry (Netflix Eureka)
│       │   └── resources
│       │       └── application.properties          # Porta 8761 e configurações do Eureka
│       └── test
│
├── order-service
│   └── src
│       ├── main
│       │   ├── java/br/com/fiap/delivery/order
│       │   │   ├── config                          # Carga inicial de pratos e configurações RabbitMQ
│       │   │   │   ├── DataLoader.java
│       │   │   │   ├── RabbitConfig.java
│       │   │   │   └── RabbitMessageConfig.java
│       │   │   ├── controller                      # Endpoints REST e tratamento de exceções
│       │   │   │   ├── ApiExceptionHandler.java
│       │   │   │   └── OrderController.java
│       │   │   ├── dto                             # DTOs com validação (Bean Validation)
│       │   │   │   ├── AssistantRequest.java
│       │   │   │   ├── OrderRequest.java
│       │   │   │   └── ReviewRequest.java
│       │   │   ├── entity                          # Entidades de Pedido e Prato
│       │   │   │   ├── CustomerOrder.java
│       │   │   │   └── Dish.java
│       │   │   ├── repository                      # Repositórios JPA com lock pessimista (PESSIMISTIC_WRITE)
│       │   │   │   ├── CustomerOrderRepository.java
│       │   │   │   └── DishRepository.java
│       │   │   ├── service                         # Regras de negócio, cliente com retry, IA e mensageria
│       │   │   │   ├── ChatService.java
│       │   │   │   ├── OrderService.java
│       │   │   │   ├── PaymentClient.java
│       │   │   │   └── ReviewPublisher.java
│       │   │   └── OrderServiceApplication.java    # Classe principal com @EnableResilientMethods e RestTemplate balanceado
│       │   └── resources
│       │       └── application.properties          # Porta 8080, Eureka, banco H2, RabbitMQ e Spring AI
│       └── test
│
├── payment-service
│   └── src
│       ├── main
│       │   ├── java/br/com/fiap/delivery/payment
│       │   │   ├── PaymentController.java          # Endpoint de pagamento com simulação de instabilidade
│       │   │   └── PaymentServiceApplication.java  # Microsserviço de pagamento
│       │   └── resources
│       │       └── application.properties          # Porta dinâmica (${SERVER_PORT:8081}) e Eureka
│       └── test
│
├── review-service
│   └── src
│       ├── main
│       │   ├── java/br/com/fiap/delivery/review
│       │   │   ├── RabbitConfig.java               # Declaração da fila, exchange e routing key
│       │   │   ├── RabbitMessageConfig.java        # Serialização/Deserialização de mensagens JSON
│       │   │   ├── ReviewConsumer.java             # Consumo via RabbitMQ, buffer em memória e flush agendado (@Scheduled)
│       │   │   ├── ReviewController.java           # Endpoint para consulta do ranking consolidado
│       │   │   ├── ReviewServiceApplication.java   # Microsserviço de avaliações
│       │   │   ├── ReviewSummary.java              # Entidade consolidada (totalRating, count)
│       │   │   └── ReviewSummaryRepository.java    # Repositório de resumos de avaliações
│       │   └── resources
│       │       └── application.properties          # Porta 8083, Eureka, RabbitMQ e banco H2
│       └── test
│
└── docker-compose.yml                              # Configuração do broker RabbitMQ (rabbitmq:4-management)
```

---

## 🏗️ Visão Geral dos Microsserviços

| Serviço | Porta Padrão | Descrição |
|---|---|---|
| **`eureka-server`** | `8761` | Servidor de registro e descoberta de serviços (Netflix Eureka). |
| **`order-service`** | `8080` | Gerenciamento de cardápio, criação e consulta de pedidos, publicação de avaliações e assistente com IA. |
| **`payment-service`** (Instância 1) | `8081` | Processamento e validação de pagamentos com simulação de instabilidade. |
| **`payment-service`** (Instância 2) | `8082` | Segunda instância para demonstração de Load Balancing e tolerância a falhas. |
| **`review-service`** | `8083` | Consumo assíncrono de avaliações via RabbitMQ com buffer em memória e consolidação de ranking. |

---

## 🧩 Principais Recursos e Tecnologias

- **Service Discovery**: Netflix Eureka (`eureka-server`) para resolução dinâmica dos serviços.
- **Client-Side Load Balancing**: `@LoadBalanced RestTemplate` distribuindo requisições entre instâncias do `payment-service`.
- **Tolerância a Falhas e Resiliência**: `@Retryable` com backoff exponencial e jitter para chamadas ao serviço de pagamento.
- **Controle de Concorrência**: Bloqueio pessimista (`PESSIMISTIC_WRITE`) no `DishRepository` para evitar inconsistências de estoque (*race condition*).
- **Mensageria Assíncrona**: RabbitMQ (Exchange `reviews.exchange`, Queue `reviews.queue`, Routing Key `reviews.key`) para desacoplamento de avaliações.
- **Processamento em Buffer**: `ReviewConsumer` acumula avaliações em `ConcurrentHashMap` e realiza *flush* periódico agendado (`@Scheduled`) no banco H2.
- **Assistente Virtual com IA**: Integração com Spring AI (`spring-ai-starter-model-openai`) conectada à API compatível (Groq/OpenAI) com *system prompt* contextualizado pelo cardápio atual.
- **Persistência em Memória**: Banco de dados H2 isolado em cada serviço aplicável.

---

## 📋 Pré-requisitos

- **Java JDK 25**
- **Docker** e **Docker Compose**
- **API Key da IA (Groq)**: A chave de API utilizada pela inteligência artificial foi obtida através da plataforma [Groq (GroqCloud)](https://console.groq.com/keys). O Spring AI utiliza o endpoint compatível da Groq (`https://api.groq.com/openai/v1`) com o modelo configurado no `order-service`.

---

## 🚀 Como Executar o Projeto

### 1. Iniciar o RabbitMQ

Execute o container do RabbitMQ via Docker Compose na raiz do projeto:

```bash
docker compose up -d
```

### 2. Configurar a Variável de Ambiente da IA (Groq)

Obtenha sua API Key em [GroqCloud Console](https://console.groq.com/keys) e defina a variável `OPENAI_API_KEY` no terminal onde o `order-service` será executado:

- **Windows (PowerShell):**
  ```powershell
  $env:OPENAI_API_KEY="gsk_sua_api_key_aqui"
  ```
- **Linux / macOS:**
  ```bash
  export OPENAI_API_KEY="gsk_sua_api_key_aqui"
  ```

---

### 3. Iniciar os Serviços (Ordem Recomendada)

Abra terminais separados para cada microsserviço e execute na seguinte ordem:

#### 1º — Eureka Server
```powershell
cd eureka-server
.\gradlew.bat bootRun
```

#### 2º — Payment Service (Instância 1 - Porta 8081)
```powershell
cd payment-service
.\gradlew.bat bootRun
```

#### 3º — Payment Service (Instância 2 - Porta 8082)
```powershell
cd payment-service
.\gradlew.bat bootRun --args="--server.port=8082"
```

#### 4º — Review Service (Porta 8083)
```powershell
cd review-service
.\gradlew.bat bootRun
```

#### 5º — Order Service (Porta 8080)
```powershell
cd order-service
.\gradlew.bat bootRun
```

---

## 🔌 Guia de Endpoints da API

### 📦 Order Service (`http://localhost:8080`)

#### 1. Listar Pratos do Cardápio
- **Método:** `GET`
- **Endpoint:** `/dishes`
- **Exemplo de Resposta:**
  ```json
  [
    {
      "id": 1,
      "name": "House Burger",
      "description": "Brioche bun, beef and cheese",
      "price": 39.90,
      "stock": 10
    },
    {
      "id": 2,
      "name": "Veggie Burger",
      "description": "Vegetable patty and fresh salad",
      "price": 35.90,
      "stock": 20
    }
  ]
  ```

#### 2. Consultar Prato por ID
- **Método:** `GET`
- **Endpoint:** `/dishes/1`

#### 3. Criar Pedido
- **Método:** `POST`
- **Endpoint:** `/orders`
- **Headers:** `Content-Type: application/json`
- **Payload:**
  ```json
  {
    "dishId": 1,
    "quantity": 2
  }
  ```
- **Resposta (`201 Created`):**
  ```json
  {
    "id": 1,
    "dishId": 1,
    "quantity": 2,
    "totalPrice": 79.80,
    "status": "CONFIRMED",
    "createdAt": "2026-10-07T03:50:00"
  }
  ```

#### 4. Consultar Pedido por ID
- **Método:** `GET`
- **Endpoint:** `/orders/1`

#### 5. Enviar Avaliação de um Prato
- **Método:** `POST`
- **Endpoint:** `/reviews`
- **Headers:** `Content-Type: application/json`
- **Payload:**
  ```json
  {
    "dishId": 1,
    "rating": 5,
    "comment": "Hambúrguer excelente, chegou quentinho!"
  }
  ```
- **Resposta:** `202 Accepted`

#### 6. Conversar com o Assistente Virtual (IA)
- **Método:** `POST`
- **Endpoint:** `/assistant`
- **Headers:** `Content-Type: application/json`
- **Payload:**
  ```json
  {
    "question": "Vocês têm alguma opção vegetariana no cardápio?"
  }
  ```
- **Exemplo de Resposta:**
  ```json
  {
    "answer": "Sim! Temos o Veggie Burger com hambúrguer vegetal e salada fresca por R$ 35,90 (20 unidades disponíveis)."
  }
  ```

---

## 👥 Integrantes da Equipe

| Nome | RM | Turma | GitHub | LinkedIn |
|---|---|---|---|---|
| **Alexander Dennis Isidro Mamani** | 565554 | 2TDSPG | [@alex-isidro](https://github.com/alex-isidro) | [LinkedIn](https://www.linkedin.com/in/alexander-dennis-a3b48824/) |
| **Kelson Zhang** | 563748 | 2TDSPG | [@KelsonZh0](https://github.com/KelsonZh0) | [LinkedIn](https://www.linkedin.com/in/kelson-zhang-211456323/) |

---