# Zero Extension AOP Guide

> Load this file when the task explicitly mentions AOP, `Aspect`, `Before`, `After`, `Around`, hook ordering, before/after plugins, or cross-cutting execution inside Zero extension modules.

## 1. Zero Has More Than One AOP Layer

Do not treat "AOP" as one thing in Zero.

| Layer | Owner | Verified anchors |
|---|---|---|
| framework generic AOP pipeline | `zero-overlay` | `Aspect`, `Before`, `After`, `Around`, `AspectComponent`, `AspectConfig`, `EmAop` |
| CRUD mutation before/after pipeline | `zero-extension-crud` | `AgonicAop`, `AgonicADBCreate`, `AgonicADBUpdate`, `AgonicADBDelete`, `AgonicADBImport` |
| modeling plugin before/after hooks | `zero-exmodule-mbsecore` | `AspectPlugin`, `BeforePlugin`, `AfterPlugin`, `IoArranger` |
| ACL/business-local before/after semantics | matching exmodule provider | `SyntaxAop`, `QuestAcl`, `UTicket`, `UTracker` |

## 2. Framework AOP Owner

Generic Zero AOP belongs to:

- `zero-epoch/zero-overlay/src/main/java/io/zerows/component/aop/Aspect.java`
- `AspectComponent.java`
- `AspectConfig.java`
- `AspectRobin.java`
- `AspectJObject.java`
- `Before.java`
- `After.java`
- `Around.java`
- `platform/enums/EmAop.java`

Use this layer when the concern is generic cross-cutting execution, not one module's business rule.

## 3. Extension-Crud AOP Owner

Standard CRUD mutation AOP belongs to `zero-extension-crud`.

Verified anchors:

- `uca/AgonicAop.java`
- `uca/AgonicADBCreate.java`
- `uca/AgonicADBUpdate.java`
- `uca/AgonicADBDelete.java`
- `uca/AgonicADBImport.java`
- `uca/AgonicHelper.java`

Use this layer when the change affects reusable create/update/delete/import behavior across modules.

## 4. Exmodule-Level AOP Owners

### MBSE Core

Use `zero-exmodule-mbsecore` when the concern is modeling/plugin before-after routing.

Verified anchors:

- `api/AspectPlugin.java`
- `api/BeforePlugin.java`
- `api/AfterPlugin.java`
- `component/plugin/IoArranger.java`

### RBAC

Use `zero-exmodule-rbac` when the concern is ACL/authorization AOP semantics.

Verified anchors:

- `component/acl/rapier/SyntaxAop.java`
- `component/acl/rapier/QuestAcl.java`
- `boot/ScAcl.java`

### Workflow

Use `zero-exmodule-workflow` when the concern is workflow ticket/tracker before-after logic.

Verified anchors:

- `component/toolkit/UTicket.java`
- `component/toolkit/UTracker.java`

## 5. Decision Rules

### Change generic overlay AOP when:

- the concern is framework-wide
- multiple modules should observe the same before/after behavior
- the semantics are not CRUD-only and not one exmodule only

### Change CRUD AOP when:

- the concern is standard mutation execution
- many CRUD-backed modules are affected
- the change is about reusable create/update/delete/import hooks

### Change exmodule-local AOP when:

- the rule is tied to one domain
- the hook uses module-specific state/metadata
- the business meaning lives in RBAC/workflow/modeling rather than in the generic framework

## 6. What Not To Do

- do not route Zero AOP questions to Spring AspectJ docs
- do not patch one actor when the real seam is in `AgonicAop` or `IoArranger`
- do not force a business-local ACL/workflow hook into generic overlay AOP
