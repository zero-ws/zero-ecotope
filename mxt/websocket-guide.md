# WebSocket Capability Guide

> Load this file when the task is about active push, WebSocket capability ownership, or framework-level socket integration.

## 1. Scope

This file owns:

- WebSocket plugin ownership
- active-push capability placement
- plugin-layer boundary for socket integration

## 2. Owning Modules

- `zero-plugins-websocket`

## 3. Capability Model

WebSocket support belongs to the capability plugin layer.
It is the framework owner for reusable active-push transport capability.

Business subscription rules or domain event semantics belong above this layer.

## 4. AI Agent Rules

- Keep transport integration in the plugin.
- Keep domain push policy in exmodules or apps.
- Start from the plugin module tree when the task says “active push” or “WebSocket”.
