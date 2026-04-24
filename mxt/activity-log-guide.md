# Activity Log Guide

> Load this file when the task is about reusable activity logging, ambient-side audit trails, or the activity-rule engine boundary.

## 1. Scope

This file owns:

- activity logging as a reusable business concern
- ambient-side activity service ownership
- the link from activity behavior to shared activity SPI

## 2. Owning Modules

- `zero-exmodule-ambient`
- shared `ExActivity` SPI

## 3. Key Anchors

- `ExActivity`
- `ActivityStub`
- `ActivityService`
- activity-rule resources and expression-backed metadata handled in ambient

## 4. Source and Resource Path

Read in this order:

```text
activity-log-guide.md
-> exmodule-ambient-guide.md
-> ambient-activity-expression-rules.md
-> zero-exmodule-ambient source/resources
```

High-value proof targets:

- ambient-side `Activity*` classes
- `ExActivity` implementations
- activity-rule and expression-backed resource files

## 5. Boundary

Use this guide when the task is about:

- reusable activity history
- tracked change logs
- ambient-owned activity rules
- expression-backed activity rendering

Do not use it for:

- generic monitor-center collection
- protocol-level audit transport
- Spring-side AOP audit logic

## 6. Pairwise Handling

Preferred pairs:

- `zero-ecotope` + `r2mo-spec` when shared marker/model meaning affects activity fields
- `zero-ecotope` alone when the question is purely ambient runtime behavior

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `ActivityStub`, `ActivityService`, or `ExActivity` is already known
- the unresolved point is caller spread or rule ownership

## 8. AI Agent Rules

- Keep reusable activity semantics in ambient and its SPI seam.
- Do not move activity policy into generic monitor plugins.
