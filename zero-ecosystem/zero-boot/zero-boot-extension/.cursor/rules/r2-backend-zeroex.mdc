---
description: Definitive guide for Zero Extension Module architecture pattern. Defines DPA four-layer structure, package layout, SPI integration, and strict module responsibilities. This is a reusable template for any extension module project.
globs: zero-exmodule-*/**/*, pom.xml
alwaysApply: true
---

# Zero Extension Module Structure Pattern

This rule defines the **DPA+UI** four-layer extension module architecture pattern for Zero Framework.
**Core Constraint**: All extension modules MUST follow this dependency order: **Domain** ← **Provider** ← **API** (← **UI** optional).

> **Note**: The current project is an implementation following this architecture pattern. Each `zero-exmodule-{name}` module is an independent unit that adheres to this structure.

---

## 1. Architecture Overview

### 1.1 Module Dependency Hierarchy

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer (Optional)                   │
│              .r2mo specs, proto definitions, pages           │
├─────────────────────────────────────────────────────────────┤
│                         API Layer                            │
│              REST Agents, Event Bus Actors, Addr             │
├─────────────────────────────────────────────────────────────┤
│                      Provider Layer                          │
│          Service Impl, Components, SPI Implementations       │
├─────────────────────────────────────────────────────────────┤
│                       Domain Layer                           │
│      Tables, POJOs, Stubs, Exceptions, Constants             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Layer Responsibilities

| Layer | Artifact Pattern | Responsibility | Dependencies |
| :--- | :--- | :--- | :--- |
| **Domain** | `{module}-domain` | Contracts, Data Models, Exceptions, Stubs | Platform/Skeleton ONLY |
| **Provider** | `{module}-provider` | Business Logic, Components, SPI | Domain |
| **API** | `{module}-api` | HTTP Endpoints, Event Consumers | Provider |
| **UI** | `{module}-ui` | Frontend Specs, Design Assets | Independent |

---

## 2. Package Namespace Convention

### 2.1 Namespace Structure

```
io.zerows.extension.module.{name}/     # Module-specific (NOT shared)
├── common/                             # Constants, Enums
├── domain/                             # jOOQ Generated
├── exception/                          # Error definitions
├── servicespec/                        # Stub interfaces
├── boot/                               # Startup (Provider)
├── component/                          # Business components (Provider)
├── serviceimpl/                        # Stub implementations (Provider)
├── spi/                                # SPI implementations (Provider)
└── api/                                # REST/Event handlers (API)

io.zerows.extension/                    # Extension root (shared infrastructure)
├── api/                                # Shared API utilities
├── crud/                               # CRUD utilities
├── skeleton/                           # Skeleton SPI interfaces
└── ...                                 # Other shared packages
```

### 2.2 Shared vs Module-Specific

| Package Scope | Shared? | Description |
| :--- | :--- | :--- |
| `io.zerows.extension.module.{name}` | **NO** | Each module has its own namespace |
| `io.zerows.extension.skeleton.spi` | **YES** | SPI interfaces shared across modules |
| `io.zerows.extension.api` | **YES** | Shared API utilities |
| `io.zerows.extension.crud` | **YES** | Shared CRUD operations |

---

## 3. Directory Structure (Non-Hidden)

