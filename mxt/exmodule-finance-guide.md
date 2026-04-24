# Exmodule Finance Guide

> Load this file when the task is about reusable finance processing, settlement, debt, bill, book, or reportable transactional business semantics in `zero-exmodule-finance`.

## 1. Scope

`zero-exmodule-finance` owns reusable finance-domain processing.

## 2. Verified Anchors

- API:
  - `BatchActor`
  - `EndBookActor`
  - `EndPreAuthActor`
  - `EndSettleActor`
- Domain/servicespec:
  - `BookStub`
  - `EndAdjustStub`
  - `EndDebtStub`
  - `EndSettleRStub`
  - `EndSettleWStub`
  - `EndTransStub`
  - `FetchStub`
  - `InBillStub`
- Provider:
  - `BookService`
  - `EndAdjustService`
  - `EndDebtService`
  - `EndSettleRService`
  - `EndSettleWService`
  - `EndTransService`
  - `FetchService`
  - `InBillService`
  - `MDFinanceActor`
  - `ExtensionFinanceSource`

## 3. Ownership Rule

Use Finance when the rule is finance-domain reusable behavior.

Do not place here:

- generic report engine behavior
- generic workflow engine behavior
- low-level messaging/cache/storage mechanics

## 4. Source and Resource Path

Read in this order:

```text
exmodule-finance-guide.md
-> zero-exmodule-finance source/resources
-> r2mo-spec only if shared finance model meaning is unresolved
-> report/workflow docs only if the actual owner is still unclear
```

High-value proof targets:

- `BatchActor`
- `EndBookActor`
- `EndSettleActor`
- `BookStub`
- `EndDebtStub`
- `BookService`
- `EndSettleRService`
- `MDFinanceActor`
- finance model/resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for finance-domain runtime ownership
- `zero-ecotope` + `r2mo-spec` when shared bill/settlement/transaction model meaning must be confirmed

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one finance actor/stub/service is already known
- the unresolved point is structural spread between transaction processing, settlement, debt, and shared model consumers
