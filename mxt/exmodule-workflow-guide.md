# Exmodule Workflow Guide

> Load this file when the task is about reusable workflow engine behavior, queue and todo handling, deployment hooks, or `zero-exmodule-workflow`.

## 1. Scope

This file owns `zero-exmodule-workflow`.

## 2. Key Anchors

- `RunActor`
- `QueueActor`
- `TodoActor`
- `FlowStub`
- `TaskStub`
- `TodoStub`
- `FlowService`
- `TaskService`
- `TodoService`
- `ExtensionWorkflowSource`
- `MDWorkflowActor`

## 3. AI Agent Rules

- Put reusable workflow-domain behavior here.
- Keep generic transport, security, and database capability outside unless the issue is really framework-level.