```
{module}/
├── pom.xml                                    # Parent POM
├── V{major}.{minor}                           # Version marker file
│
├── {module}-domain/                           # Domain Layer
│   ├── pom.xml
│   ├── database/
│   │   ├── database-reinit.sh
│   │   └── database-reinit.sql
│   ├── init-db.sh
│   └── src/main/
│       ├── java/io/zerows/extension/module/{name}/
│       │   ├── common/                        # Constants, Enums
│       │   ├── domain/                        # jOOQ Generated
│       │   │   ├── Tables.java
│       │   │   ├── Keys.java
│       │   │   ├── Indexes.java
│       │   │   ├── Zdb.java
│       │   │   └── tables/
│       │   │       ├── X*.java                # Entity tables
│       │   │       ├── R*.java                # Relation tables
│       │   │       ├── pojos/                 # POJO classes
│       │   │       ├── records/               # jOOQ Records
│       │   │       ├── daos/                  # DAO classes
│       │   │       ├── interfaces/            # POJO interfaces
│       │   │       └── converters/            # Type converters
│       │   ├── exception/                     # Error codes, Exceptions
│       │   │   ├── ERR.java                   # Error code interface
│       │   │   └── _8xxxxException*.java      # Exception classes
│       │   ├── servicespec/                   # Stub Interfaces
│       │   │   └── *Stub.java
│       │   └── Extension{name}Generation.java # Code generation entry
│       └── resources/
│           ├── plugins/{module}/
│           │   ├── data/                      # Initial data (Excel)
│           │   ├── database/                  # DDL scripts
│           │   ├── flyway/                    # Migration scripts
│           │   ├── logging/                   # Logback config
│           │   ├── model/                     # Entity/Column JSON
│           │   ├── modulat/                   # Module config
│           │   ├── security/                  # RBAC config
│           │   ├── workflow/                  # Workflow definitions
│           │   └── configuration.json
│           ├── MessageFail_*.properties       # Error messages (i18n)
│           ├── MessageInfo_*.properties       # Info messages (i18n)
│           └── flyway.conf
│
├── {module}-provider/                         # Provider Layer
│   ├── pom.xml
│   └── src/main/
│       ├── java/io/zerows/extension/module/{name}/
│       │   ├── boot/                          # Startup, Configuration
│       │   │   ├── *Config.java
│       │   │   ├── MD*Actor.java
│       │   │   ├── MD*Manager.java
│       │   │   └── MID.java
│       │   ├── component/                     # Domain Components
│       │   ├── monitor/                       # Monitoring (optional)
│       │   ├── plugins/                       # Plugin implementations
│       │   ├── serviceimpl/                   # Stub Implementations
│       │   │   └── *Service.java
│       │   ├── spi/                           # SPI Implementations
│       │   │   ├── Ex*.java                   # Extension SPIs
│       │   │   ├── Dictionary*.java           # Dictionary SPIs
│       │   │   └── Registry*.java             # Registry SPIs
│       │   └── Extension{name}Source.java     # Source entry
│       └── resources/
│           ├── META-INF/services/             # SPI Configuration
│           │   └── {interface-FQN}            # ServiceLoader files
│           └── vertx-generate.yml
│
├── {module}-api/                              # API Layer
│   ├── pom.xml
│   └── src/main/java/io/zerows/extension/module/{name}/api/
│       ├── Addr.java                          # Event Bus Addresses
│       ├── *Actor.java                        # Event Consumers
│       └── *Agent.java                        # HTTP Endpoints
│
└── {module}-ui/                               # UI Layer (Optional)
    └── .r2mo/                                 # Frontend specs
        ├── api/                               # OpenAPI specs
        ├── design/                            # Design documents
        ├── domain/                            # Proto definitions
        ├── pages/                             # Page configurations
        └── requirements/                      # Requirements
```

---

## 4. Package Structure Details

### 4.1 Domain Layer (`*-domain`)

**Responsibility**: The "Law" layer. Defines *what* is done, but not *how*.

#### Java Packages

| Package | Purpose |
| :--- | :--- |
| `common/` | Shared constants (`*Constant.java`) and enums (`em/*.java`) |
| `domain/` | jOOQ generated database layer |
| `domain/tables/` | Table classes (`X*` for entities, `R*` for relations) |
| `domain/tables/pojos/` | Plain Old Java Objects for data transfer |
| `domain/tables/records/` | jOOQ Record classes |
| `domain/tables/daos/` | Data Access Objects |
| `domain/tables/interfaces/` | POJO interfaces (`IX*`, `IR*`) |
| `domain/tables/converters/` | Custom type converters |
| `exception/` | Error codes (`ERR.java`) and exception classes |
| `servicespec/` | Service contracts (`*Stub.java`) |

#### Resources Structure

The Domain layer contains a rich set of configuration resources under `src/main/resources/`:

