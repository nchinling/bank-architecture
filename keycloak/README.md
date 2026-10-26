# Keycloak (simulated identity provider)

Keycloak is the v1 identity provider, sitting behind the `IdentityProvider`
adapter. It issues the OAuth2/OIDC JWTs that clients present to the sandbox APIs.
It is swappable: set `provider.identity=builtin` to use the in-house provider
instead (no service code changes).

## Run Keycloak natively (no Docker)

1. Download Keycloak (ZIP) from https://www.keycloak.org/downloads and unzip it.
   It runs on your existing JDK 25.
2. Start it in dev mode on port 8080, importing the sandbox realm:

   Windows (PowerShell), from the unzipped Keycloak folder:

   ```powershell
   $env:KEYCLOAK_ADMIN = "admin"
   $env:KEYCLOAK_ADMIN_PASSWORD = "admin"
   .\bin\kc.bat start-dev --import-realm --http-port=8080
   ```

   To import the realm, copy `realm-digital-bank.json` (in this folder) into
   Keycloak's `data/import/` directory before starting, or import it later via
   the admin console (Realm settings -> Partial import).

3. Admin console: http://localhost:8080  (admin / admin)
   The realm `digital-bank` is created with:
   - a public SPA client `sandbox-web`
   - a `customer_ref` claim mapper (adds the stable customer reference to tokens)
   - test users: `alice`, `bob` (role: customer) and `instructor` (roles: customer, admin)
   - all test users have password `password`

## OIDC endpoints

- Issuer:        `http://localhost:8080/realms/digital-bank`
- Token:         `http://localhost:8080/realms/digital-bank/protocol/openid-connect/token`
- JWKS (keys):   `http://localhost:8080/realms/digital-bank/protocol/openid-connect/certs`
- Discovery:     `http://localhost:8080/realms/digital-bank/.well-known/openid-configuration`

Services validate tokens against the issuer via Spring Security resource-server
(`spring.security.oauth2.resourceserver.jwt.issuer-uri`).

## Get a token (for testing APIs)

Direct access grant (password) flow with the public client, handy for demos:

```powershell
curl -X POST "http://localhost:8080/realms/digital-bank/protocol/openid-connect/token" `
  -H "Content-Type: application/x-www-form-urlencoded" `
  -d "grant_type=password&client_id=sandbox-web&username=alice&password=password"
```

Copy the `access_token` from the response and call a protected API:

```powershell
curl http://localhost:8082/customers/me -H "Authorization: Bearer <access_token>"
```

## Swapping Keycloak out later

Because every service depends only on the `IdentityProvider` contract:

- To use the built-in provider: set `provider.identity=builtin`.
- To use a different vendor IdP: point `sandbox.identity.issuer-uri` and
  `spring.security.oauth2.resourceserver.jwt.issuer-uri` at the new issuer.

No service code changes are required. The `IdentityProviderContractTest`
guarantees any provider behaves identically.
