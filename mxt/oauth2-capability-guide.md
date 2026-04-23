# OAuth2 Capability Guide

> Load this file when the task is about `zero-plugins-oauth2`, OAuth2 server/client capability, registered client persistence, or OAuth2 runtime config.

## 1. Scope

This file owns the OAuth2 plugin capability.

It owns:

- `zero-plugins-oauth2`
- OAuth2 server/client actor anchors
- OAuth2 runtime metadata and manager behavior
- registered-client persistence surface

It does not own:

- security adapter provider composition
- generic security plugin flow
- OAuth2 Flyway initialization order

## 2. Owning Module

- `zero-plugins-equip/zero-plugins-oauth2`

Verified graph/source anchors:

- `OAuth2ServerActor`
- `OAuth2ClientActor`
- `OAuth2Manager`
- `OAuth2Config`
- `OAuth2Security`
- `Oauth2RegisteredClientDao`
- `OAuth2Flyway`

## 3. Responsibility Model

OAuth2 capability is split across runtime config, server boot, client registration, and database-backed OAuth2 records.

Use:

- `oauth2-capability-guide.md` for capability ownership
- `oauth2-init-flow.md` for Flyway and startup dependency order
- `security-plugin-flow.md` for `zero-plugins-security-oauth2`

One-line rule:

```text
`zero-plugins-oauth2` owns OAuth2 capability; security-oauth2 owns auth-provider adaptation.
```

## 4. AI Agent Rules

- Start from `OAuth2ServerActor` for server-side runtime config.
- Start from `OAuth2ClientActor` for registered-client sync.
- Check `oauth2-init-flow.md` before changing client persistence or schema assumptions.
- Do not mix `zero-plugins-oauth2` with `zero-plugins-security-oauth2`.
