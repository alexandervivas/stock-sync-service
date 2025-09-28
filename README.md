# Stock Sync (multi‑module) + Event Logger

Service to **synchronize inventory** from two vendors and emit **out‑of‑stock events** to a separate **Event Logger** microservice via RabbitMQ.

* **Vendor A**: JSON API.
* **Vendor B**: writes a **CSV** to a shared volume.
* **Stock Service**: consolidates inventory and detects `> 0 → 0` transitions.
* **Event Logger**: consumes out‑of‑stock events and logs them (console for now; ready for in‑memory/db later).

---

## Modules

```
modules/
├─ stock-service/   # Main microservice (CQRS + Ports & Adapters)
├─ vendor-a/        # Mock JSON API (GET /products)
├─ vendor-b/        # CSV writer to /data/stock.csv (@Scheduled job)
└─ event-logger/    # Event consumer (RabbitMQ) → logs to console
```

---

## Architecture

* **Hexagonal / Ports & Adapters**

    * Domain defines ports (`ProductRepository`, `VendorAClient`, `VendorBReader`, `EventLogger`).
    * Infrastructure implements adapters (JPA, HTTP, Filesystem/CSV, AMQP).
* **CQRS (reads)**

    * `application/queries/ListProductsQueryHandler` powers `GET /products`.
* **Eventing**

    * `StockSyncService` emits `OutOfStockEvent` via **RabbitMQ** (topic exchange `stock.events`, routing key `out-of-stock`).
    * `event-logger` declares queue `stock.events.out-of-stock`, binds it, and consumes events.
* **Persistence**

    * H2 for dev/tests (table `products`, unique `(sku, vendor)`).

### Event payload (JSON)

```jsonc
// OutOfStockEvent
{
  "sku": "XYZ456",
  "vendor": "VendorB",
  "previousQty": 5,
  "newQty": 0,
  "occurredAt": "2025-09-28T20:13:42Z" // ISO‑8601
}
```

### Flow

```
Vendor A ---->            
            \            
             \          +---------------------+
Vendor B --->  ---> Stock Service --AMQP-->  RabbitMQ (exchange: stock.events)
                                            | routing: out-of-stock       |
                                            +---------------+-------------+
                                                            |
                                                            v
                                                   Event Logger (consumer)
```

---

## Tech Stack

* Java 21, Gradle 8.14.x
* Spring Boot 3.5.x (Web, Data JPA, Scheduling, Retry, AOP, AMQP)
* H2 (dev/test), springdoc‑openapi (Swagger UI)
* RabbitMQ 3.x (dockerized, with management UI)
* JUnit 5, AssertJ, Mockito

---

## Requirements

* **JDK 21** for Gradle/Boot (avoid JDK 25 for now).
* Docker 24+ and Docker Compose v2.
* (Optional) asdf/SDKMAN to manage JDKs.

> With `asdf`, pin Java 21 at repo root:
>
> ```bash
> asdf install java openjdk-21
> asdf local java openjdk-21
> ```

---

## Build & Test

From the **repo root**:

```bash
./gradlew clean build
# or a single module
./gradlew :modules:stock-service:test
```

---

## Local run (without Docker)

Two options:

1. **Simplest**: keep eventing off and log to console in `stock-service`.

   ```yaml
   # modules/stock-service/src/main/resources/application.yml
   events:
     sink: console  # no RabbitMQ required
   ```

   Then run vendors + stock-service as before (5s/7s crons) — see below.

2. **With RabbitMQ locally**: run a local RabbitMQ or use Docker only for Rabbit; set `events.sink=rabbit` and `spring.rabbitmq.*` to your broker.

### Default local configs (no Docker)

* `vendor-a` on **8081**
* `vendor-b` writes `./data/stock.csv` every **5s** (alternating 5 ↔ 0)
* `stock-service` reads `./data/stock.csv` and syncs every **7s**

`modules/vendor-b/src/main/resources/application.yml`:

```yaml
vendorb:
  csvPath: ./data/stock.csv
  schedule: "*/5 * * * * *"
  generateSample: true
```

`modules/stock-service/src/main/resources/application.yml`:

```yaml
ingestion:
  vendorA:
    baseUrl: http://localhost:8081
  vendorB:
    csvPath: ./data/stock.csv
  sync:
    cron: "*/7 * * * * *"
springdoc:
  swagger-ui:
    path: /swagger-ui.html
logging:
  level:
    com.upwork.stock: INFO

# Event sink (choose one)
# events:
#   sink: console  # ← default for local without Rabbit
#   # sink: rabbit
#
# spring:
#   rabbitmq:
#     host: localhost
#     port: 5672
#     username: guest
#     password: guest
#
# events:
#   broker:
#     exchange: stock.events
#     routingKey: out-of-stock
```

