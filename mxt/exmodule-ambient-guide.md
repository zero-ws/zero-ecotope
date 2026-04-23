# Exmodule Ambient Guide

> Load this file when the task is about ambient configuration, app registry, attachment resources, activity logging, or shared system-side metadata in `zero-exmodule-ambient`.

## 1. Scope

This file owns `zero-exmodule-ambient` as a reusable business module.

## 2. Owning Modules

- `zero-exmodule-ambient-api`
- `zero-exmodule-ambient-domain`
- `zero-exmodule-ambient-provider`
- optional UI resources above this layer

## 3. Key Anchors

- `AppActor`
- `AttachActor`
- `AclActor`
- `ActivityService`
- `AppService`
- `ExtensionAmbientSource`
- `MDAmbientActor`

## 4. AI Agent Rules

- Put reusable system-side domain behavior here, not in plugins.
- Use this module first for attachment metadata, activity logs, app registry, and ambient initialization concerns.
