# MCP Integration Map

> Load this file when the task starts from a broad Zero framework topic and the agent needs to route that topic to the correct `mxt/` documents, source modules, and MCP graph queries.
> This file owns topic routing only. It does not own graph operation discipline or framework topology.

## 1. Purpose

Use this file to translate a framework topic into:

- the first `mxt/` document to read
- the owning module family
- the highest-value source anchors
- the first useful `code-review-graph` or MCP search target

One-line rule:

```text
Topic first, owner second, graph third, source proof last.
```

## 2. Companion Files

Use this file together with:

| Need | Document |
|---|---|
| one-question-one-answer decision routing | `ai-decision-tree.md` |
| common AI agent mistakes and correct paths | `ai-anti-patterns.md` |
| framework topology and layer ownership | `framework-map.md` |
| shortest MCP retrieval and token saving | `mcp-fast-retrieval-rules.md` |
| document distillation | `distillation-rules.md` |
| duplicate rule purification | `purification-rules.md` |
| graph noise filtering | `graph-noise-rules.md` |
| graph operation discipline | `graph-usage-rules.md` |
| graph-assisted query playbooks | `mcp-code-review-graph-rules.md` |
| text-search entry anchors | `search-hints.md` |

## 3. Core Layer Topics

| Topic | Read First | Owning Modules | Source Anchors | First MCP / Graph Hint |
|---|---|---|---|---|
| `zero-version` version management | `zero-version-guide.md` | `zero-version/`, `zero-version-epoch`, `zero-version-plugins`, `zero-version-extension` | `zero-version/pom.xml`, `zero-version-epoch/pom.xml`, `zero-version-plugins/pom.xml`, `zero-version-extension/pom.xml` | open the version POMs directly |
| `zero-epoch` runtime architecture | `zero-epoch-runtime-guide.md` | `zero-ecosystem/zero-epoch/` | `zero-epoch/pom.xml`, `zero-epoch-cosmic`, `zero-epoch-store`, `zero-epoch-setting`, `zero-epoch-focus` | `semantic_search_nodes('zero-epoch')` with path filter |
| `zero-boot` boot wiring | `zero-boot-wiring-guide.md` | `zero-ecosystem/zero-boot/` | `VertxApplication`, `LauncherApp`, `CloudActor`, `BuildApp`, `BuildPerm` | `semantic_search_nodes('VertxApplication')`, `semantic_search_nodes('BuildApp')` |
| SPI-driven core plugin and extension model | `spi-core-plugin-guide.md` | `zero-extension-skeleton`, `zero-plugins-equip`, `zero-exmodule-*` | `ExBoot`, `SPI_SET`, `META-INF/services`, `HPI.findMany` | `semantic_search_nodes('ExBoot')` |
| local plus Nacos configuration center | `config-center-local-nacos.md` | `zero-epoch-setting`, `zero-epoch-spec`, `zero-epoch-spec-nacos`, `zero-boot-cloud-actor` | `ConfigMod`, `ZeroPower`, `ZeroSource`, `ConfigLoadCloud`, `YmSpec`, `NacosRule` | search `ConfigMod` and `nacos` |
| jOOQ-centered DBE path | `dbe-query-rules.md` | `zero-epoch-store`, exmodule `*-provider` persistence code | `DBE`, `DB`, `DBSActor`, `dslContext`, `DAO`, `serviceimpl` | `semantic_search_nodes('DBE')`, `semantic_search_nodes('DBSActor')` |
| DBS multi-data-source model, static and dynamic | `dbs-multi-datasource.md` | `zero-epoch-store`, `zero-plugins-flyway`, selected exmodules | `DBSActor`, `DBS`, `ofDBS()`, `ofContext()`, `Flyway11Configurator` | search `DBSActor` and `ofDBS` |
| `zero-overlay` bridge to `r2mo-rapid` | `zero-overlay-bridge.md` | `zero-overlay`, `zero-epoch-use` | `zero-overlay/pom.xml`, `EnvironmentVariable`, `EmService` | `semantic_search_nodes('zero-overlay')` |
| `Ut`, `Ux`, `Fx` | `abstraction-rules.md` | `zero-epoch-use`, cross-layer facade usage | `class Ut`, `class Ux`, `class Fx` | `semantic_search_nodes('Ux')`, `semantic_search_nodes('Fx')` |
| job model | `job-model-guide.md` | `zero-epoch-focus`, `zero-epoch-use`, `zero-exmodule-mbseapi` | `@Job`, `JobConfig`, `JobExtractor`, `JobStoreUnity`, `JobStub`, `TaskApi` | search `@Job`, `JobExtractor`, `TaskApi` |
| startup matrix `@Actor` | `actor-startup-matrix.md` | `zero-epoch-setting`, `zero-boot`, plugin actors | `@Actor`, `ZeroModule`, `InquirerClassActor`, `PrimedActor` | `semantic_search_nodes('ZeroModule')`, search `@Actor(` |

## 4. Core Capability Topics

