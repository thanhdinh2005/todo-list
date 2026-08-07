# Deployment Guide

## Deployment Pipeline

```
Developer

↓

Git Push

↓

GitHub Actions

↓

Build

↓

Test

↓

Docker Image

↓

Container Registry

↓

Homelab / VPS / Cloud

↓

Health Check
```

---

# Build Once, Promote Many

Docker images are built only once.

The same image should be promoted across environments.

```
Development

↓

Staging

↓

Production
```

No rebuilding.

Only configuration changes.

---

# Configuration

Configuration should be environment-specific.

```
application.yml

application-dev.yml

application-prod.yml
```

Secrets should never be committed into the repository.

---

# Deployment

Typical deployment:

```
docker compose pull

docker compose up -d
```

Future improvements:

- Blue/Green Deployment
- Rolling Update
- Automatic Rollback
