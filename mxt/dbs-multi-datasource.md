# DBS Multi Data Source Guide

> Load this file when the task is about `DBSActor`, default vs named data sources, static vs dynamic data-source registration, or jOOQ context selection in Zero.

## 1. Scope

This file owns:

- `DBSActor` lifecycle and responsibility
- static vs dynamic data-source setup
- default and named `DBS` lookup rules
- context binding for jOOQ access

It does not own query syntax details. Use `dbe-query-rules.md` for DBE composition.

## 2. Owning Modules

- `zero-epoch-store`
- `zero-plugins-flyway` when migration configuration depends on selected data source

## 3. Key Anchors

- `io.zerows.epoch.store.DBSActor`
- `DBMany`
- `DBContext`
- `DBSActor.ofDBS()`
- `DBSActor.ofDBS(name)`
- `DBSActor.ofContext()`
- `DBSActor.ofContext(database)`

## 4. Runtime Model

`DBSActor` is the framework owner for database registration and lookup.

Supported modes:

- static mode: one default `DBS`
- dynamic mode: multiple named `DBS` instances registered from configuration

The actor also prepares the underlying `DBContext` bridge used by higher-level database helpers and async jOOQ integration.

## 5. AI Agent Rules

- If a task mentions multiple databases, inspect `DBSActor` first.
- Distinguish “default database selection” from “named database lookup”.
- When Flyway points at the wrong database, verify `DBSActor` selection before editing SQL or migration locations.
