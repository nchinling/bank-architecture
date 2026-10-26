# Getting Started

There are two ways to run the sandbox:

- **Quick start with Docker** (easiest) - one command brings up everything. See below.
- **Native (no Docker)** - run each piece as a local process. See "Running natively".

---

## Quick start with Docker (recommended once Docker is installed)

This is the simplest way to run the whole sandbox. Docker pulls MySQL and Keycloak
for you (nothing to install separately) and builds the service images.

### Prerequisites
- Docker Desktop installed and running (verify: `docker --version` and `docker compose version`).
- That's it - you do NOT need MySQL, Keycloak, or a JDK installed for this path;
  everything runs inside containers.

### Steps

1. From the project root, start the whole stack:

   ```powershell
   docker compose up --build
   ```

   The first run takes a few minutes (it downloads images and builds the services).
   It starts: MySQL, Keycloak (with the `digital-bank` realm auto-imported), the
   four banking services, and the gateway.

2. Wait until the logs settle and the services report healthy. Check the gateway:

   ```
   http://localhost:8081/actuator/health
   ```

3. Open the aggregated API docs in the browser:

   ```
   http://localhost:8081/swagger-ui.html
   ```

4. (Optional) Run the reference web app in a separate terminal and open it:

   ```powershell
   npx serve -l 3000 reference-app
   ```

   Then browse to http://localhost:3000 and log in as `alice` / `password`.

5. Or call the APIs directly - get a token and call the gateway exactly as in
   steps 5 and 6 of the native guide below (the ports and URLs are identical).

### Stop everything

```powershell
docker compose down
```

Add `-v` (`docker compose down -v`) to also discard the database so the next
start is completely fresh.

### Troubleshooting
- **Port already in use**: make sure nothing else is using 8080-8085 or 3306
  (for example a native MySQL/Keycloak from the other run mode). Stop them first.
- **A service exits before MySQL/Keycloak are ready**: `docker compose up` again -
  the services wait for healthy dependencies, and a restart resolves transient races.
- **Rebuild after code changes**: `docker compose up --build` rebuilds the images.

---

## Running natively

A step-by-step guide to run the sandbox natively (no Docker) and make your first
API calls. Everything runs as local processes on your machine.

## 1. Prerequisites

- JDK 25 (verify: `java -version`)
- MySQL 8 running on `localhost:3306`
- Keycloak (downloaded; see `keycloak/README.md`) - needed for login/tokens

No Maven install needed - the project ships the Maven Wrapper (`mvnw.cmd`).

## 2. Set database credentials

Services read MySQL credentials from environment variables (never hardcoded):

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "<your-mysql-root-password>"
```

## 3. Start the pieces (in order)

Each command runs in its own terminal. Keep them open.

```powershell
# 1) Keycloak (identity) on :8080 - see keycloak/README.md for the import step
.\bin\kc.bat start-dev --import-realm --http-port=8080

# 2) Backend services (each in its own terminal), from the project root:
.\mvnw.cmd -pl customer-service    spring-boot:run     # :8082
.\mvnw.cmd -pl account-service     spring-boot:run     # :8083
.\mvnw.cmd -pl transaction-service spring-boot:run     # :8084
.\mvnw.cmd -pl payment-service     spring-boot:run     # :8085

# 3) API gateway on :8081 (the single entry point)
.\mvnw.cmd -pl gateway spring-boot:run
```

Tip: start a service only once the one before it is up. Health check any service:

```
curl http://localhost:8082/actuator/health
```

## 4. Explore the APIs in the browser (Swagger UI)

Open the aggregated Swagger UI at the gateway:

```
http://localhost:8081/swagger-ui.html
```

Use the dropdown (top-right) to switch between customer, account, transaction and
payment services. Each service's docs are served through the gateway.

## 5. Get an access token

Use Keycloak's token endpoint (test user `alice`, password `password`):

```powershell
curl -X POST "http://localhost:8080/realms/digital-bank/protocol/openid-connect/token" `
  -H "Content-Type: application/x-www-form-urlencoded" `
  -d "grant_type=password&client_id=sandbox-web&username=alice&password=password"
```

Copy the `access_token` from the JSON response.

## 6. Call the APIs through the gateway

The gateway requires a sandbox API key (`X-Sandbox-Key`) and the bearer token.
Demo keys: `demo-key-123`, `student-key-456`.

```powershell
$token = "<access_token>"

# List Alice's accounts (pre-seeded)
curl http://localhost:8081/api/v1/accounts `
  -H "X-Sandbox-Key: demo-key-123" `
  -H "Authorization: Bearer $token"

# View a statement
curl http://localhost:8081/api/v1/transactions/accounts/ACC-1001 `
  -H "X-Sandbox-Key: demo-key-123" `
  -H "Authorization: Bearer $token"

# Make a transfer (ACC-1001 -> ACC-1002, both Alice's)
curl -X POST http://localhost:8081/api/v1/payments/transfers `
  -H "X-Sandbox-Key: demo-key-123" `
  -H "Authorization: Bearer $token" `
  -H "Content-Type: application/json" `
  -d '{"fromAccount":"ACC-1001","toAccount":"ACC-1002","amount":100.00,"description":"pocket money"}'
```

## 7. Build your own app on top

Your application (any language: JavaScript, Node, Next.js, Python, Flutter, ...)
only needs to:

1. Log in via Keycloak (OIDC) to obtain an access token.
2. Call `http://localhost:8081/api/v1/...` with the `X-Sandbox-Key` header and the
   `Authorization: Bearer <token>` header.
3. Read/write JSON.

See the reference client app in `reference-app/` for a working example
(serve it with `npx serve -l 3000 reference-app`, then open http://localhost:3000).

## Seeded test data

| Customer    | Login    | Accounts (balance)                        |
|-------------|----------|-------------------------------------------|
| CUST-ALICE  | alice    | ACC-1001 (5000.00), ACC-1002 (1200.50)    |
| CUST-BOB    | bob      | ACC-2001 (8300.75), ACC-2002 (450.00)     |

All test users have password `password`. The `instructor` user additionally has
the `admin` role.

## Simulated compliance behaviour (for lessons)

- eKYC: name contains `FRAUD` -> REJECTED; contains `REVIEW` -> MANUAL_REVIEW; else APPROVED.
- AML (transfers): amount >= 10000 -> blocked (422); >= 5000 -> held for review (202); else cleared.
