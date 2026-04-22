# Search Hints

> Fast navigation patterns for AI agents and developers working in Zero Ecotope.

## 1. Find the Layer Structure

Start with the POM files:

- Root: `zero-ecotope/pom.xml`
- Ecosystem: `zero-ecotope/zero-ecosystem/pom.xml`
- Extension version list: `zero-ecotope/zero-version/zero-version-extension/pom.xml`

Search patterns:
- `<module>zero-epoch</module>`
- `<module>zero-plugins-equip</module>`
- `<module>zero-plugins-extension</module>`
- `<artifactId>zero-exmodule-`

## 1.5 Find the Shortest Agent Route

Use these owner documents before broad searches:

- `mcp-fast-retrieval-rules.md` — shortest MCP retrieval path
- `mcp-integration-map.md` — topic-to-owner routing
- `distillation-rules.md` — document compression rules
- `purification-rules.md` — duplicate rule cleanup rules
- `graph-noise-rules.md` — generated and tooling graph-noise filters

Rule:

```text
Do not search the full repository until the owner document is known.
```

## 2. Find Core Framework Code

Code markers:
- `#BOOT-` — container lifecycle steps
- `#REQ-` — request processing steps
- `#SPI` — SPI-related code
- `#PIN` — cross-plugin coupling point

High-order object declarations:
- `class HPI`
- `class DBE`
- `class HED`
- `class HOI`
- `class Fx`
- `class Ux`
- `class Ut`

Database navigation anchors:
- `DBE`
- `jOOQ`
- `DAO`
- `class .*Db`
- `interface .*Dao`
- `dslContext`

## 2.5 Find Database Access Paths

Do not misread Zero as "async-only". Database access is also a primary path, especially in management-heavy systems.

Search anchors:
- `DBE` — unified framework data-access style
- `class .*Db` — DB helper or jOOQ context wrapper
- `jOOQ` / `dslContext` — direct jOOQ DAO usage in exmodule providers
- `DAO` / `interface .*Dao` — repository-style data access interfaces
- `serviceimpl` — common hotspot where business rules meet database access

Interpretation:
- `zero-epoch-store` is the canonical module owning the DBE path.
- In real exmodule implementations, database access typically lands in `*-provider`'s service or DB class layer.
- The DBE style provides a consistent query/pager/sorter syntax, but actual implementations often also contain direct jOOQ DAO paths.
- Agents should not assume Zero = event-only; management-type exmodules (`zero-exmodule-rbac`, `zero-exmodule-ambient`, `zero-exmodule-finance`) all use persistent DB access heavily.

## 3. Find Extension Points

Key files:
- `zero-extension-skeleton/.../boot/ExBoot.java`
- `zero-extension-skeleton/.../spi/`

Owner document:
- `extension-skeleton-guide.md`

Search patterns:
- `SPI_SET`
- `HPI.findMany`
- `interface Ex`
- `interface Sc`
- `interface Ui`

Pattern meanings:
- `Ex*` — business extension SPIs
- `Sc*` — security/permission SPIs
- `Ui*` — frontend-aligned SPIs

## 4. Find ExModule Boundaries

Search patterns:
- `zero-exmodule-*/zero-exmodule-*-api`
- `zero-exmodule-*/zero-exmodule-*-domain`
- `zero-exmodule-*/zero-exmodule-*-provider`
- `zero-exmodule-rbac`
- `SResourceDao`
- `ScDetent`
- `class Addr`
- `interface .*Stub`
- `serviceimpl`

Rule:
- `api` layer holds `Actor` / `Agent` / `Addr`.
- `domain` layer holds `Stub` / model / spec.
- `provider` layer holds implementations.
- Only depend on `*-api`, never on `*-provider` from outside the exmodule.
- Use `exmodule-rbac-guide.md` when the task is specifically about `zero-exmodule-rbac` ownership.

## 5. Find Plugin Capability

Directory: `zero-ecosystem/zero-plugins-equip/`

Search patterns:
- `zero-plugins-security-`
- `zero-plugins-monitor-`
- `zero-plugins-cache-`
- `zero-plugins-redis`
- `zero-plugins-elasticsearch`
- `zero-plugins-swagger`
- `zero-plugins-session`
- `zero-plugins-oauth2`
- `zero-plugins-trash`
- `zero-plugins-websocket`

Capability owner documents:
- `elasticsearch-guide.md`
- `swagger-openapi-guide.md`
- `session-guide.md`
- `oauth2-capability-guide.md`
- `trash-capability-guide.md`

Judgment rule: if it looks like a third-party adapter, it belongs in the plugin layer. If it expresses a business rule, it belongs in an exmodule or the app layer.

## 6. Find Frontend Correspondences

Backend:
- `zero-extension-api`
- `UiForm`, `UiValve`, `UiApeak`

Frontend:
- `zero-ui/src/extension/`
- `src/extension/components`
- `src/extension/ecosystem`
- `src/extension/library`

Search patterns:
- `src/extension`
- `UiForm`
- `UiValve`
- `class Addr`

## 6.5 Find Environment and Runtime Contract

Environment variables in Zero are architectural, not incidental. Before modifying any business module or plugin to fix a config issue, inspect the env/runtime contract first.

Search anchors:
- `vertx.yml`
- `ConfigMod`
- `zero-epoch-setting`
- `TENANT_ID`
- `APP_ID`
- `APP_KEY`
- `LANG`
- `STYLE`

Interpretation:
- `vertx.yml` is the primary configuration entry point for Zero applications.
- `ConfigMod` SPI is the extension point for module-level configuration override.
- `zero-epoch-setting` is the canonical module owning configuration loading.
- Multi-tenant, multi-language, multi-style, and multi-app setups all depend on env/runtime contract being correct before plugins and exmodules behave as expected.
- If a plugin or exmodule behaves unexpectedly, check env and runtime config before touching module code.

## 7. Layer Decision Checklist

Before placing code, answer these three questions:

1. Is this adapting a third-party capability, or expressing a business rule?
2. Is this a shared extension contract, or one module's internal implementation?
3. Is this reusable across projects, or unique to this one application?

Decision mapping:
- Third-party adapter → `zero-plugins-equip`
- Shared extension contract → `zero-extension-skeleton`
- Reusable business implementation → `zero-exmodule-*`
- Project-specific customization → application layer

## 8. High-Value Runtime Anchors

Use these anchors when the task is startup-heavy and a generic text search is still faster than graph traversal.

Startup and actor anchors:
- `class ZeroLauncher`
- `class ZeroModule`
- `@Actor(`
- `class PrimedActor`

Flyway and migration anchors:
- `class FlywayActor`
- `class Flyway11Configurator`
- `interface DBFlyway`
- `META-INF/services/io.zerows.epoch.store.DBFlyway`

App and permission bootstrap anchors:
- `class BuildApp`
- `class BuildPerm`
- `class BuildMenuLoader`
- `class BuildMenuPersister`
- `RBAC_RESOURCE`
- `RBAC_ROLE`

OAuth2 anchors:
- `class OAuth2Flyway`
- `class OAuth2ServerActor`
- `class OAuth2ClientActor`
- `Oauth2RegisteredClient`

Rule:

```text
Use text search to land on the owner quickly. Use graph rules only after the owner path is plausible.
```

## 9. Next Document

When ownership depends on structure rather than one text hit, continue with:

- `graph-usage-rules.md`
- `mcp-code-review-graph-rules.md`
