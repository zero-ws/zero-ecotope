# Biological Network Node Map

> Node taxonomy for the MD-driven AI-agent network across the four framework repositories.
> This file owns classification only.

## 1. Node Classes

| Node class | Function | Typical artifact |
|---|---|---|
| signal node | captures raw user wording or exact symbols | user prompt, class name, module name, error message |
| relay node | narrows wording into one route family | trigger matrix, shortest-path doc, decision tree |
| memory node | stores stable ownership rules | owner guide, boundary guide, governance guide |
| probe node | performs direct codebase lookup | `code-review-graph`, `rg`, exact source read |
| proof node | confirms final behavior | Java source, XML, YAML, JSON, SQL, `META-INF/services` |
| handoff node | transfers context into another repository | handshake rule, consumer rule, cross-repo contract |

## 2. Repository Node Map

| Repository | Primary node role | High-value relay nodes | High-value memory nodes | High-value probe nodes |
|---|---|---|---|---|
| `zero-ecotope` | delivery/runtime organ | `ai-decision-tree.md`, `mcp-fast-retrieval-rules.md` | `extension-*.md`, `exmodule-*.md`, plugin guides | `graph-usage-rules.md`, `mcp-code-review-graph-rules.md` |
| `r2mo-rapid` | Spring/shared runtime organ | `mcp-shortest-path.md`, `framework-trigger-matrix.md` | Spring/shared capability guides | `code-review-graph-usage.md`, `mcp-route-code-review-graph.md` |
| `r2mo-spec` | shared semantic memory organ | `search-hints.md` | `spec-boundary.md`, `model-doc-protocol.md` | direct metadata/OpenAPI reads |
| `rachel-momo` | governance memory organ | `consumer-agent-rules.md`, `mcp-agent-rules.md` | `version-governance.md`, `aspectj-governance.md` | POM search, consumer detector |

## 3. Zero Node Families

### 3.1 Relay nodes

- `ai-decision-tree.md`
- `mcp-integration-map.md`
- `mcp-fast-retrieval-rules.md`

### 3.2 Memory nodes

- `extension-skeleton-guide.md`
- `extension-crud-guide.md`
- `extension-aop-guide.md`
- `exmodule-*-guide.md`

### 3.3 Probe nodes

- `graph-usage-rules.md`
- `mcp-code-review-graph-rules.md`
- direct `code-review-graph` commands when the owner layer is already plausible

## 4. R2MO Node Families

### 4.1 Relay nodes

- `mcp-shortest-path.md`
- `framework-trigger-matrix.md`
- `mcp-route-*.md`

### 4.2 Memory nodes

- `spring-runtime-guide.md`
- `spring-aop-guide.md`
- `io-boundary.md`
- `dbe-implementation-boundary.md`

### 4.3 Probe nodes

- `code-review-graph-usage.md`
- `code-review-graph-r2mo-analysis.md`
- direct `code-review-graph` lookup for structural deep retrieval

## 5. Spec Node Families

### 5.1 Relay nodes

- `search-hints.md`

### 5.2 Memory nodes

- `spec-boundary.md`
- `framework-map.md`
- `interface-semantics.md`

### 5.3 Proof nodes

- `src/main/resources/openapi/marker.md`
- `metadata/**/*.md`
- `src/main/resources/openapi/**/*.md`

Rule:

```text
`r2mo-spec` has proof nodes for meaning, not runtime probe nodes for execution.
```

## 6. Momo Node Families

### 6.1 Relay nodes

- `consumer-agent-rules.md`
- `mcp-agent-rules.md`

### 6.2 Memory nodes

- `version-governance.md`
- `stack-bom-map.md`
- `aspectj-governance.md`

### 6.3 Proof nodes

- root `pom.xml`
- `rachel-momo-stack/pom.xml`
- `rachel-momo-0216/pom.xml`

Rule:

```text
Momo proves version exposure and plugin governance, not runtime advice execution.
```

## 7. AOP-Specific Node Rule

When the signal includes `aop`, `aspect`, `advice`, `around`, `before`, `after`, `pointcut`, or `captcha aspect`:

- route to `r2mo-rapid` if the behavior is Spring-side or `r2mo-spring-security`-side
- route to `zero-ecotope` if the behavior is Zero overlay, CRUD advice, exmodule-local advice, or RBAC/ACL hook logic
- route to `rachel-momo` only for AspectJ dependency/plugin governance
- never route to `r2mo-spec` as the owner

## 8. Final Node Rule

```text
Signals pick the organ.
Relay nodes pick the owner document.
Probe nodes narrow the code.
Proof nodes close the answer.
```
