---
description: Upload/download and configurable storage rules for Ambient, Integration, and attachment flows.
globs:
  - "zero-ecosystem/zero-plugins-extension/zero-exmodule-ambient/**/*.java"
  - "zero-ecosystem/zero-plugins-extension/zero-exmodule-integration/**/*.java"
  - "zero-ecosystem/zero-plugins-extension/zero-extension-skeleton/**/*.java"
alwaysApply: false
---

# Attachment Upload, Download, and Configurable Storage Rules

Load this rule when a task touches file upload, file download, attachment metadata, document storage, upload sessions, configurable storage directories, `ExAttachment`, `ExIo`, `AtFs`, or Integration `Fs` adapters.

## Ownership

- `zero-exmodule-ambient` owns attachment business metadata, upload/download API endpoints, upload-session orchestration, and the `X_ATTACHMENT` persistence flow.
- `zero-exmodule-integration` owns configurable document directories, `I_DIRECTORY`, storage component selection, and the `ExIo` / `Fs` bridge to actual storage.
- `zero-extension-skeleton` owns stable contracts such as `ExAttachment` and `ExIo`; do not change these contracts for one feature unless the whole framework contract is intentionally upgraded.
- R2MO `r2mo-io` / `r2mo-io-local` owns generic IO abstractions such as `HFS`, `HStore`, `RFS`, `HTransfer`, transfer tokens, chunked upload, and local storage implementation details.

## Source Anchors

- Upload/download API: `AttachAgent`, `AttachActor`.
- Attachment SPI: `ExAttachment`, `ExAttachmentNorm`.
- Upload-session flow: `UploadSessionService`, `HmmTokenPool`, R2MO `RFS`, `HTransfer`, `TransferToken`, `StoreChunk`.
- Ambient storage facade: `At`, `AtFs`, `AtConfig`.
- Document service flow: `DocReader`, `DocWriter`, `AppService`.
- Integration storage SPI: `ExIo`, `ExIoPath`, `Fs`, `FsDefault`, `IsFs`.
- Directory model: `I_DIRECTORY`, `IDirectory`, `IDirectoryDao`.
- R2MO storage implementation: `HFS`, `HStore`, `HStoreLocal`, `AbstractHStore` in `../r2mo-rapid`.

## End-to-End Flow

### Small or direct upload

1. `AttachAgent.upload` accepts Vert.x `FileUpload`, creates attachment JSON, sets `filePath` to the temporary uploaded file, sets `storeWay` from `AtConfig.fileStorage`, and sets `directory` from the request.
2. `AttachActor.upload` normalizes metadata, adds `sigma`, and keeps the record active.
3. `ExAttachmentNorm.uploadAsync` calls `At.fileDir`, persists `X_ATTACHMENT`, calls `At.fileUpload`, and returns normalized attachment output.
4. `AtFs.fileDir` resolves dynamic `directory`, verifies or initializes `I_DIRECTORY`, sets `directoryId`, computes final `storePath`, and copies the directory storage type into `storeWay`.
5. `AtFs.fileUpload` splits local vs remote/configured storage by `directoryId`. Local records remain local. Remote/configured records call `ExIo.fsUpload` and then remove the temporary upload file through `HFS.of().rm`.
6. `ExIoPath.fsUpload` resolves an `Fs` implementation from the directory and delegates to `Fs.upload`.
7. `FsDefault.upload` moves the temporary file from `filePath` into `storeRoot + storePath` with `HFS.of().mv`.

### Chunked upload session

1. `AttachAgent.initSession`, `uploadChunk`, `sessionStatus`, `completeSession`, and `cancelSession` expose the upload-session API.
2. `UploadSessionService.initSession` creates a session directory under `AtConfig.storePath` or `java.io.tmpdir`, builds a R2MO `TransferRequest`, initializes `RFS`, persists session context in both cache and `TransferToken.configuration.ambientUploadContext`.
3. `UploadSessionService.uploadChunk` writes each chunk through `RFS.ioUploadChunk`.
4. `UploadSessionService.completeSession` calls `RFS.completeUpload`, builds a completed attachment record, and calls `ExAttachmentNorm.uploadCompletedAsync`.
5. `uploadCompletedAsync` persists metadata only; it must not call `At.fileUpload` again because the completed file is already assembled at `finalPath`.

### Download

