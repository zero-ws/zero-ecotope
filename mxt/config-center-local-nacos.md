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

## 5. AI Agent Rules

- When a task mentions config center behavior, inspect `ConfigMod`, `ZeroSource`, and `NacosRule` before touching business code.
- Treat environment variables such as `R2MO_NACOS_ADDR`, `R2MO_NS_CLOUD`, and `R2MO_NS_APP` as part of the framework contract.
- Do not solve a config-center issue by hardcoding defaults in exmodules or plugins.
