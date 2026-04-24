# Exmodule Workflow Guide

> Load this file when the task is about reusable workflow engine behavior, queue/todo handling, workflow deployment, or workflow-side before/after business hooks in `zero-exmodule-workflow`.

## 1. Scope

`zero-exmodule-workflow` owns reusable workflow-domain behavior.

It is the right entry for:

- workflow start/advance/cancel/report semantics,
- queue and todo lifecycle,
- workflow boot resources and deployment entry,
- workflow-owned before/after business hooks,
- workflow state tracking and ticket utilities.

## 2. Verified Anchors

- API:
  - `QueueActor`
  - `QueueAgent`
  - `ReportActor`
  - `ReportAgent`
  - `RunActor`
  - `RunAgent`
  - `TodoActor`
- Domain/servicespec:
  - `ReportStub`
  - workflow domain/todo/history resources
- Provider:
  - `ExtensionWorkflowSource`
  - `PrimedWorkflow`
  - `MDWorkflowActor`
  - `Wf`
  - `UTicket`
  - `UTracker`

Additional repository anchors worth checking directly:

- `zero-exmodule-workflow-provider/src/main/resources/vertx-generate.yml`
- `zero-exmodule-workflow-provider/src/main/resources/META-INF/services/*`
- `zero-exmodule-workflow-domain/database/*`

## 3. Ownership Rule

Use Workflow when the issue is about:

- workflow start/complete/cancel/batch semantics
- todo/task/history/report behavior
- workflow deployment boot
- ticket/tracker before/after business hooks

## 4. AOP Boundary

Workflow contains module-local before/after logic through toolkit components such as:

- `UTicket`
- `UTracker`

That is workflow-owned business hook logic, not generic overlay AOP.

## 5. Boundary

Do not use Workflow for:

- generic transport/security/storage capability
- generic CRUD engine ownership
- raw Camunda plugin infrastructure without workflow business meaning

Also do not use it for:

- generic event bus/runtime boot issues with no workflow semantics,
- version/BOM exposure questions,
- non-workflow AOP ordering.

## 6. Source and Resource Path

Read in this order:

```text
exmodule-workflow-guide.md
-> zero-exmodule-workflow-api for queue/run/report contracts
-> zero-exmodule-workflow-domain for workflow-owned resources and schema
-> zero-exmodule-workflow-provider for boot, tracker, and runtime orchestration
-> neighboring transport/security docs only if ownership crosses module boundaries
```

High-value proof targets:

- `QueueActor`
- `ReportAgent`
- `RunActor`
- `TodoActor`
- `ExtensionWorkflowSource`
- `PrimedWorkflow`
- `MDWorkflowActor`
- `UTicket`
- `UTracker`
- module-owned workflow database/resources

## 7. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for workflow business semantics
- `zero-ecotope` + `r2mo-rapid` when the unresolved point falls into shared runtime, DBE, or transport primitives
- `zero-ecotope` + `r2mo-spec` only when workflow payload meaning becomes a shared contract problem

## 8. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one workflow symbol is already known,
- the unresolved point is structural spread between API entry, provider boot, toolkit hooks, and workflow-owned resources,
- source/resource inspection still remains the final proof step.
