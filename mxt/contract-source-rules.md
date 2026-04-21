# Contract Source Rules

> Load this file when the task is about `.r2mo` contract sources, model authority, or generation inputs across backend and frontend.

## 1. Scope

This file owns:

- `.r2mo` contract authority
- model source ordering
- generation input precedence
- contract-to-runtime mapping rules

It does not own:

- backend DPA mechanics
- frontend client implementation details
- environment variable contracts

## 2. Core Rule

`.r2mo` is the contract source.

Runtime code is an implementation of the contract. It must not invent interfaces or models that the contract does not define.

## 3. Stable Contract Sources

Treat these as the primary sources:

1. `.r2mo/api/metadata-with-server.yaml`
2. `.r2mo/api/components/schemas/*`
3. `.r2mo/api/operations/{uri}/*`
4. `.r2mo/domain/*.proto`

Interpretation:

- `operations/*` define interface behavior
- `components/schemas/*` and OpenAPI metadata define API DTO contracts
- `domain/*.proto` define domain and structural contracts

Java domain classes, jOOQ artifacts, DAOs, and POJOs are runtime mappings, not the primary contract source.

## 4. Precedence Rules

Preferred generation order:

```text
OpenAPI operation + schema
  -> frontend DTO and call signature
  -> backend Agent / Actor / Stub input-output shape

Proto / domain model
  -> domain object, table, DAO, and migration shape
```

## 5. Consistency Rules

- Do not add interfaces or models that are absent from `.r2mo`.
- Keep frontend field names aligned with backend JSON contracts.
- Keep backend stub, service, and agent signatures aligned with `.r2mo`.
- Keep database tables, DAOs, and migrations subordinate to the contract model.

## 6. Agent Rules

- If code and contract disagree, trust the contract first and inspect generation flow second.
- Do not treat generated Java artifacts as the highest authority.
- When a model looks duplicated, classify whether it is API contract, domain contract, or runtime mapping.
