---
description: Dynamic modular operation rules for zero-exmodule-modulat bags, blocks, open/full configuration, cache, and B_BAG/B_BLOCK composition.
globs:
  - "zero-ecosystem/zero-plugins-extension/zero-exmodule-modulat/**/*.java"
  - "zero-ecosystem/zero-plugins-extension/zero-extension-skeleton/**/*.java"
  - "zero-ecosystem/zero-plugins-extension/zero-exmodule-modulat/**/resources/**/*"
alwaysApply: false
---

# Modulat Dynamic Operation Rules

Load this rule when a task touches dynamic modular configuration, `zero-exmodule-modulat`, `B_BAG`, `B_BLOCK`, bag/block APIs, `ExModulat`, modular app configuration, open/full config modes, `PowerApp`, `PowerMod`, or runtime module operation setup.

## Ownership

- `zero-exmodule-modulat` owns reusable modular configuration semantics and runtime composition from `B_BAG` and `B_BLOCK`.
- `zero-extension-skeleton` owns the SPI contract `ExModulat`; do not change this contract for one module's configuration shape.
- `zero-exmodule-ui` may consume or present modular configuration, but business configuration ownership remains in Modulat.
- `zero-exmodule-ambient` app loading may attach Modulat output to app metadata, but it should not build Modulat internals.

## Source Anchors

- SPI contract: `ExModulat`.
- Main implementation: `ExModulatCommon`.
- Configuration builders: `EquipFor`, `EquipForBase`, `EquipForData`, `EquipForOpen`.
- Composition chain: `Combiner`, `CombinerBlock`, `CombinerBag`, `CombinerDao`, `CombinerOutBag`, `CombinerOutChildren`, `CombinerKit`.
- Runtime cache and access: `PowerApp`, `PowerMod`, `OCacheMod`, `OCacheModAmbiguity`.
- Services: `BagArgService`, `BagService`, `BlockService`.
- APIs: `BagAgent`, `BagArgAgent`, `BagActor`, `BagArgActor`.
- Tables: `B_BAG`, `B_BLOCK`, `B_COMPONENT`, `B_WEB`, `B_AUTHORITY`.

## Core Model

- `B_BAG` represents a modular configuration package. Bags can form parent/child trees and can be grouped by `store` to produce output keys such as `mHotel`, `mSetting`, or another module-specific `mXxx` key.
- `B_BLOCK` represents concrete configuration blocks under a bag. `uiContent` holds values; `uiConfig.field` describes field metadata.
- `BBag.uiConfig` can include form layout/configuration and optional record lookup configuration.
- `BBag.uiOpen` controls which keys survive in open mode.
- `BBag.entry` marks entry-capable app bags for the `apps` output in full mode.

## Runtime Composition Flow

1. Consumers call `HPI.of(ExModulat.class)` or helper flows such as `PowerApp.getCreated(appId, open)`.
2. `ExModulatCommon.extension(appId, open)` selects `EquipForOpen` when `open=true`, otherwise `EquipForData`.
3. `EquipForBase.buildQr` builds the app/bag query and constrains bag types to foundation, commerce, and extension bags.
4. `EquipForBase.fetchBags` loads `BBag` records and resolves each bag's effective `store`, walking parent bags when a child has no store.
5. Bags are grouped by effective `store`; each group becomes one output module key.
6. `EquipForBase.dataAsync` loads blocks through `BagArgService.seekBlocks`, combines block content and metadata through `CombinerBlock`, and optionally filters keys for open mode.
7. `CombinerBlock` merges all `BBlock.uiContent`, merges field metadata from `BBlock.uiConfig.field` into `__metadata`, adds the bag key, and delegates record enrichment to `CombinerDao`.
8. `CombinerDao` reads optional `BBag.uiConfig.record` and resolves data through `Ke.umJData(record, { appId, sigma })`.
9. `EquipForData` attaches entry bags under `apps` and stores `id`; `EquipForOpen` stores only open output and the app key.
10. `PowerApp` converts composed JSON into cached `PowerMod` instances and exposes module blocks by name.

## Write and Refresh Flow

- `BagArgService.fetchBagConfig` returns bag UI configuration. Parent bags combine child bag UI segments through `CombinerBag`; child bags use `CombinerOutBag`.
- `BagArgService.fetchBag` returns merged block content and metadata for one bag.
- `BagArgService.saveBag` and `saveBagBy` load the bag, find its blocks, call `BlockService.saveParameters`, and refresh `PowerApp` cache.
- `BlockService.saveParameters` writes only fields declared in each block's `uiConfig.field` into that block's `uiContent`, updates audit fields, and persists blocks.
- `PowerApp.getRefresh` clears `PowerApp` and both `EquipForData` / `EquipForOpen` caches before rebuilding full mode.

## Agent Rules

- Do not build modular runtime JSON directly in Ambient or UI modules. Use `ExModulat` or `PowerApp`.
- Do not change `ExModulat` response shape casually. Existing consumers expect module keys plus optional `apps`, `id`, `key`, bag data, and `__metadata`.
- Do not bypass `BlockService.saveParameters` for dynamic parameter updates. It protects block field ownership by writing only fields declared in `uiConfig.field`.
- Do not forget cache invalidation after writes. Use `PowerApp.getRefresh(appId)` or `ExModulat.invalidate(appId)` paths.
- Do not duplicate module key names across unrelated bags. `ExModulat.extension(JsonObject, open)` merges module data into app JSON, and duplicate module keys can override earlier data.
- Do not treat open mode as full configuration. `open=true` filters output to `uiOpen`, `__metadata`, and parent key information.
- Do not attach UI-only behavior to `B_BLOCK.uiContent`; keep UI metadata in `uiConfig` and values in `uiContent`.
- When adding a new dynamic operation, decide first whether it is bag-level, block-level, runtime-cache-level, or UI-presentation-level.

## MCP Client Routing Rule

External MCP clients often describe this feature as "dynamic module", "modular operation", "runtime app config", "bag/block config", "mXxx config", "open config", or "module operation setup". Route those terms to this file first, then inspect `ExModulat`, `EquipForBase`, `BagArgService`, and `PowerApp`.

## Search Hints

- Runtime config: `ExModulatCommon`, `EquipForData`, `EquipForOpen`, `EquipForBase`.
- Bag/block composition: `CombinerBlock`, `CombinerBag`, `CombinerDao`.
- Dynamic writes: `BagArgService.saveConfigure`, `BlockService.saveParameters`.
- Runtime access/cache: `PowerApp`, `PowerMod`, `OCacheMod`.
