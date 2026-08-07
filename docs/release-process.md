# Release Process

## Goal

Every release should be traceable and reproducible.

---

# Release Flow

```
Feature Complete

↓

Merge into main

↓

CI Passes

↓

Create Git Tag

↓

GitHub Release

↓

Deploy
```

---

# Versioning

Semantic Versioning is used.

```
MAJOR.MINOR.PATCH
```

Example:

```
1.0.0
```

Major

Breaking Changes

Minor

New Features

Patch

Bug Fixes

---

# Git Tag

Example

```
git tag v1.0.0

git push origin v1.0.0
```

---

# Rollback

Rollback should be performed by deploying a previous stable release.