```
src/main/resources/
├── plugins/{module}/                    # Module plugin root
│   ├── configuration.json               # Module configuration
│   ├── {module}.yml                     # Module YAML config
│   │
│   ├── data/                            # Initial seed data
│   │   ├── {TABLE_NAME}/                # Per-table data directory
│   │   │   ├── *.xlsx                   # Excel data files
│   │   │   └── {subdir}/                # Nested data with JSON metadata
│   │   │       └── metadata.json
│   │   └── ...
│   │
│   ├── database/                        # Database schema definitions
│   │   ├── {DB_TYPE}/                   # Database type (e.g., MYSQL)
│   │   │   ├── X*.sql                   # Entity table DDL
│   │   │   ├── R*.sql                   # Relation table DDL
│   │   │   ├── O*.sql                   # Other tables (optional)
│   │   │   └── W*.sql                   # Workflow tables (optional)
│   │   ├── {DB_TYPE}.properties         # DB-specific properties
│   │   └── {DB_TYPE}.yml                # Liquibase changelog YAML
│   │
│   ├── flyway/                          # Flyway migration scripts
│   │   └── {DB_TYPE}/
│   │       └── V{major}.{minor}.nnn__*.sql  # Versioned migrations
│   │
│   ├── logging/                         # Logging configuration
│   │   └── logback-segment.xml          # Logback segment config
│   │
│   ├── model/                           # Entity model definitions
│   │   ├── {entity}/                    # Per-entity directory
│   │   │   ├── column.json              # Column definitions
│   │   │   └── entity.json              # Entity metadata
│   │   ├── model.hybrid/                # Hybrid model definitions (optional)
│   │   │   └── *.json                   # Hybrid entity configs
│   │   └── connect.yml                  # DAO connection mappings
│   │
│   ├── modulat/                         # Module license & UI config
│   │   ├── {BAG_ID}/                    # Bag (module group) config
│   │   │   └── uiConfig.json            # UI configuration
│   │   ├── {MODULE_ID}/                 # Per-module config
│   │   │   ├── licIdentifier.json       # License identifier
│   │   │   ├── licMenu.json             # License menu config
│   │   │   └── uiConfig.json            # UI configuration
│   │   └── *.xlsx                       # Module definition Excel
│   │
│   ├── security/                        # RBAC security definitions
│   │   ├── RBAC_ADMIN/                  # Admin-level rules
│   │   │   ├── {rule-name}/             # Rule directory
│   │   │   │   └── {sub-rule}/          # Sub-rule with JSON configs
│   │   │   │       ├── dmComponent.json # Data model component
│   │   │   │       ├── dmConfig.json    # Data model config
│   │   │   │       ├── uiComponent.json # UI component config
│   │   │   │       ├── uiCondition.json # UI condition config
│   │   │   │       ├── uiConfig.json    # UI config
│   │   │   │       └── uiSurface.json   # UI surface config
│   │   │   └── *.xlsx                   # Rule definition Excel
│   │   ├── RBAC_RESOURCE/               # Resource-level permissions
│   │   │   ├── {entity}/                # Per-entity resource
│   │   │   │   └── res.{name}.{action}/ # Resource action config
│   │   │   │       └── seekSyntax.json  # Query syntax config
│   │   │   └── *.xlsx                   # Resource definition Excel
│   │   └── RBAC_ROLE/                   # Role-level permissions
│   │       └── {ROLE_ID}/               # Per-role config
│   │           └── *.xlsx               # Role permission Excel
│   │
│   ├── web/                             # Web UI configurations (optional)
│   │   └── {module}/                    # Module-specific UI
│   │       └── {feature}/               # Feature-specific UI
│   │           └── UI.json              # UI component definition
│   │
│   ├── workflow/                        # Workflow definitions (optional)
│   │   ├── running/                     # Active workflow definitions
│   │   │   └── {process-id}/            # Process definition
│   │   │       ├── workflow.bpmn        # BPMN process definition
│   │   │       ├── *.form               # Form definitions
│   │   │       └── *.json               # Form JSON configs
│   │   ├── LINKAGE/                     # Workflow linkage configs
│   │   │   └── {TYPE}.json              # Type-specific linkage
│   │   └── *.xlsx                       # Workflow definition Excel
│   │
│   └── __history/                       # Historical configs (optional)
│       └── {config-name}/               # Config history
│           └── *.json                   # Historical JSON configs
│
├── keys/                                # Security keys (optional)
│   └── keystore.jceks                   # Java keystore
│
├── MessageFail_{locale}.properties      # Error messages (i18n)
├── MessageInfo_{locale}.properties      # Info messages (i18n)
└── flyway.conf                          # Flyway configuration
```

