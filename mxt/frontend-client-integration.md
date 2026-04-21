# Frontend Client Integration

> Load this file when the task is about frontend client generation, Rust/Tauri integration, React admin integration, or client-side contract alignment.

## 1. Scope

This file owns:

- Rust / Tauri / Leptos client integration
- React / AntD admin integration alignment
- shared frontend contract rules

It does not own:

- backend DPA structure
- `.r2mo` contract authority
- environment variable contracts

## 2. Rust / Tauri / Leptos Pattern

Use the contract-to-client path:

```text
.r2mo/api definition
  -> client DTO / model generation
  -> API client
  -> page and component usage
```

Rules:

- API base is `/api`
- web mode uses HTTP
- Tauri mode may use command bridges such as `invoke(...)`
- all API calls return a typed result model
- error models must distinguish transport, parse, serialize, API, and business failures

## 3. Shared Header Contract

Client integrations should inject consistent request headers:

- `Authorization`
- `X-App-Id`

Rule:

```text
Auth and app identity belong to shared client infrastructure, not to page-level ad hoc code.
```

## 4. React / AntD Admin Pattern

React admin UI remains contract-driven even when presentation rules differ from Rust/Tauri clients.

Important alignment points:

- page layout may differ
- transport contract should not differ
- field, filter, and column behavior should still align with backend contract and model metadata

`entity.json` and `column.json` may act as UI metadata bridges.

## 5. Agent Rules

- Do not derive client types from page code.
- Do not duplicate auth-header logic across pages.
- Keep Rust/Tauri and React clients aligned on backend contract even if their UI rules differ.
