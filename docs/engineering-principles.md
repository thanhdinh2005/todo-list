# Engineering Principles

These principles guide every engineering decision in this template.

---

## Build Once, Promote Many

Build a Docker image once.

Deploy the same artifact across all environments.

---

## Keep Main Deployable

The `main` branch should always remain in a releasable state.

---

## Small Pull Requests

Small Pull Requests are easier to review and reduce risk.

---

## Infrastructure as Code

Infrastructure should be reproducible through code and configuration rather than manual setup.

---

## Configuration over Hardcoding

Application behavior should be driven by configuration.

---

## Convention over Configuration

Follow established conventions whenever possible to reduce unnecessary decisions.

---

## Fail Fast

Detect configuration and validation errors as early as possible.

---

## Separation of Concerns

Each layer should have a single responsibility.

---

## Security by Default

Secure defaults are preferred over optional security.

---

## Continuous Improvement

The template should evolve based on lessons learned from real projects rather than speculative design.
