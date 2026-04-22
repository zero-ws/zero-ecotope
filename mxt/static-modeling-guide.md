# Static Modeling Guide

> Load this file when the task is about the static modeling chain spanning CRUD metadata, UI form generation, and MBSE modeling support.

## 1. Scope

This file owns the composite scenario:

- static modeling chain
- CRUD metadata as model definition
- UI-side form rendering alignment
- MBSE modeling participation where required

## 2. Owning Modules

- `zero-extension-crud`
- `zero-exmodule-ui`
- `zero-exmodule-mbsecore`

## 3. Key Anchors

- `entity.json`
- `column.json`
- `UiForm`
- UI form services
- MBSE core data/model services

## 4. AI Agent Rules

- Use CRUD metadata first for static model expression.
- Use UI exmodule when the concern is backend-owned UI configuration.
- Use MBSE core when the concern is modeling-core semantics rather than CRUD-only metadata.
