# AI Anti-Patterns

> Load this file when an AI agent is about to make changes in a Zero framework project and needs to avoid common mistakes.
> Each anti-pattern includes: symptom, root cause, correct path, and owner document.

## 1. Spring Assumptions in Vert.x Projects

**Symptom**: Agent writes `@Service`, `@Autowired`, `@RestController`, or Spring `application.yml` patterns in a `zero-0216` project.

**Root cause**: Default LLM training bias toward Spring Boot conventions. Zero uses Vert.x execution chain: `Agent → Addr → Actor → Stub → Service`. Dependency injection uses `@Injector` or `HPI`/`HED`, not Spring DI.

**Correct path**:
- Verify the parent POM first: `zero-0216` → Vert.x; `r2mo-0216` → Spring.
- In Zero projects, use `@Address`, `@Agent`, `@Actor` for routing.
- Use `HPI.findMany()` or `HED` for service discovery, not `@Autowired`.

**Owner**: `backend-dpa-guide.md`, `agent-quick-start.md`

## 2. App-Layer Abstractions That Belong in Framework

**Symptom**: Agent creates a new utility class, base service, or SPI contract inside a downstream project that generalizes framework behavior.

**Root cause**: Agent treats a reusable concern as project-local because the framework document was not checked first.

**Correct path**:
- Before creating any abstraction, check `framework-map.md` for existing layer ownership.
- If the behavior is reusable across projects → change framework first.
- If purely app-specific → keep in project.
- Use the ownership ladder: `runtime → boot → plugin → extension → exmodule → app`.

**Owner**: `framework-map.md`, `contract-source-rules.md`

## 3. Trusting Graph Results Without Source Verification

**Symptom**: Agent answers "X calls Y" or "X owns Z" based solely on `code-review-graph` output without opening the source files.

**Root cause**: Graph provides structural candidates, not semantic proof. Stale baselines, bundled JavaScript noise, and generated code can pollute results.

**Correct path**:
- Always verify graph results with direct source reads.
- Apply `graph-noise-rules.md` to filter known noise paths.
- If graph baseline and HEAD differ, treat graph as orientation only.

**Owner**: `graph-usage-rules.md`, `graph-noise-rules.md`

## 4. Using Code Graph for Resource-Driven Semantics

**Symptom**: Agent uses `semantic_search_nodes` or `query_graph` to understand `PERM.yml`, `RBAC_RESOURCE`, `entity.json`, Flyway SQL, or `vertx.yml` behavior.

**Root cause**: Code graph indexes Java classes and call relationships. It does not capture YAML/JSON/SQL resource semantics.

**Correct path**:
- For resource-driven behavior, read the resource files directly.
- Use `rg` or `find` for resource tree exploration.
- Graph is strongest on Java classes; weakest on SPI registrations, boot metadata, migration trees.

**Owner**: `graph-usage-rules.md` §4

## 5. Confusing Security Auth Protocol with RBAC Permission

**Symptom**: Agent edits `zero-plugins-security-*` to fix a permission issue, or edits `zero-exmodule-rbac` to fix a login failure.

**Root cause**: Authentication (who are you?) and authorization (what can you do?) are separate concerns owned by different modules.

**Correct path**:
- Login/protocol issues → `security-plugin-flow.md` → matching `zero-plugins-security-*` module.
- Permission/ACL/resource issues → `backend-rbac-rules.md` → `zero-exmodule-rbac`.
- Do not cross the boundary.

**Owner**: `security-plugin-flow.md`, `backend-rbac-rules.md`

## 6. Creating New MXT Files Without SRP Justification

**Symptom**: Agent creates a new `mxt/*.md` file that overlaps with an existing document's concern.

**Root cause**: Agent does not check the existing document boundary audit before writing.

**Correct path**:
- Check `document-boundary-audit.md` first.
- Apply `evolution-rules.md` §3: new file requires a new stable layer, new rule set, or existing overload.
- If the topic fits an existing document → update it instead.
- Run `audit_mxt_coverage.py` after changes.

**Owner**: `document-boundary-audit.md`, `evolution-rules.md`

## 7. Ignoring the `.r2mo/` Contract Authority

**Symptom**: Agent creates or modifies API contracts, entity models, or page specs in source code without checking `.r2mo/` first.

**Root cause**: `.r2mo/` is the source of truth for contract intent. Source code implements the contract; it does not define it.

**Correct path**:
- Before adding or changing an API/entity/page, check `.r2mo/requirements/` and `.r2mo/api/`.
- If the contract exists in `.r2mo/`, align the source code to match.
- If no contract exists, create the `.r2mo/` spec first, then implement.

**Owner**: `contract-source-rules.md`

## 8. Adding Project Bypasses for Framework Bugs

**Symptom**: Agent adds a workaround in a downstream project for a behavior that is clearly a framework bug or missing abstraction.

**Root cause**: Faster to patch locally than to fix upstream. But each bypass accumulates technical debt.

**Correct path**:
- If the fix belongs in framework → change framework first.
- If the fix is mixed → framework first, then remove project workaround.
- Track the workaround with a TODO referencing the framework issue.
- Never add repeated project-local bypasses for the same framework problem.

**Owner**: `contract-source-rules.md`, `agent-quick-start.md` §3

## 9. Reading Too Many MXT Files Before Acting

**Symptom**: Agent loads `README.md`, `framework-map.md`, `agent-quick-start.md`, `mcp-integration-map.md`, and several owner documents before making any decision.

**Root cause**: No token budget discipline. The MXT pack is designed for selective loading, not full consumption.

**Correct path**:
- Start with `ai-decision-tree.md` (this pack's shortest decision entry).
- Follow one branch → open one owner document → verify one source anchor.
- Stop retrieving when: owner known, owner doc read, one anchor verified, no cross-layer suspected.
- Total MXT reading should not exceed 15% of context window.

**Owner**: `mcp-fast-retrieval-rules.md`, `ai-decision-tree.md`

## 10. Mixing Vert.x and Zero Terminology

**Symptom**: Agent uses "controller", "service layer", "repository" instead of Zero-native terms, causing miscommunication with the framework's execution chain.

**Root cause**: Generic web framework terminology does not match Zero's Vert.x-based execution model.

**Correct path**:
- Use Zero terminology: `Agent` (entry), `Addr` (address), `Actor` (handler), `Stub` (contract), `Service` (business logic).
- Use `DBE` for data access, not "repository" or "DAO" (though `DAO` classes exist inside providers).
- Use `@Job` for scheduled tasks, not `@Scheduled`.
- Use `@Actor` for startup components, not `@PostConstruct` or `@Bean`.

**Owner**: `backend-dpa-guide.md`, `abstraction-rules.md`
