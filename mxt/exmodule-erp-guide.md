# Exmodule ERP Guide

> Load this file when the task is about reusable organization, company, employee, customer-facing enterprise master-data behavior in `zero-exmodule-erp`.

## 1. Scope

`zero-exmodule-erp` owns reusable enterprise master-data behavior.

## 2. Verified Anchors

- API:
  - `CompanyActor`
  - `CompanyAgent`
  - `EmployeeActor`
  - `EmployeeAgent`
- Domain:
  - `CompanyStub`
  - `EmployeeStub`
- Provider:
  - `CompanyService`
  - `EmployeeService`
  - `MDErpActor`
  - `ExtensionERPSource`

## 3. Ownership Rule

Use ERP when the behavior is about reusable org/person/company semantics.

Do not use ERP for:

- generic login/auth mechanics
- generic cache/storage concerns
- app-private employee workflow rules that are not reusable

## 4. Source and Resource Path

Read in this order:

```text
exmodule-erp-guide.md
-> zero-exmodule-erp source/resources
-> r2mo-spec only if shared company/employee/customer model meaning is unresolved
```

High-value proof targets:

- `CompanyActor`
- `EmployeeActor`
- `CompanyStub`
- `EmployeeStub`
- `CompanyService`
- `EmployeeService`
- `MDErpActor`
- module-owned ERP resources

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for ERP runtime/business ownership
- `zero-ecotope` + `r2mo-spec` when shared enterprise master-data contract meaning must be confirmed

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `Company*` or `Employee*` symbols are already known
- the unresolved point is structural spread between API, stub, provider, and shared model consumers
