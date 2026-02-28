---
description: Definitive guide for DPA (Domain-Provider-API) Backend Architecture. Defines module structure, dependencies, and strict component responsibilities.
globs: *-domain/**/*, *-provider/**/*, *-api/**/*, pom.xml, src/main/java/**/*, src/main/resources/**/*
alwaysApply: true
---

# DPA Backend Architecture — Project Structure

This rule enforces the **DPA** (Domain, Provider, API) three-layer backend structure.
**Core Constraint**: All backend development MUST follow this dependency order: **Domain** ← **Provider** ← **API**.

## 1. Root Level & Modules

The project is a multi-module Maven project defined by a root `pom.xml`.

| Module | Role | Dependencies |
| :--- | :--- | :--- |
| **`<project>-domain`** | **The Core**. Contracts, Data Models, Exceptions. | Platform/Skeleton ONLY. **NO** dependency on Provider/API. |
| **`<project>-provider`** | **The Logic**. Service Implementation, Components. | Depends on **Domain**. |
| **`<project>-api`** | **The Interface**. REST Agents & Event Bus Actors. | Depends on **Provider**. |

---

## 2. Component Naming Conventions (CRITICAL)

The terms used in the execution flow refer to **Class Types/Suffixes**, NOT method names.

| Term | Component Type | Naming Convention | Example |
| :--- | :--- | :--- | :--- |
| **Agent** | **Class** (REST Endpoint) | `*Agent.java` | `UserAgent.java` |
| **Addr** | **Constant** (Event Address) | `Addr.java` | `Addr.User.CREATE` |
| **Actor** | **Class** (Event Consumer) | `*Actor.java` | `UserActor.java` |
| **Stub** | **Interface** (Contract) | `*Stub.java` | `UserStub.java` |
| **Service** | **Class** (Implementation) | `*Service.java` | `UserService.java` |
| **DBE** | **Class/Tool** (Database Engine) | `Ux.Jooq` / `*Dao` | `UserDao.java` |

---

## 3. The "Golden Link" Execution Flow

Any feature implementation must strictly follow this execution chain involving the **Component Classes** defined above.

**Flow Chain:**
`[Class] Agent` → `[Const] Addr` → `[Class] Actor` → `[Interface] Stub` → `[Class] Service` → `[Class] DBE`

### Development Checklist (Inside-Out)
1.  **Domain**: Define `Table` (jOOQ) -> Define `Stub` (Interface) -> Define `Exception`.
2.  **Provider**: Implement `Service` (Class) which calls `DBE`.
3.  **API**: Define `Addr` -> Create `Actor` (Class) -> Create `Agent` (Class).

---

## 4. Module Details & Package Layout

### 🟢 Module: Domain (`<project>-domain`)
**Responsibility**: The "Law" layer. Defines *what* is done, but not *how*.

* **Java Layout**:
    * `domain/`: jOOQ generated classes (Tables, POJOs, Records, DAOs).
    * `servicespec/`: **Stubs** (Service Interfaces). Method signature **MUST** return `Future<T>`.
    * `exception/`: Error code enums and specific `_40x`/`_50x` exception classes.
    * `common/`: Shared constants/enums.

* **Resources (`src/main/resources/plugins/`)**:
    * **Single Source of Configuration**: All plugin configs (Flyway, RBAC, Excel models) reside here.
    * `database/`: SQL scripts and DDL.
    * `flyway/`: Versioned migrations (`V*__*.sql`).

### 🟡 Module: Provider (`<project>-provider`)
**Responsibility**: The "Worker" layer. Implements business logic.

* **Java Layout**:
    * `serviceimpl/`: Implementation of Domain Stubs.
        * **Rule**: Must implement `*Stub` interface.
        * **Rule**: Only layer allowed to call jOOQ/DBE.
    * `component/`: Domain-specific logic helpers.
    * `spi/`: Service Provider Interface implementations (e.g., Dictionary, Registry).
    * `boot/`: Startup and configuration loading.

### 🔴 Module: API (`<project>-api`)
**Responsibility**: The "Messenger" layer. Handles HTTP and Event Bus routing.

* **Java Layout**:
    * `Addr.java`: **Address Constants**. The only place to define Event Bus addresses.
    * `*Agent.java`: **HTTP Endpoint Class**.
        * Annotated with `@EndPoint`, `@Path`.
        * **Strict Rule**: NO logic. Returns `null` or interface definition only.
    * `*Actor.java`: **Event Consumer Class**.
        * Annotated with `@Queue`.
        * **Strict Rule**: Injects `*Stub`, calls Stub, returns `Future`. NO DB access.

---

## 5. Coding Conventions

### Error Handling
* **Location**: All error codes defined in `domain/exception/ERR.java`.
* **Usage**: Throw specific exceptions (e.g., `_404DataNotFoundException`) in the **Provider** layer. API layer allows them to bubble up.

### Asynchronous Pattern
* All `Stub`, `Service`, and `Actor` methods **MUST** return `Future<T>` (Vert.x / Zero Framework standard).
* Avoid blocking calls (`Thread.sleep`, blocking I/O) in the main flow.

### Out of Scope
* UI assets (`*-ui`) and Requirements specs (`.r2mo`) are documented in separate rules but referenced for context.

### Integration with .r2mo Specifications
When implementing backend features, reference `.r2mo` specifications:

- **Data Models** (`openapi.schemas`): Check `.r2mo/domain/*.proto` to ensure Table/POJO definitions match the spec.
- **API Interfaces** (`openapi.operations`): Check `.r2mo/api/operations/{uri}/*.md` for endpoint definitions.
- **Requirements** (`requirement.*`): Review `.r2mo/requirements/` for business logic.

**Rule**: The `.r2mo` specifications are the source of truth. Backend code must conform to these specs.

### Transaction & Validation (Strict)
- Transaction Boundary: The Service layer is the ONLY place to manage transactions. Never handle transactions in API (Actor/Agent).
- Validation:
  - Format Validation: JSR-303 annotations on POJOs or Agent parameters.
  - Business Validation: Must happen inside the Service before calling DBE.