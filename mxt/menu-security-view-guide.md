# Menu Security View Guide

> Load this file when a downstream project asks how to filter menus with
> `S_VIEW`, `X_MENU`, `/api/menus`, frontend-stored YAML menus, role menu
> visibility, or route guards after login.

## 1. Scope

This file owns the runtime menu-authorization pattern where `S_VIEW` is used as
the permission view for menu visibility.

It covers two supported menu storage modes:

| Mode | Menu structure source | Authorization source | Typical consumer |
|---|---|---|---|
| Backend-stored menu | `X_MENU` | `S_VIEW.ROWS.name` | `GET /api/menus` |
| Frontend-stored menu | frontend YAML/static menu files | `S_VIEW.ROWS.name` exposed as allowed names | frontend menu loader + route guard |

It does not replace API permission checks. Menu filtering is a navigation and
entry-control layer; sensitive backend APIs must still be protected through
RBAC resources, actions, and the normal Zero authorization chain.

## 2. Source Anchors

Use these anchors before inventing new behavior:

| Concern | Anchor |
|---|---|
| Post-login app/menu entry | `zero-exmodule-ambient-api/.../EntryAgent.java` |
| Current backend menu endpoint | `GET /api/menus` |
| ACL menu management list | `GET /api/acl-menus` |
| Menu table | `zero-exmodule-ambient-domain/.../X_MENU` |
| Security view table | `zero-exmodule-rbac-domain/.../S_VIEW` |
| Menu resource id | `Sc.resourceMenu()` |
| Default menu rows payload | `Sc.valueMenus()` |
| Default role view creation | `RoleService.saveDefaultView(...)` |
| `S_VIEW` result filtering semantics | `DwarfArray`, `DwarfPagination`, `DataBound` |
| Zero UI menu API | `zero-ui/mxt/mxt-zeroui/ex-api.md` |

## 3. `S_VIEW` Menu Semantics

For menu visibility, treat `S_VIEW` as a role/user view matrix keyed by the menu
resource.

Required fields:

| `S_VIEW` field | Menu meaning |
|---|---|
| `OWNER_TYPE` | normally `ROLE`; user-level overrides may use `USER` |
| `OWNER` | role id or user id |
| `RESOURCE_ID` | `Sc.resourceMenu()` |
| `NAME` | usually the default view name |
| `POSITION` | usually the default position |
| `ROWS` | object containing allowed menu names, e.g. `{ "name": ["apps.root"] }` |
| `ACTIVE` | must be true |

For a user with multiple roles, compute the union of all active role views for
the menu resource unless a project has explicitly defined stricter intersection
semantics. Empty `ROWS.name` must fail closed: return no menus and do not fall
back to all menus.

Do not derive authorization from username. Username may be useful for a local
test report, but runtime menu permission must come from roles/users and
`S_VIEW`.

New role handling must be append-only. Creating or importing a new role may add
a new `S_VIEW` row for that role, or merge new menu names into that role's
existing view, but it must not rewrite, clear, or replace other roles' views.
Use an idempotent key such as:

```text
ownerType + owner + resourceId + name + position
```

When that view already exists, merge `ROWS.name` as a set union and de-duplicate
menu names. Do not replace the whole `ROWS` object with the current import
payload unless the operator explicitly asked for a destructive reset.

## 4. Mode A: Backend-Stored Menus (`X_MENU`)

Use this mode when menus are installed into the Zero/Ambient menu table and the
frontend consumes backend-driven menus.

Recommended runtime flow:

```text
login
  -> frontend calls /api/menus with X-App-Id
  -> backend loads X_MENU by app id
  -> backend loads active S_VIEW rows for current user roles and Sc.resourceMenu()
  -> backend keeps only X_MENU records whose name is in S_VIEW.ROWS.name
  -> backend adds required ancestors when children are allowed
  -> frontend renders the filtered result
```

Implementation rules:

- Keep `/api/acl-menus` or equivalent admin permission screens able to read all
  app menus for configuration. Do not use the user-filtered endpoint as the
  permission editor's source of truth.
- `X_MENU` identity is `NAME + APP_ID`; use the existing `BuildApp` /
  `BuildMenuPersister` flow for installation-time menu persistence.
- Filtering by `S_VIEW.ROWS.name` happens after identifying the current user's
  role ids and the menu resource id.
- Role/menu imports are append-only: upsert the target role view and merge
  `ROWS.name`; never rebuild all role menu views from one role's payload.