#### Resource Configuration Files

| File | Purpose |
| :--- | :--- |
| `configuration.json` | Module-level configuration (supportSource, fileStorage, etc.) |
| `{module}.yml` | Module YAML configuration |
| `connect.yml` | DAO-to-table connection mappings with unique constraints |
| `flyway.conf` | Flyway database connection configuration |
| `*.properties` | i18n message bundles (en_US, zh_CN, ja_JP) |

#### Database Resources

| Directory/File | Purpose |
| :--- | :--- |
| `database/{DB_TYPE}/*.sql` | Static DDL scripts for table creation |
| `database/{DB_TYPE}.yml` | Liquibase changelog (include references to SQL files) |
| `database/{DB_TYPE}.properties` | Database-specific properties |
| `flyway/{DB_TYPE}/V*.sql` | Versioned Flyway migration scripts |

#### Model Resources

| Directory/File | Purpose |
| :--- | :--- |
| `model/{entity}/column.json` | Column-level metadata for entity |
| `model/{entity}/entity.json` | Entity-level metadata (name, DAO class, unique fields) |
| `model/connect.yml` | DAO connection configuration with unique constraint definitions |
| `model.hybrid/*.json` | Hybrid model definitions for complex entities |

#### Security Resources

| Directory | Purpose |
| :--- | :--- |
| `RBAC_ADMIN/` | Admin-level security rules and configurations |
| `RBAC_RESOURCE/` | Resource-level permission definitions |
| `RBAC_ROLE/` | Role-level permission assignments |
| `RBAC_USER/` | User-specific configurations (optional) |

#### Workflow Resources

| Directory/File | Purpose |
| :--- | :--- |
| `workflow/running/` | Active workflow process definitions (BPMN) |
| `workflow/LINKAGE/` | Workflow linkage configurations |
| `workflow.doc/` | Document workflow configurations (optional) |

### 4.2 Provider Layer (`*-provider`)

**Responsibility**: The "Worker" layer. Implements business logic and SPI extensions.

| Package | Purpose |
| :--- | :--- |
| `boot/` | Startup configuration, module initialization |
| `component/` | Domain-specific business logic helpers |
| `monitor/` | Monitoring and metrics implementations |
| `plugins/` | Plugin implementations (`*Checker.java`, `*Plugin.java`) |
| `serviceimpl/` | Stub implementations (`*Service.java`) |
| `spi/` | SPI implementations for framework integration |

### 4.3 API Layer (`*-api`)

**Responsibility**: The "Messenger" layer. Handles HTTP and Event Bus routing.

| Class Type | Purpose |
| :--- | :--- |
| `Addr.java` | Event Bus address constant definitions |
| `*Agent.java` | HTTP Endpoint classes (annotated with `@EndPoint`, `@Path`) |
| `*Actor.java` | Event Consumer classes (annotated with `@Queue`) |

### 4.4 UI Layer (`*-ui`)

**Responsibility**: Frontend specifications and design assets.

| Directory | Purpose |
| :--- | :--- |
| `.r2mo/api/` | OpenAPI specifications (schemas, operations) |
| `.r2mo/design/` | Design documents (spec.md, spec-page.md) |
| `.r2mo/domain/` | Protobuf domain definitions (`*.proto`) |
| `.r2mo/pages/` | Page configurations |
| `.r2mo/requirements/` | Requirements documents |

---

## 5. SPI (Service Provider Interface) Rules

### 5.1 SPI Configuration Location

All SPI configurations are located at:
```
{module}-provider/src/main/resources/META-INF/services/
```

### 5.2 SPI Interface Categories

| Category | Interface Pattern | Purpose |
| :--- | :--- | :--- |
| **Extension** | `io.zerows.extension.skeleton.spi.Ex*` | Module-specific extensions |
| **Security** | `io.zerows.extension.skeleton.spi.Sc*` | Security-related services |
| **UI** | `io.zerows.extension.skeleton.spi.Ui*` | UI-related services |
| **Dictionary** | `io.zerows.epoch.spi.Dictionary` | Dictionary service |
| **Registry** | `io.zerows.specification.configuration.HRegistry` | Registry service |
| **Modeler** | `io.zerows.spi.modeler.*` | Data modeling services |
| **Plugins** | `io.zerows.plugins.*` | Plugin services |
| **jOOQ** | `io.r2mo.vertx.jooq.generate.configuration.TypeOfJooq` | jOOQ configuration |

