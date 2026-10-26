# Reference client app

A framework-free HTML + JavaScript example showing how any app plugs into the
sandbox: it logs in via Keycloak (OIDC Authorization Code + PKCE), then calls the
gateway APIs with the bearer token and the `X-Sandbox-Key` header.

## Run it (no build step)

Serve this folder on port 3000 (the port registered in the Keycloak client's
redirect URIs). Any static server works, e.g.:

    npx serve -l 3000 reference-app

or with Python:

    python -m http.server 3000 --directory reference-app

Then open http://localhost:3000 and log in as `alice` / `password`.

Prerequisites: Keycloak (:8080), the gateway (:8081) and the four services must
be running. See the project `GETTING_STARTED.md`.

## What it demonstrates

- OIDC login/logout against the `sandbox-web` public client.
- Listing the signed-in customer's accounts.
- Viewing a selected account's statement.
- Making a transfer, including readable handling of AML holds/blocks and
  insufficient-funds errors.

## Using it as a template

Point your own app (Next.js, React, Python, Flutter, ...) at the same two things:
1. Keycloak for login (issuer `http://localhost:8080/realms/digital-bank`, client `sandbox-web`).
2. The gateway `http://localhost:8081/api/v1` with `Authorization: Bearer <token>`
   and `X-Sandbox-Key: demo-key-123`.

Edit `config.js` if your ports or API key differ.
