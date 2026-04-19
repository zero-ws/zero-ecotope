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
- `class Addr`
- `interface .*Stub`
- `serviceimpl`

Rule:
- `api` layer holds `Actor` / `Agent` / `Addr`.
- `domain` layer holds `Stub` / model / spec.
- `provider` layer holds implementations.
- Only depend on `*-api`, never on `*-provider` from outside the exmodule.

## 5. Find Plugin Capability

Directory: `zero-ecosystem/zero-plugins-equip/`

Search patterns:
- `zero-plugins-security-`
- `zero-plugins-monitor-`
- `zero-plugins-cache-`
- `zero-plugins-redis`
- `zero-plugins-elasticsearch`
- `zero-plugins-websocket`

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

## 8. Graph-Assisted MCP Navigation

When source relationships matter more than a single text hit, use [`mcp-code-review-graph-rules.md`](mcp-code-review-graph-rules.md) after this file.

Current registered graph identity:

```text
Alias: mxt-zero
Repo:  /Users/lang/zero-cloud/app-zero/zero-ecotope
```

Recommended command checks:

```bash
code-review-graph repos
code-review-graph status --repo /Users/lang/zero-cloud/app-zero/zero-ecotope
```

Graph search rules:
- Always pass the explicit `repo_root` for MCP tool calls.
- Filter backend framework analysis to `zero-ecosystem/` first.
- Filter frontend extension analysis to `zero-ui/src/extension/` first.
- Ignore `.obsidian/plugins` JavaScript graph communities unless the task is explicitly about repository documentation tooling.
- Use graph results to find candidate relationships, then open source files to verify behavior.

- For YAML / JSON / metadata-driven tasks, do not stop at graph results; continue into resource trees under `plugins/`, `model/`, `security/`, and `src/main/resources/`.
- Rebuild or incrementally update the graph before trusting impact analysis on changed framework code.
