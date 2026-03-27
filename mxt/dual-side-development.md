# Dual-Side Development

> Zero Ecotope development is a two-sided workflow: backend extension contracts on one side, frontend extension packages on the other.

## 1. Scenario Split

| Scenario | Backend | Frontend |
|---|---|---|
| Management-intensive | R2MO Rapid Spring | Zero UI |
| Compute / interaction-intensive | Zero Epoch | R2MO Web |

This split comes from the repository README and explains why Zero and R2MO coexist in the same ecosystem.

## 2. Backend-Frontend Alignment

`zero-ui/src/extension/` is intentionally aligned with backend `zero-plugins-extension`.

| Backend concern | Frontend concern |
|---|---|
| `zero-extension-api` route surface | `src/extension/components` page routing and API calls |
| `UiForm` / `UiValve` / `UiApeak*` SPIs | `src/extension/ecosystem` dynamic components and layout assembly |
| `zero-exmodule-*` reusable domain modules | `src/extension/library` domain-specific UI logic |

Rule: backend extension contracts and frontend extension packages should evolve together.

## 3. Backend-First Flow

Use this flow when adding a new exmodule or backend capability.

1. Confirm whether an existing skeleton SPI already fits.
2. Define or reuse the API surface in `zero-exmodule-*-api`.
3. Implement domain logic and SPI wiring in `*-provider`.
4. Add or update frontend pages/components inside `zero-ui/src/extension/`.

## 4. Frontend-First Flow

Use this flow when improving an existing admin experience or reorganizing UI behavior.

1. Define the page/component need in `zero-ui`.
2. Map it to existing `UiForm`, `UiValve`, or `UiApeak` contracts.
3. Add backend API/SPI support only if existing extension contracts are insufficient.

## 5. Ownership Split

### Backend owns
- domain models
- API contracts
- permission and validation rules
- dynamic form/valve data sources

### Frontend owns
- page layout
- component assembly
- interaction flow
- user-facing experience

### Both sides jointly maintain
- field naming
- state and enum definitions
- configuration structure
- menu and visibility rules

## 6. R2MO vs Zero Focus

| Project type | Backend focus | Frontend focus |
|---|---|---|
| R2MO + Zero UI | forms, workflow, permissions, configuration | admin UX and structured page assembly |
| Zero Epoch + R2MO Web | async events, high-concurrency paths, interaction-heavy flows | interactive, state-driven experiences |

Shared rule:
- New reusable business capability should be modeled in `zero-plugins-extension` first.
- Do not bind frontend code directly to plugin implementations.

## 7. Practical Constraints

1. Do not hardcode backend internals into frontend components.
2. Do not mutate shared skeleton SPIs to satisfy one page-only need.
3. Evaluate `UiForm`, `UiValve`, and `UiApeak*` before adding new UI extension mechanisms.
4. Treat exmodule `*-api` as the stable contract surface for frontend integration.
