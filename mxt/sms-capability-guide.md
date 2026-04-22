# SMS Capability Guide

> Load this file when the task is about SMS delivery capability, SMS startup actors, or the boundary between SMS plugin and SMS-auth security plugin.

## 1. Scope

This file owns:

- SMS plugin capability
- SMS startup anchors
- SMS client/provider anchors

It does not own SMS-code authentication semantics. That belongs to the security plugin family.

## 2. Owning Modules

- `zero-plugins-sms`
- `zero-plugins-security-sms`

## 3. Key Anchors

- `SmsActor`
- `SmsClient`
- `SmsClientImpl`
- `SmsAddOn`
- `SmsAuthActor`

## 4. Capability Model

SMS delivery and SMS-based auth are split:

- delivery belongs to the core SMS plugin
- auth flow belongs to the security SMS plugin

## 5. AI Agent Rules

- Diagnose delivery and authentication as separate concerns.
- Inspect `SmsActor` for transport startup and `SmsAuthActor` for login-side behavior.
