# Environment Contracts

> Load this file when the task is about runtime identity, registry/config discovery, tenant context, or environment-level integration contracts.

## 1. Scope

This file owns:

- environment variables as architecture contracts
- registry and config-center keys
- app runtime identity keys
- tenant and database environment categories

It does not own:

- backend DPA mechanics
- frontend client implementation
- contract source authority

## 2. Core Rule

Environment variables are architecture contracts, not deployment trivia.

Generation, bootstrap, and runtime alignment should preserve environment naming and category boundaries.

## 3. Categories

### Registry and Config Discovery

- `R2MO_NACOS_ADDR`
- `R2MO_NACOS_API`
- `R2MO_NACOS_USERNAME`
- `R2MO_NACOS_PASSWORD`

### App Runtime Identity

For Ambient-managed app runtimes, `Z_TENANT`, `Z_APP_ID`, and `Z_INSTANCE_ID` are UUIDs. `Z_APP_ID` is the concrete app UUID and must equal `X_APP.ID`. `Z_INSTANCE_ID` is the runtime grouping UUID under `R2MO_HOME/apps/{tenant}`; for a main app it defaults to `Z_APP_ID`. Names/codes/titles are metadata only.

- `Z_NS`
- `R2MO_INSTANCE`
- `Z_APP_ID`
- `Z_INSTANCE_ID`
- `Z_APP`
- `Z_API_PORT`
- `Z_SOCK_PORT`

### Tenant and Business Context

- `Z_TENANT`
- `Z_SIGMA`

### Database Contract

- `Z_DB_TYPE`
- `Z_DBS_INSTANCE`
- `Z_DBW_INSTANCE`
- `Z_DB_APP_USER`
- `Z_DB_APP_PASS`

## 4. Agent Rules

- Keep these variables grouped by concern in generated templates and runtime docs.
- Do not rename or flatten categories casually.
- If runtime behavior is wrong, inspect environment identity before patching business logic.

## 5. Deployment Input Boundary

Admin and App deployment inputs must come from `deployment/**` artifacts, except executable scripts. Env templates, release descriptors, manifests, checksums, and app packages are deployment inputs and must not be sourced from app source resources or display names.
