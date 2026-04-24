---
description: MCP client-facing routing rules for external projects connecting to Zero/R2MO framework knowledge packs.
globs:
  - "mxt/**/*"
alwaysApply: false
---

# MCP Client Connection Rules

Load this rule when the current repository is being consumed as an MCP knowledge endpoint by another project, or when maintaining MCP routing files for `mxt-zero` / `mxt-r2mo`.

## Meaning of External MCP Connection

An external project connecting through MCP is not asking to modify this framework by default. It is asking the framework knowledge pack to answer ownership, routing, contract, integration, and implementation-boundary questions with the shortest reliable path.

## Routing Contract

- Route from business wording to framework owner documents first.
- Prefer `.md` rules for actionable AI constraints and `.md` guides for broad orientation.
- Use `mcp-integration-map.md` for Zero topic routing.
- Use `mcp-code-review-graph-rules.md` for Zero graph playbooks.
- Use `../r2mo-rapid/mxt/mcp-trigger-matrix.md`, `mcp-shortest-path.md`, and route files for R2MO topic routing.
- Use source files only after the smallest owner/rule document is selected.

## Cross-Pack Handshake

- If the topic mentions Zero exmodules, route to `mxt-zero` first.
- If the topic mentions R2MO core abstractions such as `HFS`, `HStore`, `RFS`, `HTransfer`, DBE, JCE, JAAS, or Spring runtime, route to `mxt-r2mo` first.
- If a Zero rule references R2MO internals, read the Zero rule first, then the matching R2MO rule.
- If a R2MO rule references downstream Zero integration, read the R2MO rule first, then the Zero owner rule.

## High-Value MCP Topics

| External wording | First Zero rule | Then |
|---|---|---|
| upload, download, attachment, storage, FTP/SFTP storage | `attachment-storage-configurable-storage.md` | `io-utility-hfs-hstore-rules.md`, `../r2mo-rapid/mxt/hfs-hstore-usage.md` |
| audit log, activity log, change log, activity rule, EXPR rule | `ambient-activity-expression-rules.md` | `exmodule-ambient-guide.md` |
| dynamic module, modular operation, bag/block config, runtime app config | `modulat-dynamic-operation-rules.md` | `exmodule-modulat-guide.md` |
| `Ut.ioXxx`, `HFS`, `HStore`, `RFS`, range download | `io-utility-hfs-hstore-rules.md` | `../r2mo-rapid/mxt/hfs-hstore-usage.md` |

## Script Boundary

`R2MO_HOME/bin/mo-mcp` should only change when the MCP server cannot expose or discover the needed knowledge roots. Do not modify the script just to add a new topic if existing `mxt-zero` and `mxt-r2mo` filesystem/wrapper mounts already expose the `mxt/` directories.