### Start locally

```bash
# Terminal A
./gradlew :modules:vendor-a:bootRun

# Terminal B
./gradlew :modules:vendor-b:bootRun

# Terminal C (console sink or Rabbit configured)
./gradlew :modules:stock-service:bootRun
```

### Try it

```bash
# Vendor A (mock JSON)
curl -s http://localhost:8081/products | jq .

# Consolidated inventory
curl -s http://localhost:8080/products | jq .

# Swagger UI (stock-service)
open http://localhost:8080/swagger-ui.html
```

Out‑of‑stock transition log appears when `VendorB` flips 5→0 and a sync runs.

---

## Docker & Compose (recommended)

### Dockerfiles

Located under `deploy/docker/` using Gradle official image for build stage:

* `deploy/docker/stock-service.Dockerfile`
* `deploy/docker/vendor-a.Dockerfile`
* `deploy/docker/vendor-b.Dockerfile`
* `deploy/docker/event-logger.Dockerfile`

> **Spring relaxed binding** examples:
>
> * `INGESTION_VENDORA_BASEURL` → `ingestion.vendorA.baseUrl`
> * `INGESTION_VENDORB_CSVPATH` → `ingestion.vendorB.csvPath`
> * `INGESTION_SYNC_CRON` → `ingestion.sync.cron`
> * `EVENTS_*` (stock-service) → `events.broker.*` & `events.sink`
> * `VENDORB_*` (vendor-b) → `vendorb.*`

### Commands

```bash
docker compose build
docker compose up -d

# Logs
docker compose logs -f vendor-b
docker compose logs -f stock-service
docker compose logs -f event-logger

# Try
curl -s http://localhost:8080/products | jq .
open http://localhost:8080/swagger-ui.html
open http://localhost:15672  # Rabbit UI (guest/guest)
```

---

## Endpoints

* **stock-service**

    * `GET /products` → List of `{ id, sku, name, stockQuantity, vendor }`.
    * Swagger UI → `/swagger-ui.html`; OpenAPI → `/v3/api-docs(.yaml)`.
* **vendor-a**

    * `GET /products` (mock JSON) → used by stock-service.
* **event-logger**

    * (No HTTP yet) — consumes from Rabbit and logs to console.

---

## Relevant structure

```
modules/stock-service/src/main/java/com/upwork/stock/
├─ api/ProductsController.java
├─ application/
│  ├─ dto/ExternalProductDto.java
│  ├─ ports/{VendorAClient,VendorBReader,EventLogger}.java
│  ├─ services/StockSyncService.java
│  └─ queries/{ListProductsQuery, ListProductsQueryHandler}.java
├─ config/{Config.java, OpenApiConfig.java, StockIngestionProperties.java, RabbitConfig.java}
├─ domain/product/{Product.java, ProductRepository.java}
└─ infrastructure/
   ├─ fs/CsvVendorBReader.java
   ├─ http/HttpVendorAClient.java
   └─ persistence/jpa/{JpaProductRepository.java, SpringDataProductRepository.java}

modules/event-logger/src/main/java/com/upwork/eventlogger/
├─ EventLoggerApplication.java
├─ config/RabbitConfig.java
└─ consumers/OutOfStockListener.java
```

---

## Testing notes

* Prefer `@MockitoBean` over deprecated `@MockBean` (Boot 3.4+).
* Repo tests: `@DataJpaTest` + H2.
* Web tests: `@WebMvcTest` + record DTOs.
* `StockSyncServiceTest`: mock `VendorAClient`, `VendorBReader`, `ProductRepository`, pass a dummy `StockIngestionProperties`, and verify `EventLogger.outOfStock(...)` once on transition.

---

## Troubleshooting

* **No OUT‑OF‑STOCK log**: ensure staggered crons (e.g., 5s/7s), shared CSV path, and proper log level. Restart `vendor-b` to reset alternation.
* **Rabbit consumer fails to deserialize `Instant`**: ensure `event-logger` has `spring-boot-starter-json` and both producer/consumer use `Jackson2JsonMessageConverter(ObjectMapper)` (Boot’s mapper includes `JavaTimeModule`).
* **Gradle/Wrapper in Docker**: build stage uses `gradle:8.14.3-jdk21` → wrapper not required in container.
* **JDK mismatch**: run builds/tests with JDK 21.

---

## Roadmap (optional)

* Expose `GET /events` in `event-logger` (in‑memory list or DB persistence).
* Persist events in a DB (H2/Postgres) with time window queries.
* Pagination/filters in `GET /products`.
* Metrics & health with Actuator.
