# Wallet Service Challenge

This API project was developed to demonstrate backend development skills and architecture knowledge. It serves as part of a technical assessment to showcase the candidate's ability to design, implement, and test backend systems using modern best practices.

---

## Architecture

This project follows the **Hexagonal Architecture (Ports & Adapters)**. It separates the business logic from external dependencies such as databases and HTTP controllers, improving testability, maintainability, and flexibility.

In practice, the architecture is divided into three layers:

- **Domain (Core)**: Business rules without any dependencies
- **Ports (Interfaces)**: Input (use cases) and output (infrastructure access)
- **Adapters (Implementations)**: Concrete technologies like PostgreSQL, Redis, REST APIs

<div align="center">

![Hexagonal Architecture](https://www.arnaudlanglade.com/hexagonal-architecture-by-example/hexgonal-architecture-flow-control.svg)

![](https://img.shields.io/badge/Author-Roberto%20Gualberto%20dos%20Santos-brightgreen)
![](https://img.shields.io/badge/Language-Java%2021-brightgreen)
![](https://img.shields.io/badge/Framework-SpringBoot%203.2.x-brightgreen)
![](https://img.shields.io/badge/Architecture-Hexagonal-brightgreen)

</div>

---

## Prerequisites

- [Java 21](https://www.oracle.com/java/technologies/downloads/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/install/)

---

## Start the Application

### ✅ Option 1: Using script (recommended)

At the root of the project:

```bash
/bin/bash start-service.sh
```

This will start the PostgreSQL and Redis containers, and run the Spring Boot application.

### ✅ Option 2: Using IDE (e.g. IntelliJ)

1. Go to the Docker folder:
   ```bash
   cd docker
   docker compose -f docker-compose.yml up ws_database redis -d
   ```
2. Run the Spring Boot application from your IDE (`WalletApplication.java`)

---

## 🔗 API Links

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Endpoints

```
[POST]   /api/v1/wallets
[POST]   /api/v1/wallets/deposit
[POST]   /api/v1/wallets/withdraw
[POST]   /api/v1/wallets/transfer
[GET]    /api/v1/wallets/{walletId}
[GET]    /api/v1/wallets/{walletId}/balance
[GET]    /api/v1/wallets/{walletId}/historical-balance?timestamp={timestamp}
```

---

## ✅ Running Tests

### Unit & Integration Tests

To execute all automated tests:

```bash
./mvnw clean test
```

The tests include:

- Core business logic (deposit, withdraw, transfer)
- Idempotency and retry handling (Resilience4j)
- Exception handling and fallback logic
- Cache layer interactions
- Edge cases and error scenarios


---

## API Testing with Postman

This project includes a ready-to-use Postman collection to interact with the API.

### File Location

```
docker/Wallet.postman_collection.json
```

### Steps to Use

1. Open [Postman](https://www.postman.com/)
2. Click **Import** and select the file
3. Set variables `{{walletId}}`, `{{fromWalletId}}`, `{{toWalletId}}` with real values
4. Use the following requests in order:

    - Create Wallet
    - Deposit
    - Withdraw
    - Transfer
    - Get Wallet Info
    - Get Current Balance
    - Get Historical Balance

> Most POST endpoints require a unique `X-Request-ID` header for idempotency:
> ```
> X-Request-ID: {{$uuid}}  // Postman auto-generates it
> ```

---

## Design Choices

### Why Redis alongside PostgreSQL?

Redis was chosen for:

- **Idempotency handling**: to safely retry requests without duplication
- **Caching**: balance and wallet data are read frequently and change rarely, making them ideal for cache

This improves performance and avoids race conditions in concurrent operations.

### How does Hexagonal Architecture help?

It enforces **separation of concerns**:

- Business rules are isolated and easily testable
- Infrastructure (like PostgreSQL or Redis) can be swapped with minimal impact
- Test doubles (mocks/fakes) are easy to inject through ports

This leads to more **modular and evolvable** systems.

### How is data consistency ensured?

- All mutation operations are **transactional**
- **Resilience4j** adds retry logic to absorb transient errors
- Redis-backed **idempotency** prevents duplicated operations under concurrency or retries

---

## Trade-offs

### What was skipped due to time?

- Full-blown test coverage for all edge cases
- Docker images for production deployment
- Real observability integration (Grafana, Prometheus)

### Improvements for future iterations

- Role-based authentication (e.g., Spring Security + JWT)
- Rate limiting & API keys
- Better metrics collection and dashboarding
- Circuit breaker monitoring with Grafana

---

## Time Tracking

| Task                     | Duration |
|--------------------------|----------|
| Core business logic      | 3h       |
| Idempotency              | 2h       |
| Unit/Integration Tests   | 1.5h     |
| Infrastructure (Docker)  | 0.5h     |
| **Total**                | **7h**   |

---
