---
description: Ambient activity log, tracked-field diff, activity-rule metadata, and expression-evaluation routing rules.
globs:
  - "zero-ecosystem/zero-plugins-extension/zero-exmodule-ambient/**/*.java"
  - "zero-ecosystem/zero-plugins-extension/zero-extension-skeleton/**/*.java"
  - "zero-ecosystem/zero-plugins-extension/zero-exmodule-ambient/**/resources/**/*"
alwaysApply: false
---

# Ambient Activity Log and Expression Rules

Load this rule when a task touches activity logs, change history, `X_ACTIVITY`, `X_ACTIVITY_CHANGE`, `X_ACTIVITY_RULE`, activity rule expressions, tracked fields, EXPR/JEXL-style configuration, activity hooks, or audit-diff behavior in `zero-exmodule-ambient`.

## Ownership

- `zero-exmodule-ambient` owns activity log persistence, change records, activity-rule metadata, and system-side activity read/write behavior.
- `zero-extension-skeleton` owns the stable SPI contract `ExActivity` and shared helper behavior such as expression-backed DAO reads through `KeEnv`.
- The model/metadata layer owns which fields are trackable. Activity diff code reads `HAtom.marker()` and tracked attribute markers instead of hard-coding field lists.
- Workflow or business modules may trigger activity creation, but they should not own the activity persistence model.

## Source Anchors

- SPI contract: `ExActivity`.
- Read/write service: `ActivityService`, `ExActivityTracker`.
- Diff engine: `Schism`, `SchismBase`, `SchismJ`, `At.diffChange`, `AtDiffer`.
- Rule metadata: `XActivityRule`, `XActivityRuleDao`, `TypeOfAmbientJsonObject`.
- Tables: `X_ACTIVITY`, `X_ACTIVITY_CHANGE`, `X_ACTIVITY_RULE`.
- Expression helper: `KeEnv.daoJ`, `KeEnv.daoA`, `Ut.fromExpression`.
- Workflow integration example: `zero-exmodule-workflow` after-activity hooks such as `AfterActivityTabb`.

## Activity Data Model

- `X_ACTIVITY` is the parent history record. It stores model identity, model key/category, task/workflow context, old/new snapshots, scope fields, and audit fields.
- `X_ACTIVITY_CHANGE` is the child field-level diff record. It stores `activityId`, change type, status, field name, field alias, field type, old value, and new value.
- `X_ACTIVITY_RULE` is rule metadata. Important fields include `definitionKey`, `taskKey`, `hookComponent`, `hookConfig`, `logging`, `ruleConfig`, `ruleExpression`, `ruleField`, `ruleIdentifier`, `ruleMessage`, `ruleName`, `ruleNs`, `ruleOrder`, and `ruleTpl`.
- `TypeOfAmbientJsonObject` marks `hookConfig`, `ruleConfig`, and `ruleTpl` as JSON object columns for generator/runtime conversion.

## Diff Flow

1. Caller obtains a `Schism` through `Schism.diffJ(atom)`.
2. `SchismBase.bind(atom)` verifies that the model is trackable and that tracked fields exist.
3. `SchismJ.diffAsync(recordO, recordN, activityFn)` creates the parent activity through `activityFn`, attaches `recordOld` and `recordNew`, and computes changes.
4. `AtDiffer.diff(recordO, recordN, atom)` detects ADD/UPDATE/DELETE with `Ut.aiFlag` and reads tracked fields from `atom.marker().enabled(EmAttribute.Marker.track)`.
5. For each tracked field, `AtDiffer` uses `atom.attribute(field)` and `HMetaField` metadata to populate `fieldName`, `fieldAlias`, and `fieldType`.
6. UPDATE changes use `atom.vs().isChange(valueO, valueN, field)`; do not replace this with plain string equality.
7. `AtDiffer.diff(changes, activity)` binds each child change to the parent activity, sets default status to `CONFIRMED`, and copies audit fields.
8. `SchismBase.createActivity` persists `X_ACTIVITY` first, then `X_ACTIVITY_CHANGE` rows.

## Expression and Rule Semantics

- Treat `ruleExpression` as rule-selection or rule-evaluation metadata, not as a place for Java code.
- Treat `ruleConfig`, `hookConfig`, and `ruleTpl` as JSON configuration inputs that can be interpreted by hooks or helper APIs.
- Use `Ut.fromExpression(template, params)` for expression-backed template expansion when existing code does so.
- Use `KeEnv.daoJ` or `KeEnv.daoA` when a rule/config template needs to resolve DAO criteria from parameters. These helpers read `criteria`, apply `Ut.fromExpression`, and call DB through the configured DAO.
- Keep expression evaluation side-effect free. Expressions should calculate parameters, criteria, messages, or templates; persistence belongs in services or hooks.

## Agent Rules

- Do not hard-code tracked fields in activity code. Add or adjust track markers in model metadata and let `HAtom.marker()` drive the diff.
- Do not bypass `Schism` / `AtDiffer` for standard activity history. They coordinate trackability, metadata, diff type, and child change creation.
- Do not mutate `X_ACTIVITY_CHANGE.status` freely. `ActivityService.saveChanges` only upgrades `PENDING` to `CONFIRMED` under the confirmed path; system status has special meaning.
- Do not store arbitrary objects into old/new values in `X_ACTIVITY_CHANGE`; current diff code serializes non-null values with `toString()`.
- Do not change `ExActivity` for one module. Add module-specific behavior through hooks, rule metadata, or service code.
- Keep `XActivityRule` JSON fields registered in `TypeOfAmbientJsonObject` when adding new JSON-object columns.
- When adding a rule parser or hook, keep parsing in Ambient and keep business-specific effects in hook components or consuming modules.

## MCP Client Routing Rule

External projects connected through MCP often describe this feature as "audit log", "change log", "activity rule", "expression rule", "approval activity", or "workflow history". Route those terms to this file first, then inspect exact source anchors.

## Search Hints

- Activity read APIs: `ActivityService`, `ExActivityTracker`, `ExActivity`.
- Diff generation: `Schism.diffJ`, `SchismJ.diffAsync`, `AtDiffer.diff`.
- Rule metadata: `XActivityRule`, `ruleExpression`, `hookConfig`, `ruleConfig`, `ruleTpl`.
- Expression-backed DAO helpers: `KeEnv.daoJ`, `KeEnv.daoA`, `Ut.fromExpression`.
