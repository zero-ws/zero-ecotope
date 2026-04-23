# BuildApp and BuildPerm Flow

> Load this file when the task is about app/menu bootstrap, RBAC resource import, or installation-time data loading in Zero boot.

## 1. Scope

This file owns the execution model for:

- `BuildApp`
- `BuildPerm`
- their loader and persister chains
- installation-time ordering in boot tools

It does not own:

- generic actor startup semantics
- Flyway infrastructure rules
- OAuth2 actor initialization

## 2. Installation Order

The main installation order in `LoadInst` is:

1. `BuildApp.run(vertx)`
2. optional data import through `DataImport`
3. `BuildPerm.run(vertx)`

`MenuInst` runs only:

1. `BuildApp.run(vertx)`

## 3. `BuildApp`

`BuildApp.run(...)` is the app and menu bootstrap entry.

Actual implementation classes are:

- `BuildMenuLoader`
- `BuildMenuPersister`

Important note:

- older javadoc references such as `BuildAppMenuLoader` and `BuildAppMenuPersister` are stale names
- use the real class names above as search anchors

Rule:

```text
Search by implementation class, not by stale javadoc labels.
```

### Effective Flow

1. load global config
2. resolve app root and cache directory
3. load `instance.yml` mappings
4. load `init` config
5. merge cache marker mappings
6. scan app YAML files and menu directories through `InstApps`
7. load `XApp` and `XMenu`
8. persist `XApp` first
9. persist `XMenu` second
10. regenerate cache markers, `instance.yml`, and menu cache files
11. refresh the running ambient app registry

### Identity Rules

- `XApp` is stabilized by app ID
- `XMenu` is stabilized by `NAME + APP_ID`
- `BuildMenuPersister.upsertMenu(...)` enforces the `NAME + APP_ID` identity rule against stored menu rows.

## 4. `BuildPerm`

`BuildPerm.run(...)` is the RBAC bootstrap entry.

It scans:

- `plugins/{MID}/security/RBAC_RESOURCE`
- `plugins/{MID}/security/RBAC_ROLE`

### Loader Rules

`BuildPermLoader` uses a required two-stage load:

1. load all `PERM.yml`
2. load actions and resources after permission identities are known

Role IDs are loaded from the database before role-permission relations are loaded.

### Persistence Order

Persistence order is mandatory:

1. save permissions
2. remap permission-dependent IDs
3. save resources
4. remap resource-dependent IDs
5. save actions
6. save role-permission relations

The implementation enforces this order inside one chained async flow rather than independent loaders.

### Idempotency Rules

- `SPermission` / `SAction` / `SResource` use `code + appId`
- menu persistence uses `NAME + APP_ID`

## 5. Agent Rules

- Do not reorder the `BuildPerm` save chain casually; ID remapping depends on the current sequence.
- Do not search for `BuildAppMenuLoader`; use `BuildMenuLoader`.
- If app/menu cache behavior looks wrong, inspect generated cache artifacts together with database upsert logic.
- If RBAC import looks wrong, inspect both resource trees and database role lookup before changing persistence code.
- If bootstrap output looks incomplete, inspect scanners and resource trees before assuming persistence is wrong.
