# Ambient App Runtime Specification

> Load this file when the task is about Ambient APP identity, app package
> interpretation, instance runtime layout, Admin-managed app installation, or
> `R2MO_HOME/apps/**` disk placement.

This document defines the **basic APP specification** owned by the Ambient app
lifecycle. It intentionally focuses on identity, ownership, and runtime disk
placement. Post-packaging archive layout is covered separately by
`app-package-spec.md` and is not expanded here.

## 1. Vocabulary

| Term | Meaning |
|---|---|
| APP / `appId` | Stable application UUID. It is the database key for `X_APP` and must satisfy `X_APP.ID == APP_ID`. App code/name is metadata only. |
| Instance / `instanceId` | Runtime instance grouping UUID below a tenant. It is the first app-level directory under `R2MO_HOME/apps/{tenant}`. |
| Main App | The app that owns an instance group. For a single main app, `instanceId == appId == X_APP.ID == APP_ID`. |
| Child App | An app deployed under the same instance group as the main app. It shares `{tenant}/{instanceId}` and gets its own child `{appId}` directory. |
| Instance Name | Human-readable/runtime selector such as `HMS-001`. It is written to `.r2mo/instance`; it is not the directory identity unless explicitly mapped. |
| Package Store | Uploaded/extracted immutable app package content under `R2MO_HOME/store/{tenant}/apps/{appId}`. |
| Runtime Root | Generated mutable runtime content under `R2MO_HOME/apps/{tenant}/{instanceId}/{appId}`. |

## 2. Identity Contract

Ambient and Admin must keep these identities separate:

```text
X_APP.ID       == APP_ID == appId
instanceId    == runtime grouping id
instanceName  == display/runtime selector
```

Tenant, app, and instance identities are UUID values. Names are mutable and must not be used as path or database identity.

Main-app rule:

```text
instanceId == appId == X_APP.ID == APP_ID
```

Therefore a single AISZ main app resolves by UUID to:

```text
R2MO_HOME/apps/{tenantUuid}/{appUuid}/{appUuid}/
```

Composite app rule:

```text
R2MO_HOME/apps/{tenant}/{instanceId}/{mainAppId}/
R2MO_HOME/apps/{tenant}/{instanceId}/{childAppIdA}/
R2MO_HOME/apps/{tenant}/{instanceId}/{childAppIdB}/
```

The first `{instanceId}` segment is the instance grouping id, not a display
name. The second `{appId}` segment is always the concrete application id and is
directly queryable from `X_APP` by `ID == APP_ID`.

## 3. Runtime Environment Contract

Minimum runtime identity variables:

| Variable | Required | Meaning |
|---|---:|---|
| `R2MO_HOME` | Yes | Target Cloud/runtime root. |
| `Z_TENANT` | Yes | Tenant UUID. |
| `Z_APP_ID` | Yes | Concrete app UUID; equals `APP_ID` and `X_APP.ID`. |
| `Z_INSTANCE_ID` | Recommended | Runtime instance grouping UUID. Defaults to `Z_APP_ID` for a main app. |
| `Z_SIGMA` | Yes | Runtime sigma/business domain identity. |
| `R2MO_INSTANCE` | Recommended | Human-readable instance name, e.g. `HMS-001`. |
| `Z_API_PORT` | App-specific | Backend/API port when the app exposes one. |

Compatibility aliases may exist in downstream scripts, but new framework and
Admin docs should use the names above. If `Z_INSTANCE_ID` is absent, Admin may
normalize a main-app deployment as:

```text
Z_INSTANCE_ID=${Z_APP_ID}
```

## 4. Deployment input boundary

Admin and downstream App deployment inputs must come from `deployment/**` artifacts only, except executable scripts. Scripts may live in `bin/` or `deployment/bin/`, but release descriptors, env templates, manifests, checksums, upload packages, and publishable metadata must be read from `deployment/**`. Do not read deploy-time identity from local source resources, developer `.r2mo` folders, or display names.

## 5. Canonical Disk Layout

`R2MO_HOME` is the target host root. Ambient-aware deployment uses four top-level
areas:

