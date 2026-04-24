# AI Decision Tree

> Load this file FIRST when an AI agent needs to decide where to look or what to do for a Zero framework task.
> This file owns the top-level decision logic only. It routes to owner documents for details.

## 1. Purpose

One file → one decision. Read this file, follow the branch, open exactly one owner document.

Token budget: reading this file should consume no more than 5% of context.

If the task is explicitly cross-repository across Momo, Spec, R2MO, and Zero rather than Zero-only, switch to `biological-network-overview.md`.

## 2. Entry Decision: Is This a Framework Task?

```
User request received
  |
  ├─ Does the project pom.xml parent resolve to zero-0216?
  |    NO → Stay in downstream project context. Do not load MXT.
  |    YES ↓
  |
  ├─ Is the task about framework behavior, not app-local customization?
  |    APP-LOCAL → Stay in project. Use .r2mo/ and project-local rule files when present.
  |    FRAMEWORK ↓
  |
  └─ Continue to Section 3.
```

## 3. Ownership Decision: Which Layer?

```
What is the task about?
  |
  ├─ Container lifecycle, EventBus, Verticle deployment
  |    → zero-epoch-cosmic → zero-epoch-runtime-guide.md
  |
  ├─ Boot wiring, launcher, @Actor startup, installation import
  |    → zero-boot → zero-boot-wiring-guide.md
  |
  ├─ Cache, Redis, Excel, Flyway, Monitor, Neo4j, Email, SMS, Weco, WebSocket, Security, Session, OAuth2, Swagger, Trash
  |    → zero-plugins-equip → plugin-layer-map.md → specific *-guide.md
  |
  ├─ CRUD engine, extension skeleton, extension API
  |    → zero-extension-* → extension-skeleton-guide.md or crud-engine-guide.md
  |
  ├─ Business module: ambient, erp, finance, graphic, integration, lbs, mbseapi, mbsecore, modulat, rbac, report, tpl, ui, workflow
  |    → zero-exmodule-* → specific exmodule-*-guide.md
  |
  ├─ Frontend component or dynamic form
  |    → zero-ui → dual-side-development.md or exmodule-ui-guide.md
  |
  └─ Version / dependency / BOM
       → zero-version → zero-version-guide.md
```

## 4. CRUD Decision: Use Engine or Handwrite?

```
Is the requirement a standard list/detail/edit page?
  |
  ├─ YES → Can it be modeled by entity.json + column.json?
  |    YES → Use CRUD engine → crud-engine-guide.md
  |    NO  → Handwrite provider → backend-dpa-guide.md
  |
  └─ NO → Does it need custom business logic in the service layer?
       YES → Handwrite → backend-dpa-guide.md
       NO  → Re-evaluate; most standard pages should use CRUD engine
```

## 5. Security Decision: Auth or RBAC?

```
Is the task about login/authentication protocol?
  |
  ├─ YES → Which protocol?
  |    JWT → security-plugin-flow.md (zero-plugins-security-jwt)
  |    LDAP → security-plugin-flow.md (zero-plugins-security-ldap)
  |    OAuth2 login → security-plugin-flow.md (zero-plugins-security-oauth2)
  |    Email/SMS OTP → security-plugin-flow.md (zero-plugins-security-email/sms)
  |    Weco → security-plugin-flow.md (zero-plugins-security-weco)
  |
  └─ NO → Is it about permission/resource/ACL?
       YES → backend-rbac-rules.md or exmodule-rbac-guide.md
       NO  → Re-classify the task
```

## 6. Config Decision: Local or Cloud?

```
Is the config issue about vertx.yml or local config?
  |
  ├─ YES → config-center-local-nacos.md (local section)
  |
  └─ NO → Is it about Nacos / remote config center?
       YES → config-center-local-nacos.md (Nacos section)
       NO  → Is it an environment variable contract?
            YES → environment-contracts.md
            NO  → Re-classify
```

## 7. Graph Decision: Use Graph or Read Source?

```
Is the question structural (who owns, who calls, where defined)?
  |
  ├─ YES → Use code-review-graph → graph-usage-rules.md
  |         Then verify source files directly.
  |
  └─ NO → Is it about resource semantics (PERM.yml, entity.json, SQL, YAML)?
       YES → Read resource files directly. Graph is weaker here.
       NO  → Read source files directly first.
```

## 8. Change Placement Decision: Framework or Project?

```
Where should the fix go?
  |
  ├─ Is the behavior missing from the framework but reusable?
  |    YES → Change framework first, then adapt project.
  |            → framework-map.md for ownership
  |
  ├─ Is it a framework bug or incorrect default?
  |    YES → Change framework.
  |
  ├─ Is it purely app-local customization?
  |    YES → Change project only.
  |
  └─ Mixed (framework gap + project workaround)?
       YES → Framework fix first. Remove project workaround after.
              → contract-source-rules.md for authority
```

## 9. Quick Reference: One-Question-One-Answer

| Question | Answer | Owner Document |
|---|---|---|
| Where does this code belong? | Classify by layer first | `framework-map.md` |
| Should I use CRUD engine? | If standard list/detail → yes | `crud-engine-guide.md` |
| Which auth module? | Match login protocol | `security-plugin-flow.md` |
| Which cache backend? | Local → caffeine; distributed → redis | `cache-redis-guide.md` |
| Where to put new SPI? | `zero-extension-skeleton` | `extension-skeleton-guide.md` |
| How to add new permission? | `RBAC_RESOURCE` + `PERM.yml` | `backend-rbac-rules.md` |
| Startup order wrong? | `@Actor` matrix | `actor-startup-matrix.md` |
| Config not loading? | Local or Nacos path | `config-center-local-nacos.md` |
| Multi-DB needed? | `DBSActor` + `ofDBS` | `dbs-multi-datasource.md` |
| Is this a framework bug? | Check ownership ladder | `agent-quick-start.md` §3 |
