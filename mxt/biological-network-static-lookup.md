# Biological Network Static Lookup

> Static lookup path for AI agents across `zero-ecotope`, `r2mo-rapid`, `r2mo-spec`, and `rachel-momo`.
> Use this file before graph expansion when the task starts from wording, modules, or stable ownership questions.

## 1. Static Lookup Goal

The goal is to answer:

- which repository owns the topic
- which owner document should be read first
- which proof anchor should close the decision

One-line rule:

```text
Static lookup answers "where should I read first" before asking "what calls what".
```

## 2. Static Lookup Pipeline

Use this order:

1. classify the signal
2. choose the repository organ
3. choose one relay document
4. open one owner document
5. verify one source/resource proof anchor

Stop when:

- repository ownership is clear
- one owner document has been read
- one proof anchor confirms the decision

Do not pull in a third or fourth repository unless the current repository or current repository pair cannot close the question.

## 3. Signal Classification

| Signal shape | First route |
|---|---|
| BOM, parent, version, plugin management, dependency alignment | `rachel-momo` |
| marker, metadata, OpenAPI schema, shared model meaning | `r2mo-spec` |
| Spring runtime, Spring Security, `r2mo-spring-*`, shared capability modules | `r2mo-rapid` |
| Zero plugin, Zero extension, exmodule, Vert.x runtime, CRUD delivery | `zero-ecotope` |

If the wording is mixed:

1. governance first
2. shared semantics second
3. runtime third
4. business delivery last

## 4. Repository Routes

### 4.1 Zero route

Use:

```text
ai-decision-tree.md -> mcp-integration-map.md -> one owner doc -> source/resource
```

Best owner families:

- `extension-*.md`
- `exmodule-*.md`
- plugin capability guides

### 4.2 R2MO route

Use:

```text
mcp-shortest-path.md -> framework-trigger-matrix.md or one mcp-route doc -> one owner doc -> source
```

Best owner families:

- Spring guides
- shared capability boundaries
- boot assembly guides

### 4.3 Spec route

Use:

```text
spec-boundary.md -> search-hints.md -> metadata/openapi proof
```

### 4.4 Momo route

Use:

```text
consumer-agent-rules.md -> mcp-agent-rules.md or pom-analysis.md -> pom proof
```

## 4.5 Pairwise Static Processing

Preferred mode:

```text
one repository -> done
or
two repositories -> done
```

Use a third repository only when:

- the first repository proves governance but not runtime
- the first repository proves shared meaning but not execution
- the second repository still cannot close the proof with source/resources

Anti-pattern:

```text
Do not route every mixed question through all four repositories by default.
```

## 5. Static AOP Routing

Use this fixed split:

| AOP wording | Owner repository | First owner document |
|---|---|---|
| Spring AOP, `@Aspect`, Spring Security advice, captcha aspect | `r2mo-rapid` | `spring-aop-guide.md` |
| Zero overlay AOP, CRUD AOP, exmodule local advice/hook | `zero-ecotope` | `extension-aop-guide.md` |
| AspectJ version/plugin/dependency governance | `rachel-momo` | `aspectj-governance.md` |
| marker/spec contract wording mixed with AOP | do not stay in `r2mo-spec` | hand off to runtime repo |

## 6. Static Cross-Repo Examples

### 6.1 `captcha aspect`

```text
signal -> r2mo-rapid -> framework-trigger-matrix.md -> spring-aop-guide.md -> CaptchaValidationAspect
```

### 6.2 `PERM.yml` with exmodule wording

```text
signal -> zero-ecotope -> exmodule-rbac-guide.md or backend-rbac-rules.md -> resource proof
```

### 6.3 `Spring Boot version drift`

```text
signal -> rachel-momo -> version-governance.md -> root pom.xml property
```

### 6.4 `shared model marker meaning`

```text
signal -> r2mo-spec -> spec-boundary.md -> marker.md or metadata proof
```

## 7. When Static Lookup Is Not Enough

Escalate to dynamic lookup when any is true:

- one symbol exists in multiple repositories
- ownership is known but implementation spread is unclear
- the task asks for callers, callees, impact radius, or execution flow
- the change touches shared contracts plus runtime modules

Then continue in `biological-network-dynamic-lookup.md`.
