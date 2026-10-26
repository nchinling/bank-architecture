# Requirements Document

## Introduction

The RP Digital Bank Sandbox is a self-contained, educational banking digital-services platform. It simulates core banking capabilities (customers, accounts, transactions, payments) together with identity and compliance, so that students, staff, and interns can learn digital banking hands-on and then build and plug in their own applications (for example a wealth planner, a chatbot, or a merchant app) in any programming language against plain REST/JSON APIs secured with OAuth2/OIDC.

Every capability that would normally be provided by an external vendor — identity (OAuth/OIDC), electronic KYC (eKYC), anti-money-laundering/fraud screening (AML), and the event bus — is simulated internally but sits behind a common adapter interface. This makes each simulated capability detachable: a real vendor, or an alternative in-house implementation, can be swapped in later purely through configuration, with no changes to the services that depend on it.

The platform is built as a Java 25 + Spring Boot multi-module Maven monorepo, uses MySQL 8 for persistence, and is designed to run "native-first" (as local processes with no Docker required), while remaining container- and Kubernetes-ready for a shared, hosted deployment later.

## Glossary

- **Sandbox**: the complete educational banking platform described by this spec.
- **Application layer**: student/author-built client applications that consume the sandbox APIs; language-agnostic.
- **Service**: a backend Spring Boot module exposing part of the banking domain (customer, account, transaction, payment).
- **Gateway**: the single API entry point that fronts all services (routing, API keys, rate limiting, token validation).
- **Adapter / Provider**: a Java interface in the shared module with interchangeable implementations selected by a `provider.*` configuration flag (e.g. `IdentityProvider`, `KycProvider`, `AmlProvider`, `EventPublisher`).
- **Simulated provider**: the default in-house implementation of an adapter used in v1.
- **customerRef**: a stable public identifier for a customer, carried in the access token and used to scope data ownership.
- **eKYC**: electronic Know-Your-Customer identity screening performed at customer onboarding.
- **AML**: Anti-Money-Laundering / fraud screening performed on transfers.
- **Ledger entry / leg**: an immutable debit or credit record; the two legs of a transfer share one transaction reference.
- **Native-first**: running the platform as local OS processes (MySQL, Keycloak, Spring services) without Docker.
- **Instructor / admin**: a privileged user (holding the `admin` role) who can steer simulated compliance outcomes for demos.
- **EARS**: Easy Approach to Requirements Syntax (WHEN/IF/WHILE/WHERE ... THE system SHALL ...).

## Requirements

### Requirement 1: Pluggable, language-agnostic application layer

**User Story:** As a student developer, I want to build my own application in any language against stable REST/JSON APIs, so that I can learn digital banking without being constrained to a particular client technology.

#### Acceptance Criteria

1. THE sandbox SHALL expose all banking capabilities as REST APIs that exchange JSON over HTTP.
2. THE sandbox SHALL authenticate API callers using OAuth2/OIDC bearer tokens (JWTs).
3. WHERE a client application is written in any language or framework (for example JavaScript, Node.js, Next.js, Python, or Flutter), THE sandbox SHALL serve it without requiring server-side changes specific to that client.
4. THE sandbox SHALL support browser-based clients by permitting cross-origin requests from configured client origins.
5. THE sandbox SHALL NOT require the application layer to be deployed inside the sandbox in order to consume its APIs.

### Requirement 2: Detachable capabilities via the adapter pattern

**User Story:** As the platform owner, I want each simulated capability to sit behind a common interface, so that I can replace it with a real vendor or an alternative implementation later without rewriting the services.

#### Acceptance Criteria

1. THE sandbox SHALL define each externally-replaceable capability (identity, eKYC, AML, events) as an interface in the shared `common` module.
2. THE sandbox SHALL select the active implementation of each capability via a `provider.*` configuration property (for example `provider.identity`, `provider.kyc`, `provider.aml`, `provider.events`).
3. WHEN no `provider.*` value is configured for a capability, THE sandbox SHALL default to the simulated implementation.
4. WHEN the `provider.*` value for a capability is changed, THE sandbox SHALL switch to the matching implementation without any change to the services that depend on the interface.
5. THE sandbox SHALL include an automated contract test that verifies interchangeable implementations of the identity provider satisfy the same behavioural contract.

### Requirement 3: Customer onboarding with simulated eKYC

**User Story:** As a student, I want to onboard customers and observe KYC outcomes, so that I can learn how identity screening gates account opening.

#### Acceptance Criteria

