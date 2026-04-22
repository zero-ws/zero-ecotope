# Attachment Storage Integration Guide

> Load this file when the task is about attachment upload, storage abstraction, or FTP-style integration attached to ambient and integration modules.

## 1. Scope

This file owns the composite scenario:

- attachment upload
- storage metadata
- integration-driven remote storage such as FTP-style extension

## 2. Owning Modules

- `zero-exmodule-ambient`
- `zero-exmodule-integration`
- shared SPI such as `ExAttachment`

## 3. Key Anchors

- `AttachActor`
- `ExAttachment`
- integration directory and message services
- storage-related environment variables in `EnvironmentVariable`

## 4. AI Agent Rules

- Keep attachment metadata and ambient ownership in ambient.
- Keep external connector behavior in integration.
- Keep transport or storage protocol capability outside business modules unless the abstraction is business-owned.
