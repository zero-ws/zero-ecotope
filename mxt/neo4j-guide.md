# Neo4j Capability Guide

> Load this file when the task is about graph-database capability, Neo4j client ownership, or Neo4j plugin usage in Zero.

## 1. Scope

This file owns:

- Neo4j plugin ownership
- Neo4j client anchors
- capability boundary for graph-database integration

## 2. Owning Modules

- `zero-plugins-neo4j`

## 3. Key Anchors

- `Neo4jClient`
- `Neo4jClientImpl`

## 4. Capability Model

Neo4j support belongs to the capability plugin layer.
It provides framework-level client integration for graph-database access and should be consumed through extension or application layers rather than reimplemented there.

## 5. AI Agent Rules

- Keep graph-database capability in the plugin layer.
- Put domain graph semantics in exmodules or apps, not in the Neo4j plugin.