| Topic | Read First | Owning Modules | Source Anchors | First MCP / Graph Hint |
|---|---|---|---|---|
| cache, including Redis | `cache-redis-guide.md` | `zero-plugins-cache*`, `zero-plugins-redis` | plugin POMs, cache providers, Redis integration classes | search `zero-plugins-cache`, `zero-plugins-redis` |
| Elasticsearch capability | `elasticsearch-guide.md` | `zero-plugins-elasticsearch` | `ElasticSearchActor`, `ElasticSearchClient`, `ElasticIndexer`, `ElasticQr` | `semantic_search_nodes('ElasticSearchActor')` |
| Excel import and export model | `excel-import-export-guide.md` | `zero-plugins-excel` | `ExcelActor`, `ExcelClient`, `ExcelImport`, `ExcelExport`, `ExcelAnalyzer`, `ExcelEnvConnect` | `semantic_search_nodes('ExcelActor')`, `semantic_search_nodes('ExcelClient')` |
| Flyway bootstrap and migration loading | `flyway-loading-flow.md` | `zero-plugins-flyway`, `zero-epoch-store`, extension SPI providers | `FlywayActor`, `Flyway11Configurator`, `DBFlyway` | `semantic_search_nodes('FlywayActor')` |
| monitoring | `monitor-center-guide.md` | `zero-plugins-monitor*` | monitor module trees and plugin boot classes | search `zero-plugins-monitor` |
| security processing | `security-plugin-flow.md` | `zero-plugins-security*`, `zero-plugins-oauth2`, `zero-exmodule-rbac` | security plugin modules, `PERM.yml`, `RBAC_RESOURCE`, OAuth2 startup classes | search `zero-plugins-security`, `RBAC_RESOURCE`, `OAuth2ServerActor` |
| session capability | `session-guide.md` | `zero-plugins-session` | `SessionActor`, `SessionClient`, `SessionManager`, `SessionProvider` | `semantic_search_nodes('SessionActor')` |
| OAuth2 capability | `oauth2-capability-guide.md` | `zero-plugins-oauth2` | `OAuth2ServerActor`, `OAuth2ClientActor`, `OAuth2Manager`, `Oauth2RegisteredClientDao` | `semantic_search_nodes('OAuth2ServerActor')` |
| Neo4j integration | `neo4j-guide.md` | `zero-plugins-neo4j` | plugin module classes and config nodes | search `zero-plugins-neo4j` |
| Swagger and OpenAPI docs | `swagger-openapi-guide.md` | `zero-plugins-swagger` | `SwaggerActor`, `SwaggerAnalyzer`, `SwaggerAxis`, `SwaggerManager` | `semantic_search_nodes('SwaggerActor')` |
| trash and recycle-bin capability | `trash-capability-guide.md` | `zero-plugins-trash` | `TrashActor`, `TrashClient`, `TrashProvider`, `TrashManager` | `semantic_search_nodes('TrashActor')` |
| email capability | `email-capability-guide.md` | `zero-plugins-email`, `zero-plugins-security-email` | plugin module classes and security-email hooks | search `zero-plugins-email` |
| SMS capability | `sms-capability-guide.md` | `zero-plugins-sms`, `zero-plugins-security-sms` | plugin module classes and security-sms hooks | search `zero-plugins-sms` |
| Weco capability | `weco-capability-guide.md` | `zero-plugins-weco`, `zero-plugins-security-weco` | plugin module classes and security-weco hooks | search `zero-plugins-weco` |
| WebSocket capability | `websocket-guide.md` | `zero-plugins-websocket` | plugin module classes and EventBus bridge points | search `zero-plugins-websocket` |

## 5. Extension Center Topics

