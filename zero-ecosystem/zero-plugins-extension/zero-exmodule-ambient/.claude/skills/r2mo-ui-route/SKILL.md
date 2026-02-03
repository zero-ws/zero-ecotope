---
name: r2mo-ui-route
description: Frontend: Route tree, guards, and navigation topology from R2MO specs.
version: 7.0.0
tags: [r2mo, frontend, router, navigation-flow, guards, topology]
repository: internal
---

# Role: Frontend — Route and Guards

## Meta-Instruction

This skill targets frontend development. Outputs are Vue/React + TypeScript artifacts (route config, guards, menu projection) driven strictly by R2MO specification documents. You are the "nervous system" of the application: you translate static page-planning specs into a runtime routing tree. You do not build UI components or the container (admin does that); you generate configuration and logic. You must ensure every URL maps to a resource, every transition is guarded, and navigation provides feedback. Route config and menu structure feed the admin layout; you do not implement Rust/WASM code.

## Note Properties (Front-Matter) Convention

All specifications referenced by this skill are carried in .md documents. **Each .md includes a YAML front-matter block (note properties) at the top.** Before using any spec, parse that document's front-matter and extract the relevant keys. Drive route structure, layout association, guards, and meta strictly from these attributes. Keys most relevant here: `spec` (design.page, requirement.module), `route`, `route_base`, `layout`, `guards`, `keep_alive`, `order`, `title`, `icon`, `roles`, `hidden`. Do not rely on specific .md filenames or paths (e.g. do not hardcode `.r2mo/design/page`).

## Interaction & Flow Standards (The "Official" Feel)

Even invisible logic affects the feel of the app. Enforce:

1. **Transition Choreography**
   - **Lazy Loading**: All routes must be lazy-loaded (`() => import(...)`).
   - **Chunk Naming**: Generate clear Webpack/Vite chunk names (e.g. chunk-sys-user) for debugging and caching.
2. **Perceived Performance**
   - **Progress Feedback**: Implement NProgress (or equivalent) bound to router hooks. Color must match parsed `primary` from design.system.
   - **Title Management**: Update browser tab title dynamically (Page Title - App Name) on every route change.
3. **Exception Flows**
   - **404**: Generate a 404 catcher route at the end of the tree.
   - **403**: Redirect unauthorized access to a dedicated error page, not a blank screen.

## Context Resolution (Topology Discovery)

Do not rely on fixed paths (e.g. `.r2mo/design/page`, `.r2mo/requirements/module`). Scan and parse any .md whose front-matter matches the following conditions.

### 1. Nodes (Page Planning)

**Condition**: Front-matter contains `spec: design.page` or `spec: ui.page` (or project-equivalent).

**Logic**: Each such document is a leaf node (page).

**Key attributes:** `route`, `layout`, `guards`, `keep_alive`, `title`, `icon`, `roles`, `hidden`.

### 2. Clusters (Module Planning)

**Condition**: Front-matter contains `spec: requirement.module` (or project-equivalent).

**Logic**: Each such document is a branch node (module).

**Key attributes:** `route_base`, `order`, `guards`.

### 3. Home (The Origin)

**Logic**: Derive from parsed specs: explicit `redirect: home` or lowest `order` module. Determine where `/` redirects.

## Feature Synthesis (Execution Rules)

### Phase A: Topology Tree (Route Generation)

1. **Structure Definition**: Create a nested route object. Map parsed module specs to first-level routes; map parsed page specs to children by matching `route` prefix with module `route_base`.
2. **Layout Association**: Use parsed `layout` from each page spec. If a page specifies a main-layout name (e.g. the value corresponding to the shell layout), nest it under the main layout route. If a page specifies a blank/minimal layout, place it at root level (e.g. entry/auth). Do not hardcode "AdminLayout" or "BlankLayout"; use the layout names from parsed front-matter.

### Phase B: Meta Injection

Inject metadata for admin to consume: `title`, `icon` from page spec; `keepAlive` from `keep_alive`; `roles`/`permissions` into `meta.roles`; breadcrumb hierarchy into `meta.breadcrumb`.

### Phase C: Traffic Control (Guards)

Generate router guards (path from project convention, e.g. `src/router/guards/index.ts`). White-list: allow if page/API spec has `auth: false`. Auth guard: if route requires auth (default), check token; no token -> redirect to entry route with current path as `redirect` query. Permission guard: match user roles to `meta.roles`; mismatch -> redirect to 403.

### Phase D: Menu Data Projection

Generate a utility to transform the router tree into menu structure. Filter out routes with `hidden: true`. Sort by `order` from module specs. Output standard structure (`{ key, label, icon, children }`) for the sidebar (admin consumes this).

## Boundaries & Constraints

- **No UI Rendering**: You generate configuration (`routes.ts`) and logic (`guards.ts`), not .vue components. Admin builds the shell and consumes your menu data.
- **No Business Logic**: You route based on flags (roles, token presence); you do not validate data (e.g. password correctness).
- **Path Uniqueness**: Ensure generated paths do not collide; if specs collide, prioritize the more specific definition.
- **Handoff**: Route config and menu projection feed admin; login hands off to admin after auth; you provide the topology admin uses for sidebar and breadcrumbs.

## Rust / WebAssembly Frontend Context

In this project, **Rust** refers to **Rust-for-WebAssembly frontend** (e.g. Yew, Leptos, wasm-bindgen), not a backend. When the stack includes Rust/WASM: use shared types and JS interop so that route guards and meta that depend on permissions/user data work with both TS and WASM. This skill does not implement Rust/WASM code; it only considers the above when guards consume WASM-derived state. API calls (e.g. permissions) are to an HTTP backend (any language); align with that backend’s contract separately.
