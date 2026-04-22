# Trash Capability Guide

> Load this file when the task is about `zero-plugins-trash`, recycle-bin behavior, soft-delete support, or trash plugin capability.

## 1. Scope

This file owns `zero-plugins-trash`.

It owns:

- trash plugin startup
- trash client and provider wiring
- recycle-bin capability anchors
- plugin-level soft-delete support

It does not own:

- domain deletion policy
- retention policy for a specific business module
- RBAC rules for delete actions

## 2. Owning Module

- `zero-plugins-equip/zero-plugins-trash`

Verified graph/source anchors:

- `TrashActor`
- `TrashAddOn`
- `TrashBuilder`
- `TrashClient`
- `TrashClientImpl`
- `TrashManager`
- `TrashProvider`

## 3. Responsibility Model

Trash is an infrastructure capability for recycle-bin style operations.

It can support:

- capturing delete payloads
- exposing a trash client
- wiring plugin provider behavior

It should not decide:

- whether a domain should soft-delete
- how long deleted data should be retained
- which role can restore or purge data

## 4. AI Agent Rules

- Start from `TrashActor` for boot behavior.
- Use `TrashClient` and `TrashProvider` for capability behavior.
- Route domain deletion semantics to the owning exmodule or app layer.
