# Exmodule LBS Guide

> Load this file when the task is about location service behavior, reusable geo lookup logic, or `zero-exmodule-lbs`.

## 1. Scope

This file owns `zero-exmodule-lbs`.

## 2. Key Anchors

- `QueryActor`
- `LocationStub`
- `LocationService`
- `ExtensionLBSSource`
- `MDLbsActor`

## 3. AI Agent Rules

- Put reusable location-domain behavior here.
- Keep migration SPI or storage capability in Flyway or core data-source layers.
