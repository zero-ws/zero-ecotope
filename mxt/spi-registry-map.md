# SPI Registry Map

> Load this file when the task is about available Zero extension contracts, SPI group ownership, or the canonical registry map centered on `ExBoot.SPI_SET`.

## 1. Scope

This file owns:

- the SPI group map
- the canonical registry source
- SPI family classification

It does not own:

- implementation rules
- CRUD-engine-first decisions
- plugin startup flows

## 2. Canonical Registry Source

Source of truth:

```text
io.zerows.extension.skeleton.boot.ExBoot
```

Primary registry anchor:

```text
ExBoot.SPI_SET
```

Rule:

```text
If the question is “what SPI families exist?”, start from `ExBoot.SPI_SET`.
```

## 3. Framework Integration SPIs

| SPI | Responsibility |
|---|---|
| `ConfigMod` | module-level configuration extension |
| `HQBE` | high-order query builder extension |
| `Dictionary` | dictionary data extension |

## 4. Business Extension SPIs

| SPI | Responsibility |
|---|---|
| `ExActivity` | activity tracking and audit extension |
| `ExApp` | application-level extension logic |
| `ExArbor` | tree-structure extension |
| `ExAtom` | atomic metadata and model extension |
| `ExAttachment` | attachment management extension |
| `ExAccountProvision` | account provisioning extension |
| `ExIo` | import/export extension |
| `ExLinkage` | linkage and relation extension |
| `ExModulat` | modular configuration extension |
| `ExOwner` | owner and tenant assignment logic |
| `ExSetting` | system/settings extension |
| `ExTenantProvision` | tenant provisioning during registration |
| `ExTransit` | state transition extension |
| `ExUser` | user-model extension |

## 5. Security SPIs

| SPI | Responsibility |
|---|---|
| `ScCredential` | credential handling and validation |
| `ScOrbit` | permission/resource routing model |
| `ScPermit` | permission-rule evaluation |
| `ScRoutine` | standard security routine extension |
| `ScSeeker` | permission/resource discovery |

## 6. UI SPIs

| SPI | Responsibility |
|---|---|
| `UiApeak` | top-bar UI extension |
| `UiApeakMy` | personal top-bar UI extension |
| `UiForm` | dynamic form extension |
| `UiValve` | UI visibility and valve control |

## 7. Agent Rules

- Use this file to classify the SPI family first.
- Do not use this file as the implementation guide.
- If the requirement is actually standard metadata-driven CRUD, switch to `crud-engine-guide.md`.
