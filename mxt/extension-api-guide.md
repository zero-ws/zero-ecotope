# Zero Extension API Guide

> Load this file when the task is about `zero-extension-api`, reusable API-side extension conventions, or deciding whether an endpoint concern belongs to extension API or to one exmodule.

## 1. Scope

`zero-extension-api` is the shared API-side extension layer.

It owns:

- reusable API-facing conventions shared across exmodules
- shared transport-side helpers that should not be duplicated in each module
- API-side extension support that stays above one specific exmodule

It does not own:

- one exmodule's concrete `Actor` logic
- one module's `Addr` contract
- provider/service implementation
- business semantics that belong to domain/provider modules

## 2. Verified Anchors

The current module is intentionally small. That is evidence of scope, not absence of ownership.

Verified source anchors:

- `zero-extension-api/src/main/java/io/zerows/extension/development/ErrorCode.java`
- `zero-extension-api/src/main/java/io/zerows/extension/development/ErrorList.java`
- `zero-extension-api/src/main/java/io/zerows/extension/development/ErrorTree.java`

Interpretation:

- this layer is used to expose reusable API/development support
- it is not meant to absorb the whole transport layer of every exmodule

## 3. Boundary vs Exmodule API

### Put behavior in `zero-extension-api` when:

- the API convention must be reused across multiple exmodules
- the concern is transport-side and module-neutral
- the change affects a common API support surface

### Put behavior in `zero-exmodule-*-api` when:

- the actor/agent belongs to one module only
- the route/address is module-owned
- the payload semantics belong to one exmodule's domain

## 4. Practical Rule

If the user story sounds like:

- "all exmodule APIs should expose this the same way"
- "the shared API-side extension layer should define this helper"
- "this support class is not tied to one MID"

then `zero-extension-api` is a candidate.

If the story sounds like:

- "workflow actor needs a new route"
- "ambient attachment endpoint needs new behavior"
- "RBAC resource endpoint should change"

then the change belongs in the matching exmodule API module.

## 5. What Not To Put Here

- `ReportActor`, `WorkflowActor`, `AppActor`-style module endpoints
- exmodule-specific `Addr` constants
- domain data assembly
- service logic
- plugin/resource-driven runtime behavior

## 6. Inspection Order

1. `zero-extension-api`
2. matching exmodule `*-api`
3. matching exmodule `*-domain/servicespec`
4. matching exmodule `*-provider`

That order prevents shared API support from being confused with domain implementation.

## 7. Change Rule

Only change `zero-extension-api` when the API-side convention should serve multiple modules.

Otherwise:

- keep transport ownership in exmodule `*-api`
- keep business ownership in `*-provider`
