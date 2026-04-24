# Cache And Redis Guide

> Load this file when the task is about framework cache topology, abstract cache ownership, Redis startup, or the boundary between capability cache and business cache policy.

## 1. Scope

This file owns:

- cache capability ownership
- Redis capability ownership
- cache provider split
- cache vs business rule boundary

It does not own domain cache-key design.

## 2. Owning Modules

- `zero-plugins-cache`
- `zero-plugins-cache-caffeine`
- `zero-plugins-cache-ehcache`
- `zero-plugins-redis`

## 3. Key Anchors

- `RedisActor`
- `RedisAddOn`
- `RedisProvider`
- cache plugin module roots

## 4. Sub-Module Responsibilities

| Module | Responsibility | When AI Agent Should Inspect |
|---|---|---|
| `zero-plugins-cache` | Abstract cache capability: SPI-driven cache provider interface, shared cache abstraction | Cache SPI registration failures, provider not found, cache abstraction issues |
| `zero-plugins-cache-caffeine` | Caffeine in-process cache backend: high-performance local cache with size-based eviction | Local cache eviction, TTL, or hit-ratio issues; when no distributed cache is needed |
| `zero-plugins-cache-ehcache` | Ehcache backend: disk-persistent or distributed cache via Terracotta | Cache persistence failures, Terracotta clustering issues, disk overflow behavior |
| `zero-plugins-redis` | Redis distributed cache and data-store capability: `RedisActor` startup, connection pool, `RedisAddOn` registration | Redis connection failures, distributed cache misses, pub/sub issues, standalone Redis outside cache SPI |

Selection rule:

- For pure local caching → `caffeine` (preferred) or `ehcache`.
- For distributed caching → `redis`.
- `zero-plugins-cache` is the SPI contract; backends implement it except `redis` which also provides its own `RedisActor`.

## 5. Capability Model

The cache family is split into:

- abstract cache capability
- concrete cache backend plugins
- Redis capability as a standalone add-on and startup actor

This layer owns connection management, backend registration, and shared cache capability exposure.

## 6. Source and Resource Path

Read in this order:

```text
cache-redis-guide.md
-> plugin-layer-map.md
-> zero-plugins-cache source
-> concrete backend source such as caffeine/ehcache/redis
```

High-value proof targets:

- `RedisActor`
- `RedisAddOn`
- `RedisProvider`
- cache SPI/provider classes
- backend configuration resources

## 7. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for cache/backend capability ownership
- `zero-ecotope` + `r2mo-rapid` when cache or Redis behavior must be compared with shared Spring-side provider selection

## 8. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one cache or Redis symbol is already known
- the unresolved point is structural spread between abstract cache SPI, backend registration, and Redis standalone startup

## 9. AI Agent Rules

- Put provider behavior here only when it is backend capability.
- Keep domain cache semantics in exmodules or apps.
- When Redis startup or registration is wrong, inspect `RedisActor` before changing business consumers.
