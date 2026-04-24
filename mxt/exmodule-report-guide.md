# Exmodule Report Guide

> Load this file when the task is about configurable report-center behavior, report metadata, report generation, or report instance lifecycle in `zero-exmodule-report`.

## 1. Scope

`zero-exmodule-report` owns reusable report-center business semantics.

## 2. Verified Anchors

- API:
  - `ReportAgent`
  - `ReportActor`
- Domain/servicespec:
  - `ReportStub`
  - `ReportInstanceStub`
- Provider:
  - `ReportService`
  - `ReportInstanceService`
  - `ExtensionReportSource`
  - `MDReportManager`
  - `ExDefaultReport`

## 3. Boundary

Use Report when the issue is about:

- report metadata
- report instance paging/building/export
- report generation steps and report feature assembly

Do not use Report for:

- generic Excel transport capability
- generic monitor/export infrastructure

## 4. Source and Resource Path

Read in this order:

```text
exmodule-report-guide.md
-> report-center-guide.md when configurable report-center semantics are involved
-> zero-exmodule-report source/resources
-> excel-import-export-guide.md only if export transport ownership is unresolved
```

High-value proof targets:

- `ReportActor`
- `ReportStub`
- `ReportInstanceStub`
- `ReportService`
- `ReportInstanceService`
- `MDReportManager`
- report metadata and instance resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for report business ownership
- `zero-ecotope` + `r2mo-rapid` when the unresolved point crosses into export transport or shared document delivery

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one report actor/service symbol is already known
- the unresolved point is structural spread between metadata, instance lifecycle, and export assembly
