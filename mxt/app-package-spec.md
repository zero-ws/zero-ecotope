# Ambient App Package Specification

This document defines the release package contract consumed by the Ambient app
lifecycle (`prepare -> init -> deploy`) and by second-admin style app stores.
For APP identity, instance semantics, and runtime disk placement, read
`ambient-app-runtime-spec.md` first.
It is based on the parsed package:

```text
~/zero-store/apps/ht/aisz-app-hotel/app-aisz-upload.tar.gz
```

## 1. Package format

- Archive type: `tar.gz` (`.tar.gz` or `.tgz`).
- The archive must be extractable without absolute paths or `..` path traversal.
- The root of the archive is the package root; do not add an extra parent
  directory unless all manifest paths include that parent.

Required top-level entries:

```text
app.yaml
manifest.json
image/{appId-or-image-name}.tar
bundle/release/runtime/**
bundle/release/.r2mo/app.env
bundle/release/.r2mo/instance
checksums/image.sha256
checksums/bundle-release.sha256
```

Recommended top-level entries:

```text
README.md
bundle/release/apps/**
```

## 2. `manifest.json`

`manifest.json` is the machine contract. Ambient/package-aware Admin code must
read it from `deployment/**` package artifacts before `app.yaml`; deployment inputs outside `deployment/**` are invalid except executable scripts.

Identity values in package metadata (`appId`, `tenantId`, `instanceId`) are UUIDs. Names, codes, and titles are descriptive metadata only and must not drive runtime paths.

Required fields:

| Field | Meaning |
| --- | --- |
| `appId` | Stable application UUID and X_APP key. App code/name such as `aisz-app-hotel` is metadata, not identity. |
| `appName` | Human-readable display title, e.g. `AISZ Hotel`. |
| `image` | Docker image reference to run, e.g. `aisz-app-hotel:latest`. |
| `runtimeRoot` | Runtime path template. Preferred value: `apps/{tenant}/{instanceId}/{appId}`. Legacy `apps/{tenant}/{appId}` is interpreted as main-app shorthand and normalized by Admin. |
| `runtimeTemplate` | Runtime template path inside archive, normally `bundle/release/runtime`. |
| `envTemplate` | Env template path inside archive, normally `bundle/release/.r2mo/app.env`. |

Recommended fields:

| Field | Meaning |
| --- | --- |
| `defaultInstance` | Default instance name, e.g. `HMS-001`. |
| `instanceMode` | Runtime value strategy, e.g. `runtime-provided`. |
| `mainClass` | Application entry class. |
| `deployMainClass` | Optional deploy/data-load entry class. |
| `appPort` | Container application/web port. |
| `backendPort` | Internal backend/API port, mapped to `Z_API_PORT`. |
| `deploySteps` | Ordered lifecycle labels such as `mount-runtime`, `inject-app-env`, `run-load`, `start-container`. |
| `uploadLayout` | Package layout label, e.g. `slim`. |
| `resourceRoot` | Resource root relative to package root. |
| `builtAt` | Build timestamp. |

Observed valid example:

```json
{
  "appId": "aisz-app-hotel",
  "appName": "AISZ Hotel",
  "image": "aisz-app-hotel:latest",
  "defaultInstance": "HMS-001",
  "instanceMode": "runtime-provided",
  "mainClass": "io.zerows.apps.hotel.AISZApp",
  "deployMainClass": "io.zerows.apps.hotel.AISZDeployLoad",
  "appPort": 7005,
  "backendPort": 7185,
  "runtimeRoot": "apps/{tenant}/{instanceId}/{appId}",
  "runtimeTemplate": "bundle/release/runtime",
  "envTemplate": "bundle/release/.r2mo/app.env",
  "deploySteps": ["mount-runtime", "inject-app-env", "run-load", "start-container"],
  "uploadLayout": "slim",
  "resourceRoot": "."
}
```

Observed AISZ values such as `aisz-app-hotel` are app code/image metadata in this example; production `appId` / `tenantId` / `instanceId` values must be UUIDs.

Compatibility note: packages already built with `"runtimeRoot": "apps/{tenant}/{appId}"` are treated as main-app shorthand and normalized to `apps/{tenant}/{instanceId}/{appId}` with `instanceId = appId`.

## 3. `app.yaml`

`app.yaml` is the human/declarative descriptor. It should duplicate the critical
runtime contract but may contain `${...}` placeholders.

Required semantics:

```yaml
appId: ${Z_APP_ID}
tenantId: ${Z_TENANT}
sigma: ${Z_SIGMA}
appName: AISZ Hotel
appCode: aisz-app-hotel
image: aisz-app-hotel:latest
instance: ${R2MO_INSTANCE}
instanceId: ${Z_INSTANCE_ID}
runtimeDir: ${R2MO_HOME}/apps/${Z_TENANT}/${Z_INSTANCE_ID}/${Z_APP_ID}
runtimeTemplate: release/runtime
envTemplate: release/.r2mo/app.env
ports:
  web: 7005
  backend: ${Z_API_PORT}
startup:
  mainClass: io.zerows.apps.hotel.AISZApp
  mode: container
deploy:
  initMainClass: io.zerows.apps.hotel.AISZDeployLoad
  runtimeMode: generated-on-deploy
```

## 4. Storage layout

Ambient must separate uploaded package storage from generated runtime storage.

### Tenant package store

Large app package uploads and installed package contents belong under:

```text
${R2MO_HOME}/store/{tenant_id}/apps/{app_id}/
```

When `R2MO_HOME` is absent in local development, the project-local fallback is:

```text
runtime/store/{tenant_id}/apps/{app_id}/
```

After package install/extract, this store root should contain the package root:

```text
${R2MO_HOME}/store/{tenantUuid}/apps/{appUuid}/manifest.json
${R2MO_HOME}/store/{tenantUuid}/apps/{appUuid}/app.yaml
${R2MO_HOME}/store/{tenantUuid}/apps/{appUuid}/image/aisz-app-hotel.tar
${R2MO_HOME}/store/{tenantUuid}/apps/{appUuid}/bundle/release/runtime/**
${R2MO_HOME}/store/{tenantUuid}/apps/{appUuid}/bundle/release/.r2mo/app.env
```

The prepared app metadata should retain:

- `packagePath` — original archive path when known.
- `packageStoreRoot` — extracted tenant store root.
- `runtimeTemplate` — copied on deploy.
- `envTemplate` — rendered on deploy.

### Runtime directory

Generated runtime files belong under:

```text
${R2MO_HOME}/apps/{tenant_id}/{instance_id}/{app_id}
```

For the main app instance, `instance_id == app_id == X_APP.ID == APP_ID`, so the
single-main-app runtime directory is:

```text
${R2MO_HOME}/apps/{tenant_id}/{app_id}/{app_id}
```

For a composite instance, `{instance_id}` is the shared main app id and each
child application occupies its own `{app_id}` directory below that instance:

```text
${R2MO_HOME}/apps/{tenant_id}/{instance_id}/{main_app_id}
${R2MO_HOME}/apps/{tenant_id}/{instance_id}/{child_app_id_1}
${R2MO_HOME}/apps/{tenant_id}/{instance_id}/{child_app_id_2}
```

`instanceName` remains a display/runtime selector written to `.r2mo/instance`;
it is not the directory identity unless explicitly mapped into `instanceId`.

## 5. Lifecycle contract

### Upload / onboard

1. Upload `.tar.gz` through resumable upload or a direct package path.
2. Resolve package bytes to a local archive path or attachment-backed file path.
3. Parse `manifest.json`.
4. Extract package to tenant package store.
5. Create/prepare X_APP data using manifest values:
   - `id/appId/key = manifest.appId`
   - `code = manifest.appId`
   - `name/title = manifest.appName`
   - `metadata = manifest + packageStoreRoot/packagePath`
6. Build `source` from environment/tenant DB configuration and include package
   metadata under `source.metadata`.

### Download / install

The metadata export endpoint (`/api/app/export`) is not the package download
contract. Package download should use the attachment/file download endpoint when
serving uploaded archives, with HTTP Range support when available.

Installation is `prepare -> init`:

- `prepare` returns X_APP + `source` + package metadata.
- `init` persists X_APP/X_SOURCE and keeps package metadata available for deploy.

### Single-app instance deploy

`/api/instance/deploy` or `/api/app/deploy` receives:

```json
{
  "appId": "<app-uuid>",
  "instanceId": "<app-uuid>",
  "instanceName": "HMS-001",
  "compositeDeploy": false,
  "manifest": { "...": "metadata from manifest.json" },
  "source": { "...": "X_SOURCE/db metadata" }
}
```

Deploy must:

1. Provision database when `source` is present.
2. Create `${R2MO_HOME}/apps/{tenant}/{instanceId}/{appId}`. For the main app, `instanceId` defaults to `appId`; for child apps it must be supplied by `manifest.instanceId` / `mainAppId`.
3. Copy `manifest.runtimeTemplate` into runtime root.
4. Render `manifest.envTemplate` to `${runtimeRoot}/.r2mo/app.env`.
5. Write `${runtimeRoot}/.r2mo/instance` with `instanceName`.
6. Locate image tar from `${R2MO_HOME}/store/{tenant}/apps/{appId}/image/*.tar`.
7. `docker load` the image tar when present.
8. Start the container with:
   - `Z_TENANT`
   - `Z_APP_ID`
   - `Z_SIGMA`
   - `Z_INSTANCE_ID` (defaults to `Z_APP_ID` for a main app)
   - `R2MO_INSTANCE`
   - `Z_API_PORT` when `backendPort` is known
   - mount `${R2MO_HOME}` to `/opt/r2mo`.

### Multi-app/composite deploy

Composite deployment remains a higher-level orchestration over multiple child
single-app deployments. The package format may include `children`, `childApps`,
`apps`, or `modules`, but this is deferred unless the task explicitly targets
multi-app packages.

## 6. Environment template rules

`bundle/release/.r2mo/app.env` may contain `${...}` placeholders. The deployer
must at least render:

- `Z_TENANT`
- `Z_SIGMA`
- `Z_APP_ID`
- `Z_INSTANCE_ID`
- `R2MO_INSTANCE` (same as `Z_INSTANCE_ID` by default)
- `R2MO_INSTANCE_NAME`
- `R2MO_HOME`
- `Z_DB_HOST`
- `Z_DBS_HOST` (default from `Z_DB_HOST` when absent)
- `Z_DB_USERNAME`
- `Z_DB_PASSWORD`
- `Z_DB_APP_USER`
- `Z_DB_APP_PASS`
- `Z_DBS_INSTANCE`
- `Z_DBW_INSTANCE`

The AISZ package exposed a compatibility mismatch: app env uses
`Z_DB_HOST`, while runtime `vertx.yml` may read `Z_DBS_HOST`. Admin-side deploy
should inject or derive `Z_DBS_HOST` from `Z_DB_HOST` to avoid runtime datasource
host drift.

## 7. Checksum expectations

If checksum files are present, validators should verify them before install:

```text
checksums/image.sha256          -> image/*.tar
checksums/bundle-release.sha256 -> bundle/release/** content digest
```

Checksum validation can be introduced as a stricter step without changing the
layout contract.
