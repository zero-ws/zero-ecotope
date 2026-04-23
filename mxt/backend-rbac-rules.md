# Backend RBAC Rules

> Load this file when the task is about RBAC resources, permission mapping, declarative row filtering, or backend security resource ownership.

## 1. Scope

This file owns:

- RBAC resource ownership
- permission chain semantics
- API-to-resource mapping
- row and field permission resource rules

It does not own:

- generic tenant rules
- generic DPA structure
- OAuth2 startup behavior

## 2. Permission Chain

Zero permission chain:

```text
User -> Group -> Role -> Resource -> Action -> API
```

## 3. Resource Ownership

Permission resources live in domain resources:

```text
plugins/{module}/security/RBAC_RESOURCE/
plugins/{module}/security/RBAC_ROLE/
plugins/{module}/security/RBAC_ADMIN/
```

## 4. API Mapping

At API and OpenAPI level, map endpoints to `{resource, action}`.

At code level, attach permission metadata in API classes.

Typical action names:

- `create`
- `read`
- `search`
- `update`
- `delete`
- `import`
- `export`

## 5. Declarative Data Permission

Use:

- `seekSyntax.json` for row filters
- `dmConfig.json` for field-level permission

`seekSyntax.json` uses DBE criteria syntax and placeholders such as:

- `${sigma}`
- `${userId}`
- `${orgId}`
- `${viewId}`

Rule:

```text
Prefer declarative permission resources over handwritten permission predicates in services.
```

## 6. Agent Rules

- If permission behavior is wrong, inspect resource trees before changing service code.
- If row filtering is wrong, inspect `seekSyntax.json` before changing DAO logic.
- If field visibility is wrong, inspect `dmConfig.json` before changing DTOs or forms.
- Do not move RBAC resource semantics into imperative code unless declarative resources cannot express the requirement.
