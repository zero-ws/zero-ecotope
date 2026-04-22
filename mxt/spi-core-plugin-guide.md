# SPI Core Plugin Guide

> Load this file when the task is about SPI-driven extension seams across the core plugin and extension layers, and the question is architectural rather than implementation-specific.

## 1. Scope

This file owns:

- SPI as the core extensibility model
- how plugins, extension skeleton, and exmodules meet at SPI boundaries
- when to switch from registry reading to implementation reading

It does not own one SPI family's implementation details.

## 2. Core Anchors

- `ExBoot`
- `SPI_SET`
- `HPI.findMany`
- `META-INF/services`
- `ConfigMod`
- `ExActivity`
- `ScPermit`
- `UiForm`

## 3. AI Agent Rules

- Read this file first when the user says “core plugin layer via SPI”.
- Switch to `spi-registry-map.md` for family classification.
- Switch to `spi-implementation-rules.md` for implementation and registration details.
