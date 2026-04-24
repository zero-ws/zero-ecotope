# Report Center Guide

> Load this file when the task is about a configurable report center rather than generic reporting module ownership.

## 1. Scope

This file owns the composite scenario of a configurable report center.

## 2. Owning Modules

- `zero-exmodule-report`

## 3. Key Anchors

- `ReportActor`
- `ReportStub`
- `ReportInstanceStub`
- `ReportService`
- `ReportInstanceService`

## 4. Source and Resource Path

Read in this order:

```text
report-center-guide.md
-> exmodule-report-guide.md
-> exact report source/resources
```

High-value proof targets:

- `ReportActor`
- `ReportInstanceStub`
- `ReportService`
- report metadata and instance resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for report-center behavior
- `zero-ecotope` + `r2mo-rapid` only when Excel/export transport ownership must also be confirmed

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one report actor/service symbol is already known
- the unresolved point is report build/export lifecycle spread

## 7. AI Agent Rules

- Use this file when the question is specifically “configurable report center”.
- Use `exmodule-report-guide.md` when the question is simply module ownership for report logic.
