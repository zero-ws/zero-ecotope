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

### API-to-`S_ACTION` Completion Rule

For every backend API that is protected by RBAC, the API work is not complete
until the route can be imported as an `S_ACTION`.

The complete route permission chain is:

```text
API endpoint
-> RBAC_RESOURCE action yml
-> BuildPerm imports S_RESOURCE + S_ACTION
-> S_ACTION.permissionId points to S_PERMISSION from PERM.yml
-> RBAC_ROLE / init/oob/RBAC_ROLE grants the permission code
-> runtime authorization can match request method + URI
```

Required files for a protected API:

```text
plugins/{module}/security/RBAC_RESOURCE/{type}/{directory}/{permission}/PERM.yml
plugins/{module}/security/RBAC_RESOURCE/{type}/{directory}/{permission}/{label}@{METHOD}@{uri}.yml
plugins/{module}/security/RBAC_ROLE/{role}/...@code.yml       # framework defaults, when reusable
init/oob/RBAC_ROLE/{role}/...@code.yml                        # application defaults, when app-owned
plugins/zero-exmodule-rbac/configuration.json                 # rolePermissions seed, when used by the app
```

`PERM.yml` creates or updates the permission identity in `S_PERMISSION`.
The sibling action yml creates or updates the route/action row in `S_ACTION`
and the resource row in `S_RESOURCE`.

Do not treat `PERM.yml` alone as sufficient. If the action yml is missing, the
permission code may exist but the request still cannot be matched to an
`S_ACTION`, and the security layer can reject the request with 403.

Action yml file rules:

- filename format: `{label}@{METHOD}@{uri}.yml`
- one protected method + path variant needs one action yml
- path aliases need separate action yml files, for example `/api/license/search`
  and `/api/x-license/search`
- path parameters use the project's normalized filename convention, such as
  `$key` in `_api_instance_$key_health.yml`
- `data.keyword` becomes the action code suffix, commonly stored as
  `act.{keyword}`
- `data.resource` must point to the owning resource type, such as
  `resource.ambient` or `resource.security`
- `data.identifier` is optional; when absent, BuildPerm uses the parent
  `PERM.yml` identifier to bind the action to the permission

When adding or changing an API endpoint, update the permission chain in the
same change set. A route added only in Java/OpenAPI/frontend client code is
not RBAC-complete.

### Role Update Mode

Role handling is append-only unless the task explicitly says to remove or
replace permissions.

When adding a new role or granting a new API permission:

- add a new `RBAC_ROLE/{ROLE_CODE}/...@code.yml` file or append permission
  codes to existing role files
- append new codes to `configuration.json` / `rolePermissions` when that seed is used
- preserve all existing role files and permission codes unless the task is a
  deliberate permission-removal task
- do not rewrite a role directory from a partial desired state
- do not use existing-DB backfill that deletes or truncates role-permission
  relations before inserting the new grants
- for existing databases, insert missing `S_PERMISSION` / role relation rows
  idempotently; never clear unrelated `S_ROLE`, `S_PERMISSION`, or
  role-permission rows as part of adding a role

Reason: role data is cumulative across framework defaults, application seeds,
and deployment-specific additions. Replacing it from one task's local view can
silently remove access and create new 403 failures.

### 403 Diagnosis Checklist

For a 403 on an authenticated request, inspect in this order:

1. Is the method + URI represented by an action yml under `RBAC_RESOURCE`?
2. Does the action yml import into `S_ACTION` with the expected method and URI?
3. Does the action's `permissionId` point to the expected `S_PERMISSION` row
   from the parent `PERM.yml`?
4. Does `S_RESOURCE` contain the expected resource row and `resource` value?
5. Does the user's role grant that permission code through `RBAC_ROLE` or
   `init/oob/RBAC_ROLE`?
6. For existing databases, did `LoadInst` / `BuildPerm` or a controlled
   backfill actually run after the resource files changed?
7. If the frontend uses an alias path, is that exact alias registered as an
   action yml?

Common failure mode:

```text
rolePermissions contains the permission code
but RBAC_RESOURCE has no action yml for the endpoint
=> no matching S_ACTION for method + URI
=> 403
```

Another common failure mode:

```text
action yml exists for /api/license/search
but frontend calls /api/x-license/search
=> S_ACTION path mismatch
=> 403
```

Do not fix this class of 403 by changing service code, SPI implementations, or
controller annotations first. Complete the declarative RBAC resource chain and
rerun the permission import path.

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
