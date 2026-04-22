# Zero Epoch Runtime Guide

> Load this file when the task is about the Zero core container, runtime lifecycle, request execution substrate, or the internal structure of `zero-epoch`.

## 1. Scope

This file owns:

- `zero-epoch` as the core runtime layer
- runtime sub-module roles
- container and execution substrate boundaries

It does not own plugin capability or exmodule business logic.

## 2. Owning Modules

- `zero-epoch-cosmic`
- `zero-epoch-store`
- `zero-epoch-use`
- `zero-epoch-adhoc`
- `zero-epoch-execution`
- `zero-epoch-focus`
- `zero-epoch-setting`
- `zero-overlay`
- `zero-epoch-spec`
- `zero-epoch-spec-nacos`

## 3. AI Agent Rules

- Start here for container and execution questions before dropping to one sub-module.
- Use this file to classify whether the task belongs to runtime substrate, configuration, scheduling, storage, or shared platform contracts.