| Topic | Read First | Owning Modules | Source Anchors | First MCP / Graph Hint |
|---|---|---|---|---|
| CRUD engine | `crud-engine-guide.md` | `zero-extension-crud` | CRUD `Addr`, metadata runtime classes, import/export/search handlers | `semantic_search_nodes('Addr')` with CRUD path filter |
| extension skeleton | `extension-skeleton-guide.md` | `zero-extension-skeleton` | `ExBoot`, `SPI_SET`, `spi/`, `META-INF/services` | `semantic_search_nodes('ExBoot')` |
| extension API | `extension-api-guide.md` | `zero-extension-api` | API-side extension contracts and routing base | search `zero-extension-api` |
| ambient | `exmodule-ambient-guide.md` | `zero-exmodule-ambient` | `-api`, `-domain`, `-provider`, `-ui` | search `zero-exmodule-ambient` |
| ERP | `exmodule-erp-guide.md` | `zero-exmodule-erp` | `-api`, `-domain`, `-provider`, `-ui` | search `zero-exmodule-erp` |
| finance | `exmodule-finance-guide.md` | `zero-exmodule-finance` | `-api`, `-domain`, `-provider`, `-ui` | search `zero-exmodule-finance` |
| graphic | `exmodule-graphic-guide.md` | `zero-exmodule-graphic` | `-api`, `-domain`, `-provider`, `-ui` | search `zero-exmodule-graphic` |
| integration | `exmodule-integration-guide.md` | `zero-exmodule-integration` | integration `Addr`, service stubs, provider adapters | search `zero-exmodule-integration` |
| LBS | `exmodule-lbs-guide.md` | `zero-exmodule-lbs` | `-api`, `-domain`, `-provider`, `-ui` | search `zero-exmodule-lbs` |
| MBSE API | `exmodule-mbseapi-guide.md` | `zero-exmodule-mbseapi` | `TaskApi`, `JobStub`, `JobService`, metadata/job components | search `zero-exmodule-mbseapi` |
| MBSE Core | `exmodule-mbsecore-guide.md` | `zero-exmodule-mbsecore` | modeling metadata, JDBC and configuration bridges | search `zero-exmodule-mbsecore` |
| modulat | `exmodule-modulat-guide.md` | `zero-exmodule-modulat` | module config and UI coordination points | search `zero-exmodule-modulat` |
| RBAC | `exmodule-rbac-guide.md` | `zero-exmodule-rbac` | `SResource`, `SResourceDao`, `ScDetent`, `RBAC_RESOURCE`, `seekSyntax`, `seekConfig` | search `zero-exmodule-rbac`, `seekSyntax` |
| report | `exmodule-report-guide.md` | `zero-exmodule-report` | report provider, dataset composition, output rules | search `zero-exmodule-report` |
| tpl | `exmodule-tpl-guide.md` | `zero-exmodule-tpl` | template-center APIs, provider logic, UI module | search `zero-exmodule-tpl` |
| UI | `exmodule-ui-guide.md` | `zero-exmodule-ui`, `zero-ui/src/extension/` | `UiForm`, `UiValve`, extension component trees | search `UiForm`, `zero-exmodule-ui` |
| workflow | `exmodule-workflow-guide.md` | `zero-exmodule-workflow` | workflow `Addr`, runtime forms, queue and actor flows | search `zero-exmodule-workflow` |

## 6. Composite Capability Topics

| Topic | Read First | Owning Modules | Source Anchors | First MCP / Graph Hint |
|---|---|---|---|---|
| attachment upload and FTP-style integration | `attachment-storage-integration-guide.md` | `zero-exmodule-ambient`, `zero-exmodule-integration` | `ExAttachment`, integration provider adapters, ambient resources | search `ExAttachment`, `zero-exmodule-integration` |
| static modeling chain | `static-modeling-guide.md` | `zero-extension-crud`, `zero-exmodule-ui`, `zero-exmodule-mbsecore` | CRUD metadata, UI form adapters, modeling metadata components | search `entity.json`, `UiForm`, `zero-exmodule-mbsecore` |
| logging and activity rules | `activity-log-guide.md` | `zero-exmodule-ambient`, shared activity SPI owners | `ExActivity`, ambient provider classes, audit/resource trees | search `ExActivity`, `zero-exmodule-ambient` |
| configurable report center | `report-center-guide.md` | `zero-exmodule-report` | report provider classes, dataset and join components | search `zero-exmodule-report` |
| ACL and reinforced authorization domain | `acl-authorization-guide.md` | `zero-exmodule-rbac`, `zero-plugins-security*` | `ScPermit`, `ScSeeker`, `RBAC_RESOURCE`, `PERM.yml` | search `ScPermit`, `RBAC_RESOURCE` |
| modular configuration service and unified UI | `modulat-ui-unified-guide.md` | `zero-exmodule-modulat`, `zero-exmodule-ui` | modular config provider classes, UI extension trees, `UiValve` | search `zero-exmodule-modulat`, `UiValve` |

## 7. Topic-to-Graph Translation Rules

When a user starts with business words, translate them before using MCP graph tools.

| Business Wording | Framework Wording |
|---|---|
| standard list or detail page | CRUD engine, `entity.json`, `column.json` |
| scheduled task | `@Job`, `JobExtractor`, `TaskApi` |
| startup order | `@Actor`, `ZeroModule`, `PrimedActor` |
| remote config center | `ConfigMod`, `NacosRule`, `zero-epoch-spec-nacos` |
| multiple data sources | `DBSActor`, `ofDBS`, `Flyway11Configurator` |
| permission and ACL | `RBAC_RESOURCE`, `ScPermit`, `ScSeeker`, `PERM.yml` |
| plugin extension | `ExBoot`, `SPI_SET`, `META-INF/services` |
| dynamic form or visibility | `UiForm`, `UiValve`, `zero-ui/src/extension/` |

## 8. Evidence Rule

This file routes the topic.
The final answer still requires evidence from:

1. `mxt/` documents for framework intent
2. source files for executable behavior
3. resource files for metadata-driven semantics
4. MCP graph output for structural acceleration

## 9. Summary Rule

```text
Use this file to decide where to look.
Use graph-usage-rules.md to decide how to query.
Use mcp-code-review-graph-rules.md to decide which graph playbook fits.
```
