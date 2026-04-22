# Exmodule Integration Guide

> Load this file when the task is about reusable integration directories, messaging integration, external connector abstractions, or `zero-exmodule-integration`.

## 1. Scope

This file owns `zero-exmodule-integration`.

## 2. Key Anchors

- `DirActor`
- `MessageActor`
- `DirStub`
- `MessageStub`
- `DirService`
- `MessageService`
- `ExtensionIntegrationSource`
- `MDIntegrationActor`

## 3. AI Agent Rules

- Use this module for reusable business-facing integration logic.
- Keep protocol adapters in plugins and cross-domain attachment semantics coordinated with ambient or attachment SPI.
