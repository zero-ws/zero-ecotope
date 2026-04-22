# ACL Authorization Guide

> Load this file when the task is about ACL, authorization data domains, or reinforced authorization behavior above simple resource declarations.

## 1. Scope

This file owns the composite scenario:

- ACL behavior
- authorization data domains
- reinforced authorization model boundaries

## 2. Owning Modules

- `zero-exmodule-rbac`
- `zero-plugins-security*`

## 3. Key Anchors

- `RBAC_RESOURCE`
- `RBAC_ROLE`
- `PERM.yml`
- `ScPermit`
- `ScSeeker`

## 4. AI Agent Rules

- Use RBAC resources for declarative permission ownership.
- Use security plugins only when the issue is protocol or provider behavior.
- Keep ACL domain rules in RBAC, not in generic auth plugins.