1. WHEN a request to create a customer is received with a full name and a valid email, THE customer service SHALL create the customer and persist the resulting KYC status.
2. WHEN a customer is created, THE customer service SHALL screen the applicant through the configured `KycProvider` before confirming creation.
3. IF the applicant's full name contains the token "FRAUD" (case-insensitive), THEN the simulated eKYC SHALL return REJECTED.
4. IF the applicant's full name contains the token "REVIEW" (case-insensitive) and does not match a higher-priority rule, THEN the simulated eKYC SHALL return MANUAL_REVIEW.
5. IF the applicant's full name matches no deterministic rule, THEN the simulated eKYC SHALL return the configured default outcome (APPROVED by default).
6. IF the submitted email is not a valid email address, THEN the customer service SHALL reject the request with a validation error.
7. WHEN an authenticated caller requests their own customer record, THE customer service SHALL return the record identified by the caller's customerRef.

### Requirement 4: Simulated identity and JWT-protected access

**User Story:** As a learner, I want a realistic login that issues tokens, so that I experience how OAuth2/OIDC secures banking APIs.

#### Acceptance Criteria

1. THE sandbox SHALL provide a simulated identity provider (Keycloak) that issues OAuth2/OIDC access tokens as the v1 default.
2. THE sandbox SHALL validate incoming JWTs against the configured OIDC issuer before granting access to protected endpoints.
3. WHEN a request to a protected endpoint presents a valid JWT, THE sandbox SHALL allow the request to proceed.
4. IF a request to a protected endpoint presents no token or an invalid token, THEN the sandbox SHALL reject it with HTTP 401.
5. THE sandbox SHALL resolve the caller's customerRef from the verified token claims via the active `IdentityProvider`.
6. THE sandbox SHALL permit the identity provider to be replaced by an alternative (for example a built-in minimal auth service) through configuration only.
7. THE sandbox SHALL leave actuator health, OpenAPI documents, and Swagger UI publicly reachable without a token.

### Requirement 5: Accounts with pre-seeded synthetic data and ownership scoping

**User Story:** As a student, I want accounts and balances available immediately and scoped to me, so that I can start building without an empty database and cannot see other customers' data.

#### Acceptance Criteria

1. THE account service SHALL provide pre-seeded synthetic customers, accounts, and opening balances on first run.
2. THE account service SHALL represent monetary balances using a fixed-precision decimal type (never binary floating point).
3. WHEN an authenticated caller lists accounts, THE account service SHALL return only the accounts whose owner matches the caller's customerRef.
4. WHEN an authenticated caller requests an account by number that belongs to a different customer, THE account service SHALL respond as if the account does not exist (HTTP 404) rather than revealing its existence.
5. THE account service SHALL derive the owning customer from the authenticated token and SHALL NOT accept the owner identity from the request body or parameters.

### Requirement 6: Append-only transaction ledger and history

**User Story:** As a student, I want to view statement-style transaction history, so that I can understand how banking ledgers record money movements.

#### Acceptance Criteria

1. THE transaction service SHALL store ledger entries as immutable records that are never updated or deleted.
2. THE transaction service SHALL record each money movement as balanced legs, where the two legs of a transfer share a single transaction reference.
3. WHEN an authenticated caller requests the history for one of their accounts, THE transaction service SHALL return the matching ledger entries ordered most-recent first.
4. WHEN a date range is supplied with a history request, THE transaction service SHALL return only entries booked within that range.
5. WHEN an authenticated caller requests history for an account they do not own, THE transaction service SHALL NOT return another customer's entries.
6. THE transaction service SHALL provide pre-seeded historical transactions on first run.

### Requirement 7: Internal transfers with AML screening and events

**User Story:** As a student, I want to move money between accounts and see compliance and events in action, so that I understand payment processing end-to-end.

#### Acceptance Criteria

1. WHEN an authenticated caller requests a transfer from an account they own to another account, THE payment service SHALL screen the transfer through the configured `AmlProvider` before moving money.
2. IF the transfer amount is at or above the configured AML flag threshold, THEN the payment service SHALL block the transfer (HTTP 422) and SHALL NOT change any balance.
3. IF the transfer amount is at or above the configured AML review threshold but below the flag threshold, THEN the payment service SHALL hold the transfer for review (HTTP 202) and SHALL NOT change any balance.
4. WHEN a transfer is cleared by AML and the source has sufficient funds, THE payment service SHALL debit the source, credit the destination, and append two balanced ledger legs within a single atomic transaction.
5. IF the source account has insufficient funds, THEN the payment service SHALL reject the transfer (HTTP 409) and SHALL NOT change any balance.
6. IF the source account does not belong to the authenticated caller, THEN the payment service SHALL reject the transfer (HTTP 403).
7. WHEN a transfer completes successfully, THE payment service SHALL publish a payment-completed event through the configured `EventPublisher`.
8. WHERE the event provider is the in-process implementation, THE payment service SHALL publish events without requiring an external message broker.

