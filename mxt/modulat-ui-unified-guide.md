# Modulat UI Unified Guide

> Load this file when the task is about modular configuration service together with unified UI handling across modulat and UI modules.

## 1. Scope

This file owns the composite scenario:

- modular configuration service
- unified UI handling for modular data
- cross-module coordination between modulat and UI

## 2. Owning Modules

- `zero-exmodule-modulat`
- `zero-exmodule-ui`

## 3. Key Anchors

- `BagActor`
- `BagArgActor`
- `UiActor`
- `FormActor`
- `UiValve`

## 4. AI Agent Rules

- Keep configuration ownership in modulat.
- Keep backend-owned UI configuration exposure in UI exmodule.
- Use this file when the problem spans both modules rather than belonging to only one of them.