### 5.3 SPI File Format

Each SPI file follows the Java ServiceLoader format:
```
# File: META-INF/services/{interface.FQN}
{implementation.FQN}
```

Example:
```
# File: META-INF/services/io.zerows.epoch.spi.Dictionary
io.zerows.extension.module.{name}.spi.Dictionary{name}
```

### 5.4 SPI Implementation Rules

1. **Location**: All SPI implementations MUST be in the `spi/` package of the provider module.
2. **Naming**: Use `Ex` prefix for extensions or descriptive names matching the module.
3. **Single Implementation**: Each module provides at most one implementation per SPI interface.
4. **Discovery**: SPI implementations are discovered automatically by Java ServiceLoader.

---

## 6. Execution Flow (Golden Link)

Any feature implementation must strictly follow this execution chain:

```
[Class] Agent → [Const] Addr → [Class] Actor → [Interface] Stub → [Class] Service → [Class] DBE
```

### Development Checklist (Inside-Out)

1. **Domain**: Define `Table` (jOOQ) → Define `Stub` (Interface) → Define `Exception`
2. **Provider**: Implement `Service` (Class) → Implement `SPI` (if needed)
3. **API**: Define `Addr` → Create `Actor` (Class) → Create `Agent` (Class)

---

## 7. Coding Conventions

### 7.1 Error Handling

- **Location**: `domain/exception/ERR.java`
- **Format**: Negative integers with module prefix (e.g., `-80xxx`)
- **Naming**: `_80xxxException{HttpStatus}{Description}.java`

### 7.2 Asynchronous Pattern

- All `Stub`, `Service`, and `Actor` methods **MUST** return `Future<T>`
- Avoid blocking calls in the main flow

### 7.3 Naming Conventions

| Element | Convention | Example |
| :--- | :--- | :--- |
| Table classes | `X*` (entity), `R*` (relation) | `XApp.java`, `RTagEntity.java` |
| Stub interfaces | `*Stub.java` | `AppStub.java` |
| Service impl | `*Service.java` | `AppService.java` |
| HTTP Endpoints | `*Agent.java` | `AppAgent.java` |
| Event Consumers | `*Actor.java` | `AppActor.java` |
| Event Addresses | `Addr.java` | `Addr.App.FETCH` |
| Exceptions | `_8xxxException*.java` | `_80301Exception500ApplicationInit.java` |
| SPI impl | `Ex*` or `{Name}*` | `ExActivity.java`, `DictionaryAmbient.java` |

### 7.4 Transaction & Validation

- **Transaction Boundary**: Service layer ONLY
- **Format Validation**: JSR-303 annotations on POJOs/Agent parameters
- **Business Validation**: Inside Service before calling DBE

---

## 8. Version Marker Files

Each module contains a version marker file: `V{major}.{minor}`

- **Major version**: Framework version (e.g., `207`)
- **Minor version**: Module-specific version (e.g., `001`)

This version is used in Flyway migration scripts: `V{major}.{minor}.nnn__*.sql`

---

## 9. Integration with .r2mo Specifications

When implementing backend features, reference `.r2mo` specifications:

- **Data Models**: Check `.r2mo/domain/*.proto` for entity definitions
- **API Interfaces**: Check `.r2mo/api/operations/` for endpoint definitions
- **Requirements**: Review `.r2mo/requirements/` for business logic

**Rule**: The `.r2mo` specifications are the source of truth. Backend code must conform to these specs.

---

## 10. Module Independence Principle

Each `zero-exmodule-{name}` module is an **independent unit**:

1. **Self-contained**: Each module has its own domain, provider, and API layers.
2. **No cross-dependencies**: Modules do not depend on each other.
3. **Shared infrastructure only**: Modules share `io.zerows.extension` root packages (skeleton, api, crud) but not module-specific code.
4. **SPI-based integration**: Modules integrate with the framework via SPI, not direct dependencies.
