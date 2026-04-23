# DBE Query Rules

> Load this file when the task is about DBE, `QQuery`, `QTree`, query placement, or Zero backend search semantics.

## 1. Scope

This file owns:

- DBE query envelope rules
- `QQuery` and `QTree`
- query composition placement
- standard DBE operator semantics

It does not own:

- DPA module structure
- RBAC rules
- model metadata ownership
- CRUD engine routing

## 2. Data Access Model

Zero business modules use two common data-access styles:

1. DBE query object style via `QQuery`
2. direct async DAO / DB access via `DB.on(SomeDao.class)` or generated DAOs

Rule:

```text
Use DBE semantics for normalized query behavior even when implementation lands on direct DAO paths.
```

## 3. `QQuery` Structure

Use the normalized request envelope:

```json
{
  "criteria": {},
  "pager": { "page": 1, "size": 20 },
  "sorter": ["createdAt,DESC"],
  "projection": ["id", "name"]
}
```

Responsibilities:

- `criteria` = where clause tree
- `pager` = pagination
- `sorter` = order by
- `projection` = selected fields

## 4. `QTree` Rules

Direct node format:

```text
"field,op": value
```

Important operators:

- `=` equal
- `<>` not equal
- `>` `>=` `<` `<=`
- `i` / `!i` in / not in
- `n` / `!n` is null / is not null
- `s` starts with
- `e` ends with
- `c` contains

Logic rules:

- AND is default
- OR uses `"": false`
- nested groups use `$group`-style keys

## 5. Query Placement

- Build complex query composition in service layer.
- Keep `pager`, `sorter`, and `projection` outside `criteria`.
- Use `projection` for large or sensitive fields.
- Make indexed fields back common filters and sort orders.

## 6. Agent Rules

- Do not invent ad hoc query JSON when `QQuery` already covers the need.
- Do not place DBE composition in agents or actors.
- Do not mix pagination and sorting into `criteria`.
- If search semantics are inconsistent across layers, normalize on DBE first.
