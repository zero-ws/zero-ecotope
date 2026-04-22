# Security Plugin Flow

> Load this file when the task is about `zero-plugins-security*`, authentication plugin startup, provider selection, or protocol-specific security add-ons.

## 1. Scope

This file owns:

- security plugin family layout
- base security plugin startup
- protocol-specific security providers
- actor and provider anchors for security capability

It does not own RBAC resource semantics. Use `backend-rbac-rules.md` for permission resources.

## 2. Owning Modules

- `zero-plugins-security`
- `zero-plugins-security-jwt`
- `zero-plugins-security-ldap`
- `zero-plugins-security-oauth2`
- `zero-plugins-security-email`
- `zero-plugins-security-sms`
- `zero-plugins-security-weco`
- `zero-plugins-security-otp`
- `zero-plugins-security-htpasswd`
- `zero-plugins-security-htdigest`

## 3. Key Anchors

- `SecurityActor`
- `SecurityProviderFactory`
- `BasicSecurityProvider`
- `JwtSecurityActor`
- `LdapSecurityActor`
- `OAuth2AuthActor`
- `EmailAuthActor`
- `SmsAuthActor`

## 4. Capability Model

The security family is plugin-driven:

- base security plugin owns shared startup and provider composition
- protocol modules contribute specialized providers or actors
- higher-level permission resources remain outside this plugin family

## 5. AI Agent Rules

- Distinguish authentication capability from RBAC resource ownership.
- When login protocol behavior is wrong, inspect the matching provider or actor module first.
- Do not use permission-resource documents to diagnose provider boot failures.