### Requirement 8: Single API gateway with keys and rate limiting

**User Story:** As a self-service user, I want one entry point with a simple API key, so that onboarding is easy and the platform is protected from abuse.

#### Acceptance Criteria

1. THE gateway SHALL expose a single entry point that routes versioned paths (`/api/v1/...`) to the appropriate backend service.
2. WHEN a request to a non-public path presents a missing or unrecognised sandbox API key, THE gateway SHALL reject it with HTTP 401.
3. WHEN a valid API key exceeds its configured request quota within the rate-limit window, THE gateway SHALL reject further requests with HTTP 429.
4. THE gateway SHALL enforce rate limiting per API key independently of other keys.
5. THE gateway SHALL validate JWTs at the edge when an OIDC issuer is configured.
6. THE gateway SHALL exempt public paths (actuator health, OpenAPI documents, Swagger UI) from the API key and token requirements.
7. THE gateway SHALL perform rate limiting without requiring an external datastore in the native-first configuration.

### Requirement 9: Aggregated API documentation and onboarding

**User Story:** As a newcomer, I want interactive API docs and a getting-started guide, so that I can explore and call the APIs with minimal setup.

#### Acceptance Criteria

1. THE sandbox SHALL publish an OpenAPI document for each service that lists its versioned endpoints.
2. THE gateway SHALL present a single aggregated Swagger UI from which each service's API can be selected and exercised.
3. THE sandbox SHALL document the bearer-token security scheme in each service's OpenAPI document.
4. THE sandbox SHALL provide a getting-started guide covering native startup order, obtaining a token, obtaining an API key, and making calls.
5. THE sandbox SHALL provide at least one worked end-to-end example that a student can copy when building their own application.

### Requirement 10: Instructor-configurable compliance outcomes

**User Story:** As an instructor, I want to steer simulated compliance outcomes at runtime, so that I can reliably demonstrate approvals, rejections, holds, and blocks during a class.

#### Acceptance Criteria

1. THE sandbox SHALL provide admin endpoints that override the simulated eKYC outcome and the simulated AML flag threshold at runtime.
2. THE sandbox SHALL restrict the admin endpoints to callers holding the `admin` role.
3. IF a non-admin authenticated caller invokes an admin endpoint, THEN the sandbox SHALL reject the request with HTTP 403.
4. IF an unauthenticated caller invokes an admin endpoint, THEN the sandbox SHALL reject the request with HTTP 401.
5. WHEN an instructor forces the next eKYC outcome, THE sandbox SHALL apply that outcome to the next customer creation and then revert to the normal rules.
6. THE admin overrides SHALL affect only the simulated providers and SHALL have no effect once a real vendor adapter is active.

### Requirement 11: Native-first runtime with a path to containers and CI/CD

**User Story:** As the platform owner, I want to run everything locally without Docker and still be able to containerize and automate later, so that the sandbox is easy to start now and scalable to a shared environment later.

#### Acceptance Criteria

1. THE sandbox SHALL run as local processes (MySQL, Keycloak, Spring services, gateway) without requiring Docker.
2. THE sandbox SHALL assign each service a distinct port so that all services can run simultaneously on one machine.
3. THE sandbox SHALL read database credentials from environment variables and SHALL NOT hardcode them in source or committed configuration.
4. THE sandbox SHALL build and run its automated tests without requiring a running MySQL instance.
5. THE sandbox SHALL provide a continuous-integration pipeline that builds the project and runs the tests on each change.
6. THE sandbox SHALL provide container and Kubernetes artifacts that run the same services without changes to service code.

### Requirement 12: Build system and technology baseline

**User Story:** As a contributor, I want a consistent, self-bootstrapping build on a modern Java baseline, so that I can build the project without installing extra tooling.

#### Acceptance Criteria

1. THE sandbox SHALL be organised as a multi-module Maven monorepo with a shared parent that pins common dependency and plugin versions.
2. THE sandbox SHALL target Java 25 (LTS) and Spring Boot 3.5.x.
3. THE sandbox SHALL include a Maven Wrapper so that contributors can build without a pre-installed Maven.
4. THE sandbox SHALL use database schema migrations authored for MySQL 8 to create and seed all tables.
5. WHEN the full build is run, THE sandbox SHALL compile every module and execute every module's tests.
