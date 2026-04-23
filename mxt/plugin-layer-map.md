# Plugin Layer Map

> `zero-plugins-equip` is the capability plugin layer of Zero Ecotope.
> It provides reusable infrastructure capability only. It does not own business rules.

## 1. Plugin Groups

### Storage and Search

| Module | Responsibility |
|---|---|
| `zero-plugins-redis` | Redis integration |
| `zero-plugins-cache` | Abstract cache layer |
| `zero-plugins-cache-ehcache` | EhCache implementation |
| `zero-plugins-elasticsearch` | Elasticsearch integration |
| `zero-plugins-neo4j` | Neo4j graph database integration |
| `zero-plugins-flyway` | Database migration management |

### Session and Transport

| Module | Responsibility |
|---|---|
| `zero-plugins-session` | Session management |
| `zero-plugins-websocket` | WebSocket support |

### Messaging and Notification

| Module | Responsibility |
|---|---|
| `zero-plugins-email` | Email delivery |
| `zero-plugins-sms` | SMS delivery |

### Security Protocols

| Module | Responsibility |
|---|---|
| `zero-plugins-oauth2` | OAuth2 integration |
| `zero-plugins-security-oauth2` | OAuth2 security adapter |
| `zero-plugins-security-ldap` | LDAP authentication |
| `zero-plugins-security-otp` | One-time password authentication |
| `zero-plugins-security-htpasswd` | htpasswd authentication |
| `zero-plugins-security-htdigest` | htdigest authentication |
| `zero-plugins-security-weco` | Weco security integration |
| `zero-plugins-security-email` | Email-code authentication |
| `zero-plugins-security-sms` | SMS-code authentication |

### Monitoring and Operations

| Module | Responsibility |
|---|---|
| `zero-plugins-monitor` | Core monitoring capability |
| `zero-plugins-monitor-prometheus` | Prometheus integration |
| `zero-plugins-monitor-hawtio` | Hawtio integration |

### Other Capability Modules

| Module | Responsibility |
|---|---|
| `zero-plugins-weco` | Weco platform integration |
| `zero-plugins-swagger` | Swagger/OpenAPI documentation |
| `zero-plugins-trash` | Soft-delete / recycle-bin support |

## 2. Layer Rule

This layer answers one question:

**What infrastructure capability can the framework plug in?**

It does **not** answer:
- Which business rule should trigger the capability
- How a domain should model its behavior
- How a project should customize a specific workflow

## 3. Correct Boundary

| Belongs here | Does not belong here |
|---|---|
| Cache providers and connection management | Business cache key policy |
| Mail/SMS delivery capability | Business notification timing |
| Authentication protocol adapters | Role and permission rules |
| Search engine integration | Domain search semantics |
| Migration engine integration | Project-specific bootstrap data logic |

## 4. Consumption Rule

Exmodules and applications should consume plugin capability through framework abstractions such as `Fx`, `Ux`, and SPI lookups via `HPI`.

They should not depend on a plugin module's concrete implementation class directly.

See [exmodule-boundary.md](exmodule-boundary.md) for the full boundary rule.
