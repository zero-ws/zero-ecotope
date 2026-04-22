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

## 4. Sub-Module Responsibilities

| Module | Responsibility | When AI Agent Should Inspect |
|---|---|---|
| `zero-plugins-security` | Base security plugin: shared `SecurityActor` startup, `SecurityProviderFactory` composition, common provider infrastructure | Security boot failure, provider registration issues, "which protocol handles login" |
| `zero-plugins-security-jwt` | JWT token generation, validation, and renewal via `JwtSecurityActor` | JWT login/logout failures, token expiry, signature mismatch |
| `zero-plugins-security-ldap` | LDAP bind authentication via `LdapSecurityActor` | LDAP directory login failures, bind DN issues, group mapping |
| `zero-plugins-security-oauth2` | OAuth2 client authorization flow via `OAuth2AuthActor`; distinct from `zero-plugins-oauth2` (capability) | OAuth2 login redirect failures, code exchange errors, provider configuration |
| `zero-plugins-security-email` | Email-based one-time authentication | Email OTP delivery or verification failures for auth use cases |
| `zero-plugins-security-sms` | SMS-based one-time authentication | SMS OTP delivery or verification failures for auth use cases |
| `zero-plugins-security-weco` | WeChat/Weco platform authentication | Weco OAuth or QR-code login failures |
| `zero-plugins-security-otp` | Generic one-time-password authentication (TOTP/HOTP) | OTP verification failures, secret provisioning |
| `zero-plugins-security-htpasswd` | Apache-style `.htpasswd` file authentication | Flat-file auth failures, htpasswd format issues |
| `zero-plugins-security-htdigest` | Apache-style `.htdigest` file authentication | Digest auth failures, realm mismatch |

Selection rule:

- Choose the auth protocol module matching the user's login method.
- `zero-plugins-security` is always required; protocol modules are additive.
- Do not confuse `zero-plugins-security-oauth2` (auth protocol) with `zero-plugins-oauth2` (OAuth2 server capability).

## 5. Capability Model

The security family is plugin-driven:

- base security plugin owns shared startup and provider composition
- protocol modules contribute specialized providers or actors
- higher-level permission resources remain outside this plugin family

## 5. AI Agent Rules

- Distinguish authentication capability from RBAC resource ownership.
- When login protocol behavior is wrong, inspect the matching provider or actor module first.
- Do not use permission-resource documents to diagnose provider boot failures.
