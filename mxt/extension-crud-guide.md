# Zero Extension CRUD Guide

> Load this file when the task explicitly targets `zero-extension-crud`, standard CRUD transport, `MDCRUDManager`, `Ix*`, `Pre*`, `Agonic*`, or reusable mutation/view pipeline behavior.

## 1. Scope

`zero-extension-crud` owns the reusable CRUD delivery engine.

It owns:

- CRUD module boot and registry
- metadata-driven route assembly
- request preprocessing
- standard create/update/delete/import/view execution chains
- reusable doc/file/export helpers tied to the CRUD engine

It does not own:

- one exmodule's domain behavior
- generic SPI vocabulary
- low-level DBE syntax itself

## 2. Verified Anchors

Confirmed source anchors:

- `boot/PrimedCRUD.java`
- `boot/MDCRUDActor.java`
- `boot/MDCRUDManager.java`
- `boot/IxSetup*.java`
- `common/Ix.java`, `IxConfig.java`
- `api/PostActor.java`, `PutActor.java`, `DeleteActor.java`, `ViewActor.java`, `FileActor.java`
- `uca/AgonicAop.java`
- `uca/AgonicADBCreate.java`
- `uca/AgonicADBUpdate.java`
- `uca/AgonicADBDelete.java`
- `uca/AgonicADBImport.java`
- `uca/AgonicView*.java`
- `uca/input/PreAudit*.java`
- `uca/input/PreFile*.java`
- `uca/input/PreExcel.java`
- `plugins/DocExtensionActor.java`
- `plugins/ExDefaultCRUD.java`

## 3. Execution Model

The extension is organized roughly like this:

```text
metadata/config -> MDCRUDManager / IxSetup*
request normalization -> Pre*
operation execution -> Agonic* / Operate*
shared reply/view/doc helpers -> Ix* / DocExtensionActor
```

So when behavior is wrong, debug the pipeline stage first instead of patching the outer actor immediately.

## 4. Where To Change What

### Change `boot/*` when:

- module discovery is wrong
- CRUD configuration is not loading
- route/module registry ownership is wrong

### Change `Pre*` when:

- request normalization is wrong
- audit/user/id/file/excel preprocessing should be reusable
- multiple CRUD operations need the same pre-step

### Change `Agonic*` or `Operate*` when:

- reusable standard execution behavior is wrong
- the create/update/delete/import/view flow itself is wrong
- before/after execution around standard mutations needs change

### Change exmodule/provider code instead when:

- the rule is business-domain specific
- the flow goes beyond standard CRUD semantics
- orchestration is the main value

## 5. AOP In CRUD

`zero-extension-crud` already has its own before/after execution layer.

Verified anchors:

- `AgonicAop`
- comments and flow points in `AgonicADBCreate`
- `AgonicADBUpdate`
- `AgonicADBDelete`
- `AgonicADBImport`
- `AgonicHelper`

Do not confuse this with Spring AspectJ. In Zero, this is pipeline-owned mutation AOP.

Use `extension-aop-guide.md` when the issue becomes explicitly about hook ordering or before/after responsibility.

## 6. Decision Rule

If the user asks for:

- a reusable change across many CRUD-backed modules
- file/audit/import/view preprocessing changes
- route behavior derived from metadata

start in `zero-extension-crud`.

If the user asks for:

- workflow-specific orchestration
- RBAC-specific authorization logic
- finance/report generation logic

start in the matching exmodule.
