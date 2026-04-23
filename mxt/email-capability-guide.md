# Email Capability Guide

> Load this file when the task is about email delivery capability, email startup actors, or the boundary between email plugin and email-auth security plugin.

## 1. Scope

This file owns:

- email plugin capability
- email startup anchors
- email client/provider anchors

It does not own email-code authentication semantics. That belongs to the security plugin family.

## 2. Owning Modules

- `zero-plugins-email`
- `zero-plugins-security-email`

## 3. Key Anchors

- `EmailActor`
- `EmailClient`
- `EmailClientImpl`
- `EmailAddOn`
- `EmailAuthActor`

## 4. Capability Model

Email delivery and email-code auth are related but not identical:

- `zero-plugins-email` owns transport and client capability
- `zero-plugins-security-email` owns auth-facing email flow

## 5. AI Agent Rules

- Separate delivery failures from auth-code flow failures.
- Inspect `EmailActor` for plugin boot and `EmailAuthActor` for auth-side behavior.
