# MCP Code Review Graph Playbooks

> Load this file after `mcp-integration-map.md` and `graph-usage-rules.md`.
> This file owns graph-assisted playbooks only. It does not own pack entry rules, framework topology, or graph safety policy.

## 1. Scope

Use this file when:

- the owning layer is already plausible
- the topic is already translated into framework terms
- the next step is choosing the right MCP or graph query sequence

One-line rule:

```text
This file answers "which graph playbook fits next", not "what does the framework contain".
```

## 2. Fixed Preconditions

Before using these playbooks:

1. confirm the stack is Zero-first
2. load `framework-map.md`
3. route the topic with `mcp-integration-map.md`
4. follow `graph-usage-rules.md` for repo root, evidence, and refresh discipline

## 3. Repository Anchor

Always use the explicit Zero repository root:

```text
/Users/lang/zero-cloud/app-zero/zero-ecotope
```

Rule:

```text
Never run framework graph queries without an explicit repo_root for zero-ecotope.
```

## 4. Playbook Selection

| If the task is about... | Use this playbook |
|---|---|
| symbol ownership | section 5 |
| request path or EventBus flow | section 6 |
| SPI contract to implementation | section 7 |
| plugin capability ownership | section 8 |
| CRUD and metadata-driven runtime | section 9 |
| RBAC and permission resources | section 10 |
| startup or configuration wiring | section 11 |
| exmodule blast radius | section 12 |

## 5. Symbol Ownership Playbook

Use when the task starts from one symbol, class, interface, or module name.

1. `semantic_search_nodes('<symbol>', repo_root='<zero-ecotope>')`
2. inspect the returned path prefix
3. classify the file into runtime, boot, plugin, extension, exmodule, or UI
4. open the returned source file directly
5. verify ownership before reading callers or impact

Best targets:

- `ExBoot`
- `DBSActor`
- `FlywayActor`
- `ConfigMod`
- `UiForm`
- `TaskApi`

## 6. Request and Event Flow Playbook

Use when the task mentions API, address, actor, route, or EventBus flow.

1. `semantic_search_nodes('<ActorName or Addr>', repo_root='<zero-ecotope>')`
2. `query_graph(pattern='children_of', target='<Addr or API target>', repo_root='<zero-ecotope>')`
3. `query_graph(pattern='callees_of', target='<Actor or service method>', repo_root='<zero-ecotope>')`
4. `query_graph(pattern='callers_of', target='<service method>', repo_root='<zero-ecotope>')`
5. confirm the DPA chain in source

Mental model:

```text
Agent -> Addr -> Actor -> Stub -> Service -> DBE
```

## 7. SPI Contract Playbook

Use when the task is about extension points, override hooks, or missing SPI wiring.

1. `semantic_search_nodes('ExBoot', repo_root='<zero-ecotope>')`
2. `semantic_search_nodes('<SPI name>', repo_root='<zero-ecotope>')`
3. open `ExBoot` and verify membership in `SPI_SET`
4. search the filesystem for `META-INF/services/<fqcn>`
5. inspect provider implementations under `*-provider` or plugin modules

High-value targets:

- `ConfigMod`
- `ExActivity`
- `ScPermit`
- `UiValve`

## 8. Plugin Capability Playbook

Use when the task is about cache, Redis, monitor, security, Neo4j, Excel, email, SMS, Weco, or WebSocket.

1. search the plugin module by exact artifact or directory name
2. `semantic_search_nodes('<plugin anchor>', repo_root='<zero-ecotope>')`
3. constrain reading to `zero-ecosystem/zero-plugins-equip/`
4. verify whether the requirement is capability-level or business-usage-level
5. if it is business-level, continue into the consuming exmodule instead of changing the plugin first

Anti-pattern:

```text
Do not edit a plugin just because the requirement mentions a plugin capability word.
```

## 9. CRUD and Metadata Playbook

Use when the task mentions standard list, detail, edit, import, export, search, `entity.json`, or `column.json`.

1. route through `crud-engine-guide.md`
2. use graph search to find the owning Java module for CRUD runtime classes
3. switch to direct file reads for metadata resources
4. verify whether the behavior is owned by CRUD runtime, exmodule metadata, or app resources

Mandatory rule:

```text
Graph finds the Java owner; metadata files still decide CRUD semantics.
```

## 10. RBAC and Permission Playbook

Use when the task mentions `RBAC_RESOURCE`, `RBAC_ROLE`, `PERM.yml`, `seekSyntax`, `seekConfig`, or ACL.

1. `semantic_search_nodes('ScPermit', repo_root='<zero-ecotope>')`
2. `semantic_search_nodes('ScSeeker', repo_root='<zero-ecotope>')`
3. search the filesystem for `RBAC_RESOURCE`, `RBAC_ROLE`, and `PERM.yml`
4. open resource files directly
5. map the result back to `zero-exmodule-rbac` or security plugin ownership

Mandatory rule:

```text
Do not stop at graph evidence for RBAC tasks.
```

## 11. Startup and Configuration Playbook

Use when the task mentions `@Actor`, startup order, config center, Nacos, remote config, or multi-data-source boot behavior.

1. `semantic_search_nodes('ZeroModule', repo_root='<zero-ecotope>')`
2. `semantic_search_nodes('ConfigMod', repo_root='<zero-ecotope>')`
3. search for `@Actor(`, `DBSActor`, `ZeroPower`, `ZeroSource`, `ConfigLoadCloud`, `NacosRule`
4. open YAML and spec resources directly when configuration semantics matter
5. only then inspect impact radius or callers

## 12. Exmodule Blast Radius Playbook

Use when the change touches one exmodule and the task asks what else moves with it.

1. `detect_changes(repo_root='<zero-ecotope>', changed_files=[...])`
2. `get_impact_radius(repo_root='<zero-ecotope>', changed_files=[...])`
3. `get_affected_flows(repo_root='<zero-ecotope>', changed_files=[...])`
4. constrain review to the exmodule subtree plus the directly referenced shared contracts
5. verify real callers and resource owners in source

## 13. Resource-Heavy Fallback Table

| Resource Type | Do This After Graph Search |
|---|---|
| `PERM.yml` and RBAC resources | open resource trees directly |
| `entity.json` and `column.json` | inspect metadata files directly |
| `vertx.yml` and cloud config | inspect config files and config loaders |
| Flyway SQL and seed data | inspect SQL locations and `DBFlyway` SPI files |
| plugin resource trees | inspect `src/main/resources/` directly |

## 14. Final Review Questions

Before returning a graph-assisted answer:

1. Did I already classify the layer?
2. Did I use the smallest playbook that fits?
3. Did I verify source files directly?
4. If resources matter, did I read the resources directly?
5. Is the conclusion framework-reusable or only local to one module?

## 15. Summary Rule

> `mcp-integration-map.md` routes the topic.
> `graph-usage-rules.md` controls graph discipline.
> This file provides the actual query playbooks.
