# Excel Import And Export Guide

> Load this file when the task is about `zero-plugins-excel`, workbook import/export flow, Excel DI registration, or Excel environment config.

## 1. Scope

This file owns:

- Excel plugin startup
- Excel client registration
- import/export entry anchors
- Excel environment config anchors

It does not own business spreadsheet semantics in one exmodule.

## 2. Owning Modules

- `zero-plugins-excel`

## 3. Key Anchors

- `ExcelActor`
- `ExcelClient`
- `ExcelClientImpl`
- `ExcelImport`
- `ExcelExport`
- `ExcelAnalyzer`
- `ExcelEnvConnect`

## 4. Runtime Model

`ExcelActor` starts the Excel plugin and registers the `ExcelClient` provider into DI.

The plugin exposes:

- client access
- workbook parsing
- import handling
- export handling
- environment-driven Excel template and tenant config

## 5. AI Agent Rules

- When Excel startup fails, inspect `ExcelActor` and DI registration first.
- When import/export shape is wrong, inspect `ExcelImport`, `ExcelExport`, and `ExcelAnalyzer`.
- Treat `excel:` config under `vertx.yml` as plugin environment input, not as app-local ad hoc settings.
