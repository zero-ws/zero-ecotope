# Exmodule TPL Guide

> Load this file when the task is about template-center, menu/notify template, or personal-setting semantics in `zero-exmodule-tpl`.

## 1. Scope

`zero-exmodule-tpl` owns reusable template-center and personal-setting business semantics.

## 2. Verified Anchors

Typical ownership in this module should be read through:

- menu/template/notify-facing API actors
- `MenuStub`, `NotifyStub`-style domain seams
- provider services under template and personal-setting semantics
- `ExtensionTPLSource`

This exmodule is still domain/business ownership, not transport capability ownership.

## 3. Boundary

Use TPL when the issue is about reusable template/menu/notify semantics.

Do not use it for:

- raw email or SMS delivery transport
- low-level content rendering engines

## 4. Source and Resource Path

Read in this order:

```text
exmodule-tpl-guide.md
-> exmodule-boundary.md
-> delivery capability guides only if transport ownership is unclear
-> zero-exmodule-tpl source/resources
```

High-value proof targets:

- menu/template/notify actors
- `MenuStub`, `NotifyStub`
- template and personal-setting services
- `ExtensionTPLSource`

## 5. Pairwise Handling

Preferred pairs:

- `zero-ecotope` alone for template-center business ownership
- `zero-ecotope` + `r2mo-rapid` when email/SMS/WeCo delivery boundary must be confirmed

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one template/menu/notify symbol is already known
- the unresolved point is whether the behavior stays in TPL or drops into delivery transport modules
