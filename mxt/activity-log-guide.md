# Activity Log Guide

> Load this file when the task is about reusable activity logging, ambient-side audit trails, or the activity-rule engine boundary.

## 1. Scope

This file owns:

- activity logging as a reusable business concern
- ambient-side activity service ownership
- the link from activity behavior to shared activity SPI

## 2. Owning Modules

- `zero-exmodule-ambient`
- shared `ExActivity` SPI

## 3. Key Anchors

- `ExActivity`
- `ActivityStub`
- `ActivityService`

## 4. AI Agent Rules

- Keep reusable activity semantics in ambient and its SPI seam.
- Do not move activity policy into generic monitor plugins.
