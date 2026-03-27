# Abstraction Rules

## 1. Three-Letter Objects

Three-letter objects are high-order framework abstractions used internally by Zero. Business code should not instantiate them directly.

| Object | Full Name | Responsibility |
|---|---|---|
| `HED` | HighOrder Encrypt Decrypt | Encryption/decryption utility abstraction |
| `HPI` | HighOrder Service Provider Interface | High-order SPI entry point used by the framework |
| `HOI` | HighOrder Owner ID | Tenant/owner identity handling |
| `DBE` | Database Engine | Abstracted database engine and unified data-access style |
| `HFS` | HighOrder File System | Local file system abstraction |
| `RFS` | Remote File System | Remote file system abstraction |

Rule:
- Framework authors may work directly with these abstractions.
- Business code should prefer `Fx`, `Ux`, and only occasionally `Ut`.

## 2. Fx / Ux / Ut

| Object | Full Name | Usage |
|---|---|---|
| `Fx` | Function Extension | Business-facing functional extension entry |
| `Ux` | Utility Extension | Business-facing utility extension entry |
| `Ut` | Utility | Low-level utility helper, use sparingly |

Rule:
- Use `Fx` and `Ux` in most business-facing code.
- Use `Ut` only for narrow utility scenarios.
- Do not bypass these facades to create internal high-order objects directly.

## 3. Code Markers

The repository uses code markers to make source reading faster.

| Marker | Meaning |
|---|---|
| `#BOOT-NNN` | Container startup lifecycle step |
| `#SPI` | SPI-related code |
| `#PIN` | Core code connected to another plugin |
| `#REQ-NNN` | Request-processing sequence step |

These markers matter because they define the shortest path for both humans and AI agents to trace runtime flow.

## 4. Sequence Diagram Markers

| Marker | Meaning |
|---|---|
| Agent / Worker method | Basic execution step |
| EventBus address | EventBus endpoint |
| Business method | Domain/business logic step |
| DBE method | Database engine step |
| Component / plugin / SPI logic | Framework extension step |

## 5. Property Markers

Used in API and schema descriptions.

| Marker | Meaning |
|---|---|
| Component property | Usually maps to a Java class or component |
| Config property | Usually maps to `JsonObject` / `JsonArray` |
| Business property | End-user visible business field |
| Identifier property | Business identity field, excluding scope keys |
| Scope identifier | Range/scope field such as tenant/app IDs |
| System property | System-managed deployment/runtime field |
| Audit property | Typically `?At` / `?By` fields |
| Association property | Relation field |
| Boolean suffix | JSON boolean field |
| Numeric suffix | JSON numeric field |

## 6. Abstraction Layer Rules

1. Each layer should expose only a higher-level abstraction upward.
2. `zero-epoch` contains framework-level abstractions, not business semantics.
3. `zero-plugins-equip` should provide reusable capability, not cross-module business logic.
4. `zero-extension-skeleton` defines extension contracts and should remain stable.
5. `zero-exmodule-*` implements those contracts and adds domain logic without redefining the skeleton contract.
