# Job Model Guide

> Load this file when the task is about `@Job`, scheduled missions, runtime job extraction, or the MBSE job-facing API surface.

## 1. Scope

This file owns:

- framework job model
- annotation-driven mission extraction
- runtime scheduling metadata
- the relationship between epoch job runtime and MBSE job APIs

It does not own generic startup order. Use `actor-startup-matrix.md` for `@Actor`.

## 2. Owning Modules

- `zero-epoch-focus`
- `zero-epoch-use`
- `zero-exmodule-mbseapi`

## 3. Key Anchors

- `io.zerows.epoch.annotations.Job`
- `io.zerows.cosmic.plugins.job.JobExtractor`
- `JobConfig`
- `JobStoreUnity`
- `Mission`
- `TaskApi`
- `JobStub`
- `JobService`

## 4. Runtime Model

Zero job runtime starts from `@Job` and produces a `Mission`.

Core phases:

- load optional configuration
- build `Mission`
- set basic identity and status
- apply threshold
- apply timer and formula
- bind the implementation class and required `@On` method

The MBSE API layer then exposes job-facing service and task endpoints on top of that runtime model.

## 5. AI Agent Rules

- When a task mentions scheduling or mission lifecycle, inspect `JobExtractor` before changing API code.
- Treat `Mission` as runtime truth and MBSE job APIs as consumer-side management surfaces.
- Do not confuse `@Job` tasks with `@Actor` boot modules.
