---
description: Rules for choosing Ut.ioXxx, HFS, HStore, RFS, and Integration Fs in Zero/R2MO IO work.
globs:
  - "zero-ecosystem/**/*.java"
  - "../r2mo-rapid/r2mo-io/**/*.java"
  - "../r2mo-rapid/r2mo-io-local/**/*.java"
  - "../r2mo-rapid/r2mo-ams/**/*.java"
alwaysApply: false
---

# IO Utility, HFS, and HStore Rules

Load this rule when a task touches `Ut.ioXxx`, `HFS`, `HStore`, `RFS`, `HTransfer`, file path utilities, classpath resource loading, local file operations, range reads, or storage-provider selection.

## Decision Matrix

| Need | Use | Do Not Use |
|---|---|---|
| Simple Zero utility path join, path expansion, existence check, or Buffer conversion | `Ut.ioPath`, `Ut.ioPathSet`, `Ut.ioPathRoot`, `Ut.ioExist`, `Ut.ioBuffer`, `Ut.ioStream` | raw string concatenation, ad-hoc `new File(...)` checks |
| Framework-level local file operation through configured R2MO storage | `HFS.of()` | direct `Files.*` in business modules |
| New storage implementation or provider-level behavior | `HStore` implementation | Ambient `Attach*` classes |
| Directory-backed configurable storage operation | `ExIo` / `Fs` | direct `HFS` from Ambient business logic |
| Chunked/resumable transfer | `RFS` / `HTransfer` | one-off upload-session state machines |
| Read multiple files as one download payload | `HStore.inBinary(Set<String>, ...)` or existing `Ut.toZip` route | manual zip assembly in endpoint code |
| Range download | `FileRange` plus `HStore.inBinary(path, range, null)` | slicing a full `Buffer` in memory |

## Layering Rules

- `Ut.ioXxx` is a Zero upper utility facade. Use it for small path, stream, existence, and buffer conversion operations.
- `HFS` is the R2MO high-level file-system facade. It delegates to the current `HStore` from `SPI.SPI_IO.ioAction()`.
- `HStore` is the storage-provider contract. Implement or modify it only in provider modules such as `r2mo-io-local` or another storage backend.
- `RFS` is the transfer/session facade for upload chunks, resume, progress, completion, and cancellation.
- `ExIo` is the Zero extension contract that connects Ambient document/attachment logic to Integration storage directories.
- `Fs` is the Integration component abstraction selected by `IDirectory.runComponent`; it is the right place for FTP/SFTP/HDFS/cloud-style storage adapters.

## HFS/HStore Source Facts

- `HFS.of()` is thread-cached and wraps `SPI.SPI_IO.ioAction()`.
- `HFS` exposes `cp`, `mv`, `mkdir`, `rm`, `ls`, `write`, `inString`, `inBytes`, `inStream`, YAML/JSON reads, key reads, and `fileSize` by delegating to `HStore`.
- `HStore.pHome(path)` resolves paths through `HUri.UT.resolve(this.pHome(), path)`.
- `HStore.inStream(String)` converts a filename to URL and returns `null` for missing files or IO failures in the default implementation.
- `HStore` includes `inBinary(String, FileRange, HProgressor)` for range reads and `inBinary(Set<String>, Set<FileMem>, HProgressor)` for multi-file binary/zip-like reads.
- `HStoreLocal` is the local provider registered with `@SPID(HStore.DEFAULT_ID)` and delegates to local helpers for copy, move, remove, mkdir, read, write, range read, and key IO.
- `FsDefault` uses `HFS` for local configured storage operations and `SPI.V_STORE` for range reads.

## Ut.ioXxx Rules

- Use `Ut.ioPath(...)` instead of manual slash concatenation.
- Use `Ut.ioPathSet(path)` when the framework needs gradient directory paths for tree verification.
- Use `Ut.ioPathRoot(path)` when extracting the root segment for directory initialization.
- Use `Ut.ioExist(path)` before reading optional local files.
- Use `Ut.ioBuffer(path)` only when reading a whole local file is expected and acceptable.
- Use `Ut.ioStream(...)` for classpath/resource stream loading in plugin-style resource readers.

## HFS Usage Rules

- Use `HFS.of().mv(from, to)` for configured local storage moves, as in `FsDefault.upload`.
- Use `HFS.of().rm(pathOrSet)` for temp-file cleanup and configured local deletion.
- Use `HFS.of().mkdir(pathOrSet)` for idempotent directory creation.
- Use `HFS.of().inContent(path)` when the code intentionally supports storage first and classpath fallback.
- Do not use `HFS` directly from endpoint classes. Endpoints should produce request data and route through actor/service/SPI layers.
- Do not assume every `HStore` supports `File` or `Path` overloads. The `HStore` contract marks those methods as unsuitable for network or distributed implementations unless overridden.

## HStore Implementation Rules

- Keep provider-specific path resolution inside the `HStore` implementation.
- Implement `toURL(String)` carefully because most read methods are built on `toURL -> inStream`.
- Preserve missing-file behavior unless deliberately changing the contract: default `inStream(URL)` returns `null` for missing or failed streams.
- Implement `inBinary(String, FileRange, HProgressor)` for efficient Range download support.
- Implement bulk `rm`, `mkdir`, and `mv` as idempotent operations when possible.
- Keep security key read/write methods (`inPrivate`, `inPublic`, `inSecret`, `write(...Key)`) in provider code, not in business modules.

## Integration Fs Adapter Rules

- Put storage-protocol adapters in `zero-exmodule-integration` by implementing `Fs` and configuring `IDirectory.runComponent`.
- `Fs.upload` receives a map of temporary physical path to storage-relative `storePath`.
- `Fs.download` receives storage-relative `storePath`; combine it with adapter config such as `storeRoot` inside the adapter.
- `Fs.rm` and `Fs.rename` should operate on storage-relative paths and apply the adapter root internally.
- `IsFs.component(directoryId)` is the dispatcher from directory metadata to `Fs`; do not duplicate that lookup elsewhere.

## Cross-Repository Rule

When working in Zero Ecotope and the task touches `HFS`, `HStore`, `RFS`, `HTransfer`, or chunked upload internals, inspect `../r2mo-rapid` source before changing Zero code. Zero uses these as framework dependencies, and behavior changes often belong in R2MO rather than in Ambient or Integration.

## Anti-Patterns

- Avoid direct `java.nio.file.Files` operations in Ambient or Integration business flows unless the existing code is explicitly managing upload-session temporary files.
- Avoid full-buffer reads for large files when a `FileRange` path exists.
- Avoid absolute path leakage into `storePath` for Integration-managed storage.
- Avoid adding storage-driver conditionals such as `if ftp`, `if sftp`, or `if local` outside `Fs` or `HStore` provider code.
- Avoid changing `HStore` defaults casually; they affect R2MO, Zero, JCE license IO, boot loading, and plugin resource loading.