```text
R2MO_HOME/
  admin/                         # Current Admin management app runtime
  apps/{tenant}/{instanceId}/{appId}/
  store/{tenant}/apps/{appId}/   # package store / extracted package content
  deployment/                    # target-local deployment workspace
```

### 5.1 Framework-layer persistence rules

Framework/Ambient owns reusable metadata and lifecycle contracts, not
application-specific business files:

- Persist app registry by `X_APP.ID == APP_ID`.
- Persist package/source metadata through Ambient app lifecycle data such as
  `X_APP`, `X_SOURCE`, and attachment/document records where applicable.
- Store uploaded or extracted package bytes under
  `R2MO_HOME/store/{tenant}/apps/{appId}`.
- Generate or instruct runtime placement under
  `R2MO_HOME/apps/{tenant}/{instanceId}/{appId}`.
- Do not hard-code downstream business app names, instance names, or tenant
  examples into framework code.
- Treat legacy `apps/{tenant}/{appId}` descriptors as main-app shorthand only;
  normalize them to `apps/{tenant}/{appId}/{appId}` at install/deploy time.

### 5.2 Admin management-side persistence rules

The current Admin management app owns orchestration on the target host:

- Its own runtime is always `R2MO_HOME/admin/`.
- It parses package metadata from `deployment/**` artifacts, resolves UUID `appId`, and queries/persists app records
  using `X_APP.ID == APP_ID`.
- It computes UUID `instanceId`; for a main app, default `instanceId = appId`.
- It creates `R2MO_HOME/apps/{tenant}/{instanceId}/{appId}` for app runtimes.
- It renders runtime env/config files into the app runtime root, commonly
  `.r2mo/app.env` plus `.r2mo/instance`.
- It may start containerized apps by mounting the whole `R2MO_HOME` into the
  container, while keeping package storage and runtime storage separate.

### 5.3 `R2MO_HOME` persistence rules

`R2MO_HOME` is a host-level contract, not a repository checkout:

- `admin/` is reserved for the current Admin app runtime.
- `apps/` is reserved for generated managed app runtime state.
- `store/` is reserved for app packages, extracted package contents, attachment
  or object-cache material, organized by tenant and app id.
- `deployment/` is a target-local workspace for manuals, scripts, packages, and
  temporary deployment cache; it is not an app runtime root.
- Source repositories may generate artifacts, but long-lived runtime state must
  be copied/rendered into `R2MO_HOME` by Admin/deployment tooling.

### 5.4 Downstream app repository rules (`app-aisz` example)

An app repository owns its own build output and app-specific release contract.
It does not own Cloud bootstrap, Admin runtime layout, or global infra layout.

For `/Users/lang/zero-cloud/app-zero/r2mo-apps/app-aisz`:

- Source app code/image base: `aisz-app-hotel`; deploy-time main app id must be the UUID stored in `X_APP.ID`.
- Main app runtime root:
  `R2MO_HOME/apps/{tenantUuid}/{appUuid}/{appUuid}/`.
- Default human instance name may remain `HMS-001`, written to
  `.r2mo/instance`; it is not the instance id segment.
- Preferred runtime template expression:
  `apps/{tenant}/{instanceId}/{appId}`.
- Existing `apps/{tenant}/{appId}` descriptors are compatibility shorthand for
  main-app deployments only and must not be copied as the canonical structure
  into new docs.
- The app repository may provide `manifest.json`, `app.yaml`, image tar, and
  runtime templates. Admin decides the final host path and injects environment
  values at deployment time.

## 6. MCP reading rule for APP topics

When another project connects through `mxt-zero` and asks about app runtime,
package install, app registry, or `R2MO_HOME/apps/**`:

1. Read this file first for identity and disk-placement rules.
2. Read `app-package-spec.md` only when archive/package contents are in scope.
3. Read `exmodule-ambient-guide.md` for Ambient ownership and app registry
   boundaries.
4. Read `environment-contracts.md` for env variable category boundaries.
5. Verify source or downstream project scripts only after the owner rule is
   selected.
