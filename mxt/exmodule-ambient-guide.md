# Exmodule Ambient Guide

> Load this file when the task is about ambient configuration, app registry, attachment metadata, document/directory handling, activity history, subscription, or shared system-side metadata in `zero-exmodule-ambient`.

## 1. Scope

`zero-exmodule-ambient` is the platform-business exmodule for shared ambient/system records.

It owns:

- app/menu registry surfaces
- attachment/document metadata and upload-session coordination
- activity history and rule-driven activity semantics
- tenant/model/datum/tag/link/shared ambient records
- system-side initialization surfaces that are broader than one app

It does not own:

- low-level storage provider mechanics
- generic transfer capability
- generic security/auth protocol behavior

## 2. Verified Module Shape

Modules:

- `zero-exmodule-ambient-api`
- `zero-exmodule-ambient-domain`
- `zero-exmodule-ambient-provider`
- `zero-exmodule-ambient-ui`

Verified anchors:

- API:
  - `AppActor`
  - `AttachActor`
  - `AclActor`
  - `EntryActor`
  - `DocActor`
  - `HistoryActor`
  - `FileActor`
  - `TagActor`
  - `SubscriptionActor`
- Domain/servicespec:
  - `ActivityStub`
  - `AppStub`
  - `DocBStub`
  - `DocRStub`
  - `DocWStub`
  - `InitStub`
  - `LinkStub`
  - `UploadStub`
- Provider:
  - `MDAmbientActor`
  - `ExtensionAmbientSource`
  - `ActivityService`
  - `AppService`
  - `MenuService`
  - `UploadSessionService`
  - `DocBuilder`
  - `DocReader`
  - `DocWriter`
  - `SubscriptionService`
  - `ExAttachmentNorm`

## 3. Ownership Model

### App/menu/platform registry

Use Ambient when the issue is about shared app/menu or entry-level system data.

Verified anchors:

- `AppActor`
- `EntryActor`
- `AclActor`
- `MenuService`

### Attachment/document metadata

Use Ambient when the issue is about:

- attachment metadata normalization
- document + directory modeling
- upload session lifecycle
- generated download URL and attachment document state

Verified anchors:

- `AttachActor`
- `UploadSessionService`
- `DocBuilder`
- `DocReader`
- `DocWriter`
- `ExAttachmentNorm`

Boundary:

- storage provider selection belongs in attachment/io rules
- attachment business metadata and document-directory semantics belong here

### Activity history and expression rules

Use Ambient when the issue is about:

- activity log
- change history
- `X_ACTIVITY_RULE`
- expression-backed rule evaluation

Verified anchors:

- `ActivityService`
- ambient flyway `X_ACTIVITY_RULE`
- `UiValueRule`

Use `ambient-activity-expression-rules.md` for the deeper rule body.

## 4. Companion Documents

- `attachment-storage-configurable-storage.md`
- `io-utility-hfs-hstore-rules.md`
- `activity-log-guide.md`
- `ambient-activity-expression-rules.md`

## 5. Agent Rules

- Read Ambient first for app registry, document metadata, upload session, and activity history.
- Do not move attachment/document semantics down into generic plugins.
- Do not move storage-provider mechanics up into Ambient provider code.
