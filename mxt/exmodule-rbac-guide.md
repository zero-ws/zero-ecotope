# Exmodule RBAC Guide

> Load this file when the task is about `zero-exmodule-rbac`, reusable RBAC domain ownership, or the module boundary between security plugins and RBAC business semantics.

## 1. Scope

This file owns `zero-exmodule-rbac`.

It owns:

- reusable RBAC business-module ownership
- role, group, user, and resource domain positioning
- the module boundary between auth capability and RBAC domain semantics
- `seekConfig` / `seekSyntax` as RBAC-domain resource attributes

It does not own:

- generic protocol authentication flow
- OAuth2 startup order
- generic permission resource authoring rules for all modules

## 2. Owning Modules

- `zero-plugins-extension/zero-exmodule-rbac`
- `zero-exmodule-rbac-api`
- `zero-exmodule-rbac-domain`
- `zero-exmodule-rbac-provider`

Verified source anchors:

- `zero-exmodule-rbac/pom.xml`
- `zero-exmodule-rbac-domain/.../tables/SResource.java`
- `zero-exmodule-rbac-domain/.../tables/daos/SResourceDao.java`
- `zero-exmodule-rbac-provider/.../authorization/ScDetent.java`
- `plugins/zero-exmodule-rbac/security/RBAC_RESOURCE/`

## 3. Responsibility Model

`zero-exmodule-rbac` is the reusable business module that turns low-level auth capability into reusable authorization semantics.

It owns:

- RBAC domain data and APIs
- resource, role, group, and user authorization models
- reusable authorization inheritance and detent logic
- resource-level row/field filtering attributes stored on RBAC resources

Security plugins still own:

- protocol adapters
- credential transport
- auth-provider integration

One-line rule:

```text
Security plugins authenticate; `zero-exmodule-rbac` authorizes.
```

## 4. Source-Proven Anchors

Graph and source verification show:

- `SResourceDao.findManyBySeekSyntax(...)` exists in the RBAC domain DAO surface
- `SResource` stores both `SEEK_CONFIG` and `SEEK_SYNTAX`
- `ScDetent` in the provider layer owns reusable authorization detent composition
- `plugins/zero-exmodule-rbac/security/RBAC_RESOURCE/...` contains declarative RBAC resources

Interpretation:

- `seekSyntax` is not only a generic security concept; it is modeled as RBAC resource data
- RBAC inheritance and detent logic belongs in the exmodule provider layer, not in generic auth plugins
- the RBAC exmodule is the owner when the task is about authorization domain behavior

## 5. Boundary Rules

Put behavior in `zero-exmodule-rbac` when it is:

- reusable authorization-domain logic
- role/group/resource composition
- ACL and data-domain authorization semantics
- RBAC-side SPI implementations

Do not put behavior here when it is:

- login transport
- protocol-specific auth flow
- generic plugin bootstrap
- one application's private permission customization without reuse value

## 6. Companion Documents

Use this file together with:

- `backend-rbac-rules.md` for declarative permission resource rules
- `security-plugin-flow.md` for security plugin composition
- `acl-authorization-guide.md` for ACL and reinforced authorization as a composite topic

## 7. AI Agent Rules

- Read this file first when the user names `-rbac` or `zero-exmodule-rbac`.
- Switch to `backend-rbac-rules.md` when the task is about `RBAC_RESOURCE`, `RBAC_ROLE`, `seekSyntax`, or `dmConfig` authoring rules.
- Switch to `security-plugin-flow.md` when the issue is about authentication or protocol providers.
