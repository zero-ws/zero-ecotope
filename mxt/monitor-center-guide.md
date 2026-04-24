# Monitor Center Guide

> Load this file when the task is about monitoring bootstrap, monitor actors, or monitor plugin grouping under `zero-plugins-monitor*`.

## 1. Scope

This file owns:

- monitor capability ownership
- monitor startup anchor
- monitor plugin grouping

It does not own application metrics semantics.

## 2. Owning Modules

- `zero-plugins-monitor`
- `zero-plugins-monitor-prometheus`
- `zero-plugins-monitor-hawtio`

## 3. Key Anchors

- `MonitorActor`
- `YmMonitor`

## 4. Sub-Module Responsibilities

| Module | Responsibility | When AI Agent Should Inspect |
|---|---|---|
| `zero-plugins-monitor` | Base monitor plugin: `MonitorActor` startup, shared metrics infrastructure, configuration entry | Monitor boot failure, metrics not appearing, plugin inclusion issues |
| `zero-plugins-monitor-prometheus` | Prometheus endpoint exposure and scrape-compatible metrics export | Prometheus scrape errors, metrics format issues, endpoint missing |
| `zero-plugins-monitor-hawtio` | Hawtio web console integration for JVM and Vert.x runtime inspection | Hawtio console unreachable, JMX/Vert.x bean visibility issues |

Selection rule:

- `zero-plugins-monitor` is always required; `prometheus` and `hawtio` are independent add-ons.
- Choose `prometheus` for external metrics collection; choose `hawtio` for interactive runtime diagnostics.

## 5. Capability Model

The monitor family provides framework-level observability capability and optional backend-specific integrations such as Prometheus and Hawtio.

The core monitor plugin owns startup and configuration entry, while integrations extend the exposure surface.

## 6. Source and Resource Path

Read in this order:

```text
monitor-center-guide.md
-> plugin-layer-map.md
-> zero-plugins-monitor source
-> prometheus or hawtio add-on source if integration-specific
```

High-value proof targets:

- `MonitorActor`
- `YmMonitor`
- Prometheus endpoint classes/resources
- Hawtio integration classes/resources

## 7. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for monitor capability ownership
- `zero-ecotope` + `r2mo-rapid` only when observability or export behavior must be compared across runtime lines

## 8. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `MonitorActor` or one monitor integration symbol is already known
- the unresolved point is structural spread between base monitor startup and add-on exposure modules

## 9. AI Agent Rules

- Treat monitor plugins as infrastructure capability.
- Do not move app-specific reporting rules into monitor plugins.
- When monitoring does not boot, inspect `MonitorActor` and plugin module inclusion first.
