# Repository Setup

This checklist should be completed after creating a new project from the template.

---

# Repository

- [ ] Rename the project
- [ ] Update README
- [ ] Configure project description

---

# Branch Protection

Protect `main`.

Recommended rules:

- [ ] Require Pull Request
- [ ] Require successful CI
- [ ] Prevent force push
- [ ] Prevent branch deletion

---

# Repository Settings

- [ ] Automatically delete merged branches
- [ ] Configure merge strategy
- [ ] Enable GitHub Actions

---

# Secrets

Configure required secrets.

Example:

- [ ] SSH_HOST
- [ ] SSH_USER
- [ ] SSH_PRIVATE_KEY
- [ ] DOCKER_USERNAME
- [ ] DOCKER_PASSWORD

---

# Verification

Before development begins:

- [ ] Project builds
- [ ] Docker Compose works
- [ ] Flyway migration succeeds
- [ ] CI pipeline passes
