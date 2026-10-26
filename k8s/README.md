# Kubernetes manifests

Plain Kubernetes manifests that run the same sandbox services as Docker Compose,
with no changes to service code - proving the architecture is container- and
cluster-ready.

## What's here

| File | Contents |
|------|----------|
| `00-config.yaml` | ConfigMap (settings + adapter `PROVIDER_*` switches) and Secret (DB creds) |
| `10-infra.yaml`  | MySQL and Keycloak Deployments + Services |
| `20-services.yaml` | The four banking services (Deployments + Services) |
| `30-gateway.yaml` | Gateway Deployment + Service + Ingress (single entry point) |

Adapter switching is configuration only: edit the `PROVIDER_*` keys in
`00-config.yaml` to swap a simulated provider for a vendor implementation.

## Build and load images (kind example)

The manifests reference local image tags (`rp-sandbox/<service>:latest`). Build
them from each Dockerfile, then make them available to the cluster.

```bash
# Build images (run from the repo root)
docker build -t rp-sandbox/customer-service:latest    -f customer-service/Dockerfile .
docker build -t rp-sandbox/account-service:latest     -f account-service/Dockerfile .
docker build -t rp-sandbox/transaction-service:latest -f transaction-service/Dockerfile .
docker build -t rp-sandbox/payment-service:latest     -f payment-service/Dockerfile .
docker build -t rp-sandbox/gateway:latest             -f gateway/Dockerfile .

# Load them into a kind cluster (or push to your registry for a real cluster)
kind load docker-image rp-sandbox/customer-service:latest
kind load docker-image rp-sandbox/account-service:latest
kind load docker-image rp-sandbox/transaction-service:latest
kind load docker-image rp-sandbox/payment-service:latest
kind load docker-image rp-sandbox/gateway:latest
```

## Deploy

```bash
kubectl apply -f k8s/00-config.yaml
kubectl apply -f k8s/10-infra.yaml
kubectl apply -f k8s/20-services.yaml
kubectl apply -f k8s/30-gateway.yaml
```

## Validate without a cluster

```bash
kubectl apply --dry-run=client -f k8s/
```

## Notes

- Infra (MySQL/Keycloak) uses ephemeral storage - fine for a teaching sandbox.
- For a shared cluster, add persistent volumes, resource limits, readiness/
  liveness probes, and a real Secret source; and import the Keycloak realm (mount
  `keycloak/realm-digital-bank.json` and pass `--import-realm`).
- Promotion path: native-first -> Docker Compose (`docker compose up`) -> this
  Kubernetes deployment. The service code is identical across all three.
