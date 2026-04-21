# Backend API Integration

> Load this file when the task is about backend integration from contract to Zero runtime API, especially for generated or contract-aligned interfaces.

## 1. Scope

This file owns:

- contract-aligned backend API integration
- Zero runtime API chain for integration work
- search protocol alignment between client and backend

It does not own:

- frontend client details
- environment variable contracts
- broader DPA architecture rules

## 2. Runtime Chain

Use the standard backend chain:

```text
Agent -> Addr -> Actor -> Stub -> Service -> DBE
```

Responsibilities:

- Agent = route and input transfer only
- Addr = event address constants
- Actor = async entry to stub
- Stub = domain contract
- Service = business implementation
- DBE / DAO = persistence access

## 3. Contract Rules

- stub, service, and actor methods return `Future<T>`
- transaction management stays in service layer
- format validation stays at input boundary
- business validation stays in service layer
- exception ownership stays in provider/domain error contracts

## 4. Search Protocol Alignment

Zero search protocol is `QQuery` / `QTree`, not ad hoc query JSON.

Use the normalized envelope:

```json
{
  "criteria": {},
  "pager": { "page": 1, "size": 10 },
  "sorter": ["field,ASC"],
  "projection": ["field1", "field2"]
}
```

Rule:

```text
Frontend search forms, backend search APIs, and DAO query semantics must align on the same query model.
```

## 5. Agent Rules

- Do not generate backend APIs that drift away from the contract model.
- Do not place transaction or DB logic in API classes.
- Do not invent a separate search protocol when `QQuery` already exists.
