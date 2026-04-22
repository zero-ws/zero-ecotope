# Zero Extension API Guide

> Load this file when the task is about `zero-extension-api`, reusable API-side extension conventions, or the boundary between extension API scaffolding and concrete exmodule handling.

## 1. Scope

This file owns:

- `zero-extension-api` positioning
- reusable API-side extension responsibility
- what belongs in extension API vs exmodule API

## 2. Owning Modules

- `zero-extension-api`
- consuming exmodule `*-api` modules

## 3. Responsibility Model

`zero-extension-api` owns shared API-side extension conventions:

- routing base patterns
- reusable API support code
- external API-facing extension norms shared across modules

It does not own one domain’s concrete transport behavior.

## 4. AI Agent Rules

- Change `zero-extension-api` only when the API-side convention must be reusable across modules.
- Put module-specific actors, addresses, and transport rules in the matching exmodule API layer.
