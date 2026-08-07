# Branching Strategy

## Purpose

This project follows a Feature Branch Workflow to keep the `main` branch stable, reviewable, and always deployable.

---

# Old Way

All developers commit directly to the `main` branch.

Problems:

- Broken production branch
- Difficult code review
- Frequent merge conflicts
- Unfinished features mixed together

---

# New Approach

Each feature is developed in an isolated branch.

```
main
 ├── feat/auth
 ├── feat/user
 ├── feat/github-actions
 ├── fix/login
```

Only reviewed code can be merged into `main`.

---

# Branch Naming

Feature

```
feat/<feature-name>
```

Bug Fix

```
fix/<issue-name>
```

Documentation

```
docs/<topic>
```

Refactoring

```
refactor/<module>
```

Continuous Integration

```
ci/<topic>
```

Maintenance

```
chore/<task>
```

Testing

```
test/<module>
```

---

# Development Flow

```
Create Branch

↓

Implement

↓

Commit

↓

Push

↓

Open Pull Request

↓

Code Review

↓

CI

↓

Merge

↓

Delete Branch
```

---

# Best Practices

- Never commit directly to `main`
- Keep Pull Requests small
- One Pull Request should represent one logical change
- Delete feature branches after merging
- Keep `main` always releasable
