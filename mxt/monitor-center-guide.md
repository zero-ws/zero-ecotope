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

## 4. Capability Model

The monitor family provides framework-level observability capability and optional backend-specific integrations such as Prometheus and Hawtio.

The core monitor plugin owns startup and configuration entry, while integrations extend the exposure surface.

## 5. AI Agent Rules

- Treat monitor plugins as infrastructure capability.
- Do not move app-specific reporting rules into monitor plugins.
- When monitoring does not boot, inspect `MonitorActor` and plugin module inclusion first.