1. `AttachAgent.download` receives `fileKey` and optional HTTP `Range`.
2. `AttachActor.download` converts the header to `FileRange`, ensures file size when possible, and delegates to `DocReader`.
3. `DocReader.downloadDoc` delegates to `ExAttachment.downloadAsync`.
4. `ExAttachmentNorm.downloadAsync` loads `X_ATTACHMENT` by `key` or `fileKey`, then calls `At.fileDownload`.
5. `AtFs.fileDownload` first returns `filePath` directly when it still exists; otherwise it requires `directoryId` and delegates to `ExIo.fsDownload` using `storePath`.
6. `ExIoPath.fsDownload` resolves the `Fs` implementation for `directoryId`; `FsDefault.download` reads from `storeRoot + storePath`, including range reads through `HStore.inBinary(path, range, null)`.

## Configurable Storage Rules

- Treat `AtConfig.storePath` as the Ambient root used by app metadata and upload-session temporary storage.
- Treat Integration `IsConfig.storeRoot` as the root for configured directory storage in `FsDefault`.
- Treat `directory` as the business-level destination expression. `AtFs.fileDir` resolves it through `Ut.fromExpression(directory, params)` and then validates it through `ExIo.verifyIn`.
- Treat `directoryId` as the switch from raw local temp-file behavior to Integration-managed storage behavior.
- Treat `storePath` as storage-relative path below the configured root. Do not store absolute paths in `storePath` unless the existing model explicitly does so.
- Treat `filePath` as the temporary or currently accessible physical path. It may point to Vert.x upload temp files or completed session files.
- Treat `storeWay` as metadata copied from `AtConfig.fileStorage` initially and later corrected from the verified `I_DIRECTORY.type` when a directory is resolved.

## Agent Rules

- Do not put storage protocol decisions in `AttachAgent`, `AttachActor`, or business services. Route protocol decisions through `ExIo` and `Fs`.
- Do not bypass `ExAttachmentNorm` when changing attachment metadata persistence. It coordinates DB rows, directory resolution, storage upload/download, and output enrichment.
- Do not bypass `AtFs.fileDir` for directory-backed attachments. It is the place that resolves expressions, verifies directories, assigns `directoryId`, computes `storePath`, and chooses `storeWay`.
- Do not call `At.fileUpload` for completed chunked uploads unless the file still needs to be moved from a temporary path to configured storage. Existing `uploadCompletedAsync` intentionally persists metadata only.
- Do not delete temporary upload files until the configured storage upload succeeds. Current `AtFs.fileUpload` removes temp files only after `ExIo.fsUpload` completes.
- Preserve Range download support by carrying `FileRange` from the HTTP header through `AttachActor`, `DocReader`, `ExAttachment`, `AtFs`, `ExIo`, and `Fs`.
- When adding a new storage adapter, implement `Fs`, configure `IDirectory.runComponent`, and keep `ExIoPath` as the adapter dispatcher.
- When changing bulk download, preserve the map shape `filePath -> storePath` before it reaches `ExIo.fsDownload`.
- When changing trash, restore, purge, or rename behavior, update both attachment metadata and directory-backed storage operations. Use `DocWriter` and `ExIo` mix methods as the source of truth.
- When implementing tests, cover direct upload metadata, directory-backed upload, range download, session recovery from `TransferToken.configuration`, and cleanup on cancel.

## Anti-Patterns

- Do not write direct `Files.move`, `Files.delete`, or `Paths.get(root, path)` logic in Ambient business services when an `ExIo` or `Fs` route exists.
- Do not add FTP/SFTP/HDFS logic into `zero-exmodule-ambient`; add an Integration `Fs` implementation or lower-level R2MO storage provider.
- Do not mutate `fileKey`, `storePath`, `directoryId`, or metadata shape in update-only attachment paths unless migration and download compatibility are handled.
- Do not assume local files remain available after configured upload; temp files are removed after successful remote/configured upload.
- Do not return empty `Buffer` silently for new failure modes without matching existing behavior and tests.

## Search Hints

- Search upload path: `AttachAgent`, `AttachActor`, `ExAttachmentNorm.uploadAsync`, `AtFs.fileUpload`.
- Search chunked upload: `UploadSessionService`, `RFS`, `TransferToken`, `StoreChunk`.
- Search download path: `AttachActor.download`, `DocReader.downloadDoc`, `ExAttachmentNorm.downloadAsync`, `AtFs.fileDownload`, `FsDefault.download`.
- Search configurable storage: `ExIoPath`, `Fs`, `FsDefault`, `IsFs.component`, `IDirectory.runComponent`, `storeRoot`, `storePath`.
