# Elasticsearch Guide

> Load this file when the task is about `zero-plugins-elasticsearch`, Elasticsearch client setup, indexing, or search-engine capability.

## 1. Scope

This file owns `zero-plugins-elasticsearch`.

It owns:

- Elasticsearch plugin capability
- client and provider wiring
- indexer and query helper anchors
- plugin-level search-engine integration

It does not own:

- domain search semantics
- DBE query rules
- business-level indexing policy

## 2. Owning Module

- `zero-plugins-equip/zero-plugins-elasticsearch`

Verified graph/source anchors:

- `ElasticSearchActor`
- `ElasticSearchClient`
- `ElasticSearchClientImpl`
- `ElasticSearchProvider`
- `ElasticIndexer`
- `ElasticQr`
- `AbstractEsClient`
- `up/rules/elasticsearch.yml`

## 3. Responsibility Model

Elasticsearch is a plugin capability.

It should own:

- connecting to Elasticsearch
- exposing framework search/index helpers
- managing plugin-level indexer behavior

It should not own:

- deciding which business fields are searchable
- replacing DBE query semantics
- embedding business-specific indexing rules in plugin code

## 4. AI Agent Rules

- Start from `ElasticSearchActor` for boot behavior.
- Use `ElasticSearchClient` and `ElasticIndexer` for capability behavior.
- If the task is about domain search meaning, route to the consuming exmodule or app metadata first.