- If the endpoint returns no allowed names, return an empty array instead of
  exposing all menus.
- A menu endpoint override in an app should be treated as an app-local bridge
  only unless the behavior is reusable enough to move into `zero-exmodule-rbac`
  or `zero-exmodule-ambient`.

This mode is best when the backend is the authoritative source for menu
structure, icons, text, parent/child relations, and type such as side/top menus.

## 5. Mode B: Frontend-Stored Menus

Use this mode when a downstream app owns menu structure in frontend YAML/static
files and does not want to persist those menus into `X_MENU`.

Recommended runtime flow:

```text
login
  -> frontend requests a secure "visible menu names" endpoint
  -> backend loads S_VIEW.ROWS.name for current user roles and Sc.resourceMenu()
  -> backend returns only allowed menu names, not the full menu structure
  -> frontend loads its local YAML/static menus
  -> frontend filters local menus by allowed names
  -> frontend builds uri -> menu.name mapping
  -> frontend route guard rejects paths whose menu name is not allowed
```

Recommended endpoint shape:

```http
GET /api/menus/visible
Authorization: Bearer <token>
X-App-Id: <appId>
```

Response:

```json
{
  "data": {
    "names": ["apps.root", "apps.onboard", "personal.security.account"]
  }
}
```

Implementation rules:

- The endpoint must read roles from the authenticated user principal or session
  context, not from username.
- The response should be a set of allowed menu names. Avoid returning static
  menu structure from backend if the frontend owns that structure.
- Adding a new frontend role/menu profile must append or merge the corresponding
  role's allowed names in `S_VIEW`; it must not overwrite other roles' allowed
  names.
- The frontend must filter by exact menu `name`, then remove orphan children or
  add required ancestors according to the app's tree-building rule.
- Cache by `appId + userId` at minimum. Clear menu and route-guard cache on
  login, logout, and app switch.
- Empty allowed names must render no protected menu and deny protected routes.
- Public routes such as login, callback, static error pages, or health screens
  need an explicit allowlist.

This mode is best when the app has a custom frontend stack or static menu files,
but still wants Zero RBAC to own authorization.

## 6. Route Guard Contract

Menu hiding alone is not a complete user-facing security flow. Frontend-stored
menus must also guard route entry.

Guard algorithm:

```text
load all menu definitions
load allowed menu names from S_VIEW
build allowedNameSet
build uriToName from all menu definitions
on navigation:
  if path is public -> allow
  else if no uri mapping -> deny
  else if uriToName[path] in allowedNameSet -> allow
  else deny
```

Denial behavior should be deterministic: show a 403/no-permission page or
redirect to a safe default route. Do not silently render the target business
page and rely on missing sidebar entries as the only protection.

Backend APIs remain authoritative. If a route is denied in the frontend, that is
good UX. If a user bypasses the frontend, the API must still reject unauthorized
operations through RBAC resources and actions.

## 7. Choosing the Mode

| Situation | Prefer |
|---|---|
| Existing Zero UI app expects `Ex.I.menus()` and backend menu tree | Backend-stored `X_MENU` mode |
| App install/bootstrap already owns menu import | Backend-stored `X_MENU` mode |
| Custom frontend owns YAML/static menus | Frontend-stored menu mode |
| App wants no `X_MENU` rows but still wants RBAC visibility | Frontend-stored menu mode |
| Permission-management screen needs the full menu universe | Backend source or explicit full-menu endpoint, not user-filtered `/api/menus` |

Both modes may coexist in one system, but each app should choose one runtime
menu-structure authority. Do not mix "backend returns partial `X_MENU`" and
"frontend loads full YAML" without a clear precedence rule.

## 8. MCP Routing Rule

When an MCP client asks about:

- "`S_VIEW` menu filtering"
- "`X_MENU` authorization"
- "`/api/menus` returns too many/too few menus"
- "frontend YAML menu with RBAC"
- "route guard based on menu permission"
- "login后菜单过滤"

route here first, then inspect:

1. `exmodule-rbac-guide.md` for RBAC ownership
2. `backend-rbac-rules.md` for resource/view semantics
3. `buildapp-buildperm-flow.md` for installation-time `X_MENU` / RBAC import
4. `zero-ui/mxt/mxt-zeroui/ex-api.md` for Zero UI menu consumption
5. source anchors listed in Section 2

Do not answer from `X_MENU` alone. The security decision is in `S_VIEW` and
RBAC resources; `X_MENU` is only one possible menu structure store.
