# Backend Model and Tenant Rules

> Load this file when the task is about table conventions, model metadata, tenant isolation, audit fields, or backend resource ownership.

## 1. Scope

This file owns:

- table naming conventions
- common business fields
- model metadata resource ownership
- tenant and app context rules

It does not own:

- DPA boundaries
- DBE query syntax
- RBAC semantics
- CRUD engine routing

## 2. Table Conventions

Common table prefixes:

- `X_` business entity tables
- `R_` relation tables
- `S_` system and security tables
- `O_` supporting tables

## 3. Common Business Fields

Business tables commonly carry:

- `key`
- `sigma`
- `appId`
- `language`
- `active`
- `createdBy`
- `createdAt`
- `updatedBy`
- `updatedAt`
- `deleted`

Rules:

- `sigma` is mandatory for tenant-safe business data
- add an index on `SIGMA`
- unique constraints should include tenant scope where required
- prefer logical delete over physical delete

## 4. Model Metadata Ownership

Keep entity metadata in domain resources, commonly under:

```text
plugins/{module}/model/{identifier}/entity.json
plugins/{module}/model/{identifier}/column.json
```

Common uses:

- unique-key declaration
- default values
- UI and search metadata
- field rendering and filter configuration

Rule:

```text
Model metadata belongs to domain resources, not to scattered handwritten transport code.
```

## 5. Tenant Context

Zero business modules are shared-db/shared-schema multi-tenant by default.

Core runtime context values:

- `Habitus.sigma()`
- `Habitus.appId()`
- `Habitus.userId()`
- `Habitus.language()`

Common request headers:

- `X-Sigma`
- `X-App-Id`
- `X-Lang`
- `Authorization`

## 6. Tenant Rules

- framework auto-injects tenant and audit fields on insert
- framework auto-adds tenant filters in DBE / DB queries
- do not set `sigma` manually unless the use case is explicitly cross-tenant
- cross-tenant operations require elevated authorization
- row isolation belongs in security and model metadata, not in ad hoc service predicates

## 7. Agent Rules

- If tenant behavior is wrong, inspect context injection before changing service code.
- If UI/search metadata looks wrong, inspect `entity.json` and `column.json` before changing API code.
- Do not solve tenant isolation with handwritten query fragments when framework metadata already owns it.
