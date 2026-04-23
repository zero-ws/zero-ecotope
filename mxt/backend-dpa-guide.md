# Backend DPA Guide

> Load this file when the task is about Zero backend module structure, DPA ownership, async rules, or the standard request execution chain.

## 1. Scope

This file owns:

- DPA module structure
- module responsibilities
- async backend contract
- the standard request execution chain

It does not own:

- DBE query syntax details
- model metadata rules
- RBAC resource rules
- CRUD-engine-first decisions

## 2. Canonical Module Shape

Use the strict three-module backend split:

```text
<project>-domain   <- contracts, generated model, exceptions, resources
<project>-provider <- business logic, components, spi, boot
<project>-api      <- HTTP agents, event-bus actors, address constants
```

Dependency direction is fixed:

```text
Domain <- Provider <- API
```

## 3. Standard Execution Chain

Use the fixed backend chain:

```text
Agent -> Addr -> Actor -> Stub -> Service -> DBE
```

Meaning:

- `Agent` = HTTP entry declaration
- `Addr` = event-bus address registry
- `Actor` = event consumer that invokes stub
- `Stub` = domain contract
- `Service` = provider implementation
- `DBE` = database access path

## 4. Module Responsibilities

### Domain

Owns:

- generated jOOQ metadata, tables, records, DAOs
- service stub interfaces
- typed exceptions and error codes
- shared enums and constants
- domain resource trees

### Provider

Owns:

- service implementation
- reusable components
- SPI implementations
- module boot wiring

Rules:

- service layer owns orchestration, transactions, and business validation
- provider is the only layer allowed to combine DB access with business rules

### API

Owns:

- `Addr.java`
- `*Agent.java`
- `*Actor.java`

Rules:

- Agent contains no business logic
- Actor contains no DB access
- Actor invokes stub and returns async results
- transport metadata and permission annotations belong here

## 5. Async Contract

Zero backend code is fully async.

Rules:

- stub, service, and actor methods return `Future<T>`
- do not block on futures
- use `compose`, `map`, `Future.failedFuture`, `Future.succeededFuture`
- do not place blocking I/O in the main agent/actor/service flow

## 6. Agent Rules

- Do not collapse DPA boundaries because a module looks small.
- Do not put transport logic in provider code.
- Do not put business logic in agents.
- Do not put DB access in actors.
- Classify the missing abstraction first: contract, implementation, or transport.
