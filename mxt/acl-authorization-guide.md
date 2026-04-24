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

## 4. Source and Resource Path

Read in this order:

```text
acl-authorization-guide.md
-> backend-rbac-rules.md
-> exmodule-rbac-guide.md
-> security-plugin-flow.md only if protocol/auth provider behavior is involved
-> exact source/resources
```

High-value proof targets:

- `PERM.yml`
- `RBAC_RESOURCE/**`
- `RBAC_ROLE/**`
- `ScPermit`, `ScSeeker`
- ACL-oriented business hooks such as `SyntaxAop` or `QuestAcl`

## 5. Boundary

Use this guide when the issue is about:

- ACL data-domain ownership
- reinforced authorization semantics
- permission-resource interpretation above raw login/auth

Do not use it for:

- JWT/OAuth2/LDAP protocol behavior
- captcha or token issuance
- generic session/provider registration

## 6. Pairwise Handling

Preferred pairs:

- `zero-ecotope` + `r2mo-spec` when shared permission/resource semantics need contract confirmation
- `zero-ecotope` alone when the issue is RBAC resource loading or ACL runtime behavior

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- `ScPermit`, `ScSeeker`, `SyntaxAop`, or `QuestAcl` is already known
- the unresolved point is structural spread between RBAC resources and runtime consumers

## 8. AI Agent Rules

- Use RBAC resources for declarative permission ownership.
- Use security plugins only when the issue is protocol or provider behavior.
- Keep ACL domain rules in RBAC, not in generic auth plugins.
