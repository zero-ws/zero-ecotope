# Local And Nacos Config Center

> Load this file when the task is about local configuration, remote Nacos imports, config DSL parsing, or the boundary between `vertx.yml` and cloud-loaded config.

## 1. Scope

This file owns:

- local vs remote config loading
- `ConfigMod` as the module config contract
- Nacos import rule parsing
- config merge entry anchors

It does not own business module settings.

## 2. Owning Modules

- `zero-epoch-spec`
- `zero-epoch-setting`
- `zero-epoch-spec-nacos`
- `zero-boot-cloud-actor`
- `zero-overlay` environment constants

## 3. Key Anchors

- `io.zerows.epoch.configuration.ConfigMod`
- `io.zerows.epoch.boot.ZeroPower`
- `io.zerows.epoch.boot.ZeroSource`
- `io.zerows.epoch.boot.ConfigLoadCloud`
- `io.zerows.epoch.configuration.NacosRule`
- `io.zerows.platform.EnvironmentVariable`

## 4. Runtime Model

The configuration center in Zero is dual-path:

- local mode reads `vertx.yml` and module resources through `ConfigMod`
- cloud mode extends local config with remote imports such as Nacos-backed rules

Nacos imports are not raw strings. They are parsed through a dedicated DSL parser that captures:

- protocol
- optional flag
- data ID
- query parameters such as refresh switches and groups

## 5. Source and Resource Path

Read in this order:

```text
config-center-local-nacos.md
-> zero-epoch-spec for config interfaces and base loaders
-> zero-epoch-spec-nacos for provider and Nacos parsing
-> zero-epoch-setting for local/cloud load orchestration
-> zero-overlay only when environment-variable exposure is the unresolved point
```

High-value proof targets:

- `ConfigMod`
- `ConfigLoadBase`
- `ConfigProvider`
- `NacosRule`
- `ConfigLoadCloud`
- `ZeroSource`
- `ZeroPower`
- `META-INF/services/io.zerows.epoch.configuration.ConfigMod`
- `META-INF/services/io.zerows.epoch.configuration.ConfigProvider`
- `vertx.yml` / `vertx-boot.yml` specs

## 6. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for config loading and Nacos import behavior
- `zero-ecotope` + `r2mo-rapid` only when the unresolved point is how shared runtime or Spring-side consumption interprets the loaded config
- `zero-ecotope` + `rachel-momo` only when the issue is actually dependency/BOM governance for Nacos-related libraries

External repository dependency is optional, not the default.

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one config-loading symbol is already known,
- the unresolved point is structural spread between spec, provider registration, and setting-side boot orchestration,
- final proof still comes from source and resource files.

## 8. AI Agent Rules

- When a task mentions config center behavior, inspect `ConfigMod`, `ZeroSource`, and `NacosRule` before touching business code.
- Treat environment variables such as `R2MO_NACOS_ADDR`, `R2MO_NS_CLOUD`, and `R2MO_NS_APP` as part of the framework contract.
- Do not solve a config-center issue by hardcoding defaults in exmodules or plugins.
