# Development Workflow

## Goal

Every change should follow the same predictable workflow.

```
Issue

↓

Feature Branch

↓

Implementation

↓

Commit

↓

Push

↓

Pull Request

↓

Code Review

↓

CI Validation

↓

Merge

↓

Deploy
```

---

# Why Feature Branch?

Isolation.

Developers can work independently without affecting the stability of the main branch.

---

# Why Pull Request?

Pull Requests provide an opportunity to:

- Review code
- Discuss implementation
- Run automated CI
- Detect bugs before merging

---

# Why CI?

Every Pull Request should be validated automatically.

Typical checks include:

- Build
- Unit Tests
- Static Analysis
- Formatting

The goal is to prevent broken code from reaching the main branch.
