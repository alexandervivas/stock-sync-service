# Stock Sync (multi‑module)

Service to **synchronize inventory** from two vendors:

* **Vendor A**: JSON API.
* **Vendor B**: **CSV** file written to a shared volume.

Exposes `GET /products` with consolidated stock and logs the **`> 0 → 0`** transition (out‑of‑stock).

---

## Modules

```
modules/
├─ stock-service/   # Main microservice (CQRS + Ports & Adapters)
├─ vendor-a/        # Mock JSON API (GET /products)
└─ vendor-b/        # CSV writer to /data/stock.csv (@Scheduled job)
```

---

## Architecture

* **Hexagonal / Ports & Adapters**

    * Domain defines ports (`ProductRepository`, `VendorAClient`, `VendorBReader`).
    * Infrastructure implements adapters (JPA, HTTP, Filesystem/CSV).
* **CQRS (reads)**

    * `application/queries/ListProductsQueryHandler` powers `GET /products`.
* **Persistence**

    * H2 for dev/tests (table `products`, unique constraint `(sku, vendor)`).
* **Synchronization**

    * `StockSyncService` pulls from Vendor A & B, performs **upsert** by `(sku, vendor)`, and logs `>0 → 0` transitions.

---

## Tech Stack

* Java 21, Gradle 8.14.x
* Spring Boot 3.5.x (Web, Data JPA, Scheduling, Retry, AOP)
* H2 (dev/test), springdoc‑openapi (Swagger UI)
* JUnit 5, AssertJ, Mockito (`@MockitoBean`)
* Docker & Docker Compose

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

## 🧪 Build & Test

From the **repo root**:

```bash
./gradlew clean build
# or a single module
./gradlew :modules:stock-service:test
```

---

## ▶️ Local run (no Docker)

### 1) Default configs

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
```

### 2) Start

```bash
# Terminal A
./gradlew :modules:vendor-a:bootRun

# Terminal B
./gradlew :modules:vendor-b:bootRun

# Terminal C
./gradlew :modules:stock-service:bootRun
```

### 3) Try it

```bash
# Vendor A (mock JSON)
curl -s http://localhost:8081/products | jq .

# Consolidated inventory
curl -s http://localhost:8080/products | jq .

# Swagger UI
open http://localhost:8080/swagger-ui.html
```

### 4) Out‑of‑stock logs

With staggered crons (5s vs 7s) you should see in `stock-service`:

```
OUT-OF-STOCK detected sku=XYZ456 vendor=VendorB prev=5 now=0
```

If not:

* Verify **CSV path** matches in both modules.
* Increase log level: `logging.level.com.upwork.stock=DEBUG`.
* Restart `vendor-b` to reset its alternation (starts at 5).

---

## Docker & Compose

### Dockerfiles

Located under `deploy/docker/` using Gradle official image for build stage:

* `deploy/docker/stock-service.Dockerfile`
* `deploy/docker/vendor-a.Dockerfile`
* `deploy/docker/vendor-b.Dockerfile`

> **Spring relaxed binding** examples:
>
> * `INGESTION_VENDORA_BASEURL` → `ingestion.vendorA.baseUrl`
> * `INGESTION_VENDORB_CSVPATH` → `ingestion.vendorB.csvPath`
> * `INGESTION_SYNC_CRON` → `ingestion.sync.cron`
> * `VENDORB_*` → `vendorb.*`

### Commands

```bash
docker compose build
docker compose up -d

# Logs
docker compose logs -f vendor-b
docker compose logs -f stock-service

# Try
curl -s http://localhost:8080/products | jq .
open http://localhost:8080/swagger-ui.html
```

---

## Endpoints

* `GET /products` → List of `{ id, sku, name, stockQuantity, vendor }`.
* Swagger UI → `/swagger-ui.html`
  OpenAPI → `/v3/api-docs`, `/v3/api-docs.yaml`.

---

## Testing notes

* Prefer `@MockitoBean` over deprecated `@MockBean` (Boot 3.4+).
* Repository tests with `@DataJpaTest` and H2 in‑memory.
* Web tests with `@WebMvcTest` and record DTOs for responses.
* For `StockSyncServiceTest`, mock `VendorAClient`, `VendorBReader`, `ProductRepository`, and pass a dummy `StockIngestionProperties`.

---

## Troubleshooting

* **Gradle/Wrapper in Docker**: build stage uses `gradle:8.14.3-jdk21` → no wrapper required inside the container.
* **“Could not determine java version from '25'”**: run with JDK 21; set `org.gradle.java.home` or `asdf local java …`.
* **No OUT‑OF‑STOCK log**: ensure staggered crons (e.g., 5s/7s), shared CSV path, and proper log level. Restart `vendor-b` to reset its alternation.
* **`vendorBProperties` missing**: in `vendor-b` we use `@EnableConfigurationProperties(VendorBProperties.class)` and `@Scheduled(cron = "${vendorb.schedule}")` (no SpEL by bean name).

---

## Roadmap

* `POST /admin/sync` to trigger on‑demand sync.
* Persist out‑of‑stock events and expose `GET /events`.
* Pagination/filters in `GET /products`.
* Migrate H2 → Postgres (with Testcontainers in tests).
* Metrics with Actuator.
