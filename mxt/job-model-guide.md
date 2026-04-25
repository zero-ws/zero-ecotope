# Job Model Guide

> Load this file when the task is about `@Job`, scheduled missions, runtime job extraction, or the MBSE job-facing API surface.

## 1. Scope

This file owns:

- framework job model
- annotation-driven mission extraction
- runtime scheduling metadata
- the relationship between epoch job runtime and MBSE job APIs

It does not own generic startup order. Use `actor-startup-matrix.md` for `@Actor`.

## 2. Owning Modules

- `zero-epoch-focus`
- `zero-epoch-use`
- `zero-exmodule-mbseapi`

## 3. Key Anchors

- `io.zerows.epoch.annotations.Job`
- `io.zerows.cosmic.plugins.job.JobExtractor`
- `JobConfig`
- `JobStoreUnity`
- `Mission`
- `TaskApi`
- `JobStub`
- `JobService`

## 4. Runtime Model

Zero job runtime is centered on `Mission`. A `Mission` may come from code
(`@Job`) or from an extension `JobStore` such as MBSE API database-backed jobs.

The common runtime fields are:

- `code`: runtime identity. Code-defined jobs default to
  `vertx.zero.jobs-{mission.name}`.
- `name`, `type`, `comment`, `additional`, `metadata`: management and
  execution metadata.
- `readOnly`: `true` for code-defined jobs, `false` for stored/dynamic jobs.
- `status`: state machine value, initially `STARTING`.
- `threshold`: execution timeout, stored on `Mission`, not on `KScheduler`.
- `scheduler`: a `KScheduler` for timed jobs.
- `proxy`, `on`, `off`: runtime class instance and `@On` / optional `@Off`
  methods.
- `income`, `incomeAddress`, `outcome`, `outcomeAddress`: input/output
  component or EventBus binding metadata.

## 5. How `@Job` Is Scanned And Entered Into Runtime Storage

The static/code path is:

```text
JobActor
-> ORepositoryJob.whenStart(...)
-> JobInquirer scans loaded classes
-> JobExtractor.extract(Class<?>)
-> OCacheJob
-> JobStoreCode.fetch()
-> JobStoreUnity.fetch()
-> JobQueue.save(...)
```

Important behavior:

- `JobActor` is the runtime actor for the `job` node. During startup it scans
  job classes through `ORepositoryJob` and then configures `JobClientManager`.
- `JobExtractor` only accepts classes annotated with `@Job`.
- `JobExtractor` builds a `Mission` by loading optional JSON config from
  `jobs/{config}.json`, applying basic identity, threshold, and timer metadata.
- `Mission.connect(clazz)` creates the proxy singleton and resolves the required
  `@On` method and optional `@Off` method.
- A class with `@Job` but no valid `@On` method is ignored.
- Code-defined jobs are marked `readOnly = true`.
- `JobStoreCode` exposes these jobs from `OCacheJob`; it does not support
  `add`, `update`, or `remove`.
- `JobStoreUnity.initialize()` merges code jobs and extension jobs, then writes
  the merged set into `JobQueue`.

Configuration precedence for static jobs is mostly configuration-first after the
`@Job` shell exists:

- `name`: annotation name, then class name if still empty.
- `type`: config value can pre-fill `Mission.type`; otherwise annotation value.
- `threshold`: config `threshold` first, then annotation `threshold`.
- `duration`: config `duration` first, then annotation `duration`.
- `formula`: used only for `FORMULA` jobs, config `formula` first, then
  annotation `formula`.

## 6. Task Control APIs

MBSE API exposes the job control HTTP surface through `TaskApi` and `TaskActor`:

| HTTP endpoint | EventBus address | Runtime call |
|---|---|---|
| `PUT /api/job/start/{code}` | `JtAddr.Job.START` | `Ux.Job.on().startAsync(code)` |
| `PUT /api/job/stop/{code}` | `JtAddr.Job.STOP` | `Ux.Job.on().stopAsync(code)` |
| `PUT /api/job/resume/{code}` | `JtAddr.Job.RESUME` | `Ux.Job.on().resumeAsync(code)` |
| `GET /api/job/info/status/{namespace}` | `JtAddr.Job.STATUS` | `Ux.Job.on().statusAsync(namespace)` |
| `POST /api/job/info/by/sigma` | `JtAddr.Job.BY_SIGMA` | `JobStub.searchJobs(...)` |
| `GET /api/job/info/mission/:key` | `JtAddr.Job.GET_BY_KEY` | `JobStub.fetchByKey(...)` |
| `PUT /api/job/info/mission/:key` | `JtAddr.Job.UPDATE_BY_KEY` | `JobStub.update(...)` |

The start/stop/resume/status operations control the in-memory runtime pool via
`JobClientImpl` and `JobQueue`:

- `startAsync(code)` fetches the mission from `JobQueue`, chooses an `Agha` by
  `JobType`, starts it, then binds the returned `timerId` to the mission code.
- `stopAsync(code)` resolves the current timer id, marks the mission stopped via
  `JobQueue.stop(timerId)`, and calls `Vertx.cancelTimer(timerId)`.
- `resumeAsync(code)` marks resumable state in `JobQueue` and delegates back to
  `startAsync(code)`.
- `statusAsync(namespace)` returns a JSON snapshot from `JobQueue.status(...)`.
  The current implementation logs the namespace but reports all jobs in the
  runtime pool.

The query/update operations are MBSE API management operations:

- `searchJobs(sigma, body, grouped)` queries `I_JOB`, maps database job rows to
  runtime mission codes with `Jt.jobCode(...)`, then reads current runtime state
  from `JobClient`.
- `fetchByKey(key)` loads an `I_JOB` row and returns the corresponding runtime
  `Mission` view.
- `update(key, data)` splits `job` and `service` payloads, upserts `I_JOB` and
  `I_SERVICE`, then asks `AmbientStub.updateJob(...)` to refresh the dynamic
  job cache.

## 7. Runtime Storage: Static And Dynamic

There are three layers, and they should not be confused:

1. Static scan cache: `OCacheJob`
   - Populated by class scanning.
   - Contains `Mission` objects produced from `@Job`.
   - Read by `JobStoreCode`.

2. Runtime pool: `JobQueue`
   - Process-local in-memory registry.
   - Stores every visible `Mission`, static and dynamic.
   - Tracks active timers through `RUNNING` and `RUNNING_REF`.
   - Is the source used by `JobClientImpl.fetch/start/stop/resume/status`.

3. Extension/persistent storage: `JobStore` implementations
   - `JobStoreUnity` combines code jobs and extension jobs.
   - `JobStoreExtension` delegates to an externally configured store, if one
     exists.
   - Dynamic jobs are editable only if the configured extension store supports
     `add`, `update`, and `remove`.

`JobStoreUnity.fetch()` enforces the split:

- code jobs must be `readOnly = true`;
- stored jobs must be `readOnly = false`;
- `ONCE` jobs still in `STARTING` are normalized to `STOPPED`;
- the merged result is saved back to `JobQueue`.

During execution, `AghaAbstract.moveOn(...)` updates mission status and calls
`store().update(mission)`. With only the default store this mostly updates the
runtime pool. With a real extension store, the extension decides how much state
is persisted.

## 8. Dynamic Jobs With `zero-exmodule-mbseapi`

`zero-exmodule-mbseapi` adds database-backed task definitions above the raw job
runtime. The relevant tables are:

- `I_JOB`: task/job definition, including `CODE`, `TYPE`, `PROXY`,
  `RUN_AT`, `RUN_FORMULA`, `DURATION`, `THRESHOLD`, input/output components,
  `SERVICE_ID`, `SIGMA`, `ACTIVE`, and tenant/app metadata.
- `I_SERVICE`: dynamic service definition referenced by `I_JOB.SERVICE_ID`.

The dynamic conversion path is:

```text
MDMBSEApiActor
-> ServiceEnvironment init
-> ServiceEnvironment.jobs()
-> JtHypnos.fetch()
-> JtJob.toJob()
-> Mission
-> JobStoreUnity / JobQueue
```

Key points:

- `JtHypnos` is the MBSE API `JobStore` implementation. It reads
  `ServiceEnvironment.jobs()` and converts each `JtJob` to a `Mission`.
- `JtJob.toJob()` combines an `I_JOB` row and its `I_SERVICE` row.
- Dynamic jobs are marked `readOnly = false`.
- The runtime mission code is produced by `Jt.jobCode(job)`, not simply by raw
  `I_JOB.CODE`.
- Dynamic job type defaults to `ONCE` if `I_JOB.TYPE` is absent or invalid.
- Dynamic timeout is built from `I_JOB.THRESHOLD`.
- Dynamic scheduler is built from `I_JOB.RUN_FORMULA`, `I_JOB.RUN_AT`, and
  `I_JOB.DURATION`.
- Dynamic proxy class comes from `I_JOB.PROXY`; that class must provide a valid
  `@On` method. `JtThanatos` is the standard MBSE dynamic job proxy pattern.

Importing `zero-exmodule-mbseapi` is not enough by itself to make `I_JOB` rows
visible to the scheduler. The job store must be configured to use
`io.zerows.extension.module.mbseapi.component.JtHypnos` or an equivalent
`JobStore` implementation:

```yaml
job:
  enabled: true
  store:
    component: io.zerows.extension.module.mbseapi.component.JtHypnos
```

If no store component is configured, `JobConfig.createStore()` falls back to
`JobStoreUnity`, and dynamic storage is only available if `JobStoreExtension`
can resolve a real extension store through the runtime configuration.

## 9. How To Define The Scheduler

The scheduler has two separate meanings:

- container scheduler: `ZeroScheduler`, the worker verticle that starts all
  visible missions;
- mission scheduler: `KScheduler`, the per-job timer definition.

### Container scheduler

`ZeroScheduler` starts only when `JobActor.ofConfig()` returns a `JobConfig`.
It fetches missions from `JobActor.ofStore()`, selects an `Agha` for each
mission by `JobType`, and starts every non-`ONCE` mission.

`ONCE` jobs are not started automatically by `ZeroScheduler`; they are meant to
be started explicitly through `JobClient` or a control API.

### Mission scheduler

`KScheduler` is attached to each timed `Mission`.

For code-defined jobs, `JobExtractor.configureTimer(...)` creates it from:

- annotation or config `duration`;
- annotation or config `formula` for `FORMULA` jobs.

For MBSE dynamic jobs, `JtJob.setTimer(...)` creates it from:

- `I_JOB.RUN_FORMULA`;
- `I_JOB.RUN_AT`;
- `I_JOB.DURATION`.

For `ONCE` jobs, MBSE dynamic jobs intentionally do not attach a scheduler.

### Interval implementation

The interval implementation is selected from `job.interval.component`. If it is
absent, `JobConfig.getInterval()` defaults to `JobIntervalVertx`.

`JobIntervalVertx` uses Vert.x timers:

- `startAt(actuator, null)` runs once using a minimal timer delay.
- `startAt(actuator, scheduler)` calculates first run time and then starts a
  periodic timer using `KScheduler.waitDuration()`.
- `restartAt(actuator, scheduler)` schedules the next formula-driven execution
  using `KScheduler.waitUntil()`.
- The timer id is passed back through `JobInterval.bind(...)` so `JobQueue` and
  `JobClient.stopAsync(...)` can cancel it later.

`Agha` maps `JobType` to execution behavior:

- `FIXED` -> `AghaFixed`: starts a periodic fixed job.
- `FORMULA` -> `AghaFormula`: repeatedly computes the next formula run.
- `ONCE` -> `AghaOnce`: one-shot execution when explicitly started.

## 10. Development Modes: Code, Configurable, Dynamic

Zero job development has three practical layers. Choose the layer before adding
controller APIs, because each layer has a different identity, storage, and
restart model.

### Hard-coded jobs

Use this when the executable behavior is framework or application code and the
job definition should not be user-editable.

Implementation pattern:

```java
@Job(value = EmService.JobType.FIXED, name = "my-job", duration = "30s")
public class MyJob {
    @On
    public Future<Boolean> run(final Long timerId) {
        // job logic
    }

    @Off
    public Future<Boolean> stop(final Long timerId) {
        // optional cleanup
    }
}
```

Rules:

- The class must be visible to Zero class scanning and must have exactly one
  usable `@On` method. Without `@On`, `JobExtractor` returns `null`.
- Use `@Off` only when explicit stop/cleanup behavior is needed.
- Runtime identity defaults to `vertx.zero.jobs-{mission.name}` when no config
  overrides `code`.
- `readOnly` is always `true`. The MBSE job edit page should not allow editing
  these definitions.
- `ONCE` jobs are registered but not auto-started by `ZeroScheduler`.
- `FIXED` and `FORMULA` jobs are auto-start candidates when `job.enabled` is
  active and the mission is not `ONCE`.

### Configurable code jobs

Use this when the executable class is hard-coded but identity, timer, metadata,
or timeout should come from a packaged JSON file.

Implementation pattern:

```java
@Job(value = EmService.JobType.FORMULA, config = "my-job")
public class MyConfigurableJob {
    @On
    public Future<Boolean> run(final Long timerId) {
        // job logic
    }
}
```

The config is resolved under `jobs/{config}.json`. The current extractor reads
JSON into `Mission`, then applies annotation defaults. Effective precedence:

- `name`: annotation name wins, then class name if still empty.
- `type`: config may pre-fill `Mission.type`; otherwise annotation value is
  used.
- `threshold`: config `threshold`, then annotation `threshold`.
- `duration`: config `duration`, then annotation `duration`.
- `formula`: config `formula`, then annotation `formula`, but only when the
  effective job type is `FORMULA`.

Use this layer for repeatable application tasks whose schedule differs by
deployment. Do not use it for user-created jobs, because `JobStoreCode` is
read-only and does not implement `add`, `update`, or `remove`.

### Dynamic MBSE jobs

Use this when `I_JOB` and `I_SERVICE` should own the definition and the job can
be managed through MBSE APIs/UI.

Required parts:

- `zero-exmodule-mbseapi` must be loaded.
- `job.store.component` must point to
  `io.zerows.extension.module.mbseapi.component.JtHypnos` or an equivalent
  `JobStore`.
- `I_JOB.PROXY` must resolve to a concrete Java class with a valid `@On` method.
- `I_JOB.TYPE` must be a current `EmService.JobType`: `ONCE`, `FIXED`, or
  `FORMULA`.
- `I_JOB.SERVICE_ID` must reference a valid `I_SERVICE` row.

Dynamic jobs are converted by `JtJob.toJob()`:

- `I_JOB.CODE` is not used directly as the runtime key. The runtime code is
  produced by `Jt.jobCode(job)`.
- `I_JOB.THRESHOLD` is interpreted as minutes and becomes `Mission.threshold`.
- `I_JOB.DURATION` is interpreted as minutes and becomes `KScheduler.duration`.
- `I_JOB.RUN_FORMULA` and `I_JOB.RUN_AT` configure formula/first-run behavior.
- `ONCE` jobs intentionally do not attach a scheduler.

Current caveat: `JtHypnos.fetch()` is implemented, but `add`, `update`,
`remove`, and `fetch(name)` are still TODO/null. `JobService.update(...)`
upserts `I_JOB` and `I_SERVICE` and calls `AmbientStub.updateJob(...)`, but do
not assume this restarts or cancels already-running timers unless the runtime
refresh path is verified in source and logs.

### About PLAN

Older comments and zero-ui cab files still use `PLAN` to mean a polling or
planned job. The current backend enum is `EmService.JobType.ONCE/FIXED/FORMULA`;
there is no backend `PLAN` enum value. When implementing new backend code:

- use `FIXED` for interval-based polling;
- use `FORMULA` for formula/run-at based planned execution;
- treat UI `PLAN` labels as legacy wording unless the UI mapping is updated;
- never persist `PLAN` into `I_JOB.TYPE` unless backend enum support is added.

## 11. Task Controller Development Playbook

Use the existing MBSE task controller surface before creating new routes.

Backend chain:

```text
TaskApi @EndPoint
-> @Address(...)
-> TaskActor @Queue
-> Ux.Job.on() or JobStub
-> JobClientImpl / JobQueue or JobService / DB
```

Runtime-only control endpoints:

- `PUT /api/job/start/{code}` starts by runtime mission code or short code.
- `PUT /api/job/stop/{code}` cancels the active timer and marks the mission
  stopped when the timer reference exists.
- `PUT /api/job/resume/{code}` moves resumable jobs toward `READY` and delegates
  to start.
- `GET /api/job/info/status/{namespace}` returns `JobQueue.status(...)`. The
  current implementation accepts `namespace` but reports all jobs in the
  process-local runtime pool.

DB-backed management endpoints:

- `POST /api/job/info/by/sigma?group=true|false` reads `X-Sigma`, applies body
  criteria through `Ir`, queries `I_JOB`, maps DB rows to runtime codes, then
  merges runtime `Mission` status from `JobClient`.
- `GET /api/job/info/mission/:key` loads the `I_JOB` row by key and returns the
  corresponding runtime `Mission` view.
- `PUT /api/job/info/mission/:key` upserts `I_JOB` plus nested `service`, then
  updates MBSE ambient job cache.

Parameter rules:

- `code` is a runtime operation key. zero-ui currently sends `record.opKey`,
  which is filled from `mission.code`.
- `key` is the `I_JOB` primary key in MBSE management APIs.
- `sigma` comes from `X-Sigma` on `fetchJobs(...)`.
- `grouped` is query parameter `group`, default `false`.
- search body must be a criteria/pager payload compatible with `Ir.create(...)`.

RBAC/resource rules:

- Existing resources live under
  `zero-exmodule-mbseapi-domain/src/main/resources/plugins/zero-exmodule-mbseapi/security/RBAC_RESOURCE/动态建模/MBSE核心/任务控制/`.
- Existing resource files already cover status, list, read, update, start,
  stop, and resume for the current `TaskApi` routes.
- If adding routes, add all four pieces together: `@EndPoint` method,
  `@Address`, `@Queue` handler, and matching RBAC resource/PERM entry.
- Keep route placeholders consistent with current resources:
  `$code`, `$key`, and `$namespace`.

Persistence and lifecycle cautions:

- `JobQueue` is the runtime status source. It is process-local and tracks
  `RUNNING` timer ids separately from mission definitions.
- `I_JOB`/`I_SERVICE` store definition/configuration, not guaranteed live timer
  state.
- Updating a job definition is not the same as restarting the running timer.
  Stop/start explicitly if a controller workflow must apply a changed schedule.
- A stored job without a resolvable proxy class and `@On` method cannot become a
  runnable `Mission`.
- Static code jobs are read-only. Controllers should reject or hide edit actions
  for `readOnly=true`.

Controller implementation checklist:

- Decide the target layer: hard-coded, configurable code job, or dynamic MBSE
  job.
- Use `TaskApi` routes unless the desired operation is genuinely absent.
- For runtime operations, pass `mission.code`/`opKey`, not `I_JOB.ID`.
- For edit/read operations, pass `I_JOB.ID`/`key`, not the runtime code.
- Verify the job store component before debugging missing dynamic jobs.
- Verify `I_JOB.TYPE` is one of `ONCE`, `FIXED`, `FORMULA`.
- Verify `I_JOB.PROXY` loads and exposes `@On`.
- Verify `TaskActor` has a matching `@Address` for every new endpoint.
- Verify RBAC resources are imported; a missing RBAC action can surface as
  request-operation missing errors even when the route exists.

Verification checklist:

- `GET /api/job/info/status/{namespace}` returns the mission and expected timer.
- `POST /api/job/info/by/sigma?group=true` returns `list`, `count`, and
  optional `aggregation`.
- `GET /api/job/info/mission/:key` returns a normalized dynamic mission.
- `PUT /api/job/start/{code}`, `stop`, and `resume` change `JobQueue` status as
  expected.
- DB rows exist in `I_JOB` and `I_SERVICE`, with matching `SERVICE_ID`.
- Logs confirm `JtHypnos.fetch()` loaded dynamic jobs during startup.
- For schedule changes, explicitly test whether an already-running timer was
  replaced or whether stop/start is required.

## 12. zero-ui Notes

`zero-ecotope` also contains `zero-ui`. Existing job UI anchors:

- `zero-ui/src/extension/components/system/job/` is the current system job
  management component.
- `zero-ui/src/cab/cn/extension/system/job/` owns the Chinese cab/config for
  the system job screen.
- `zero-ui/src/extension/library/interface.ajax.js` exposes `Ex.I.mission`,
  `Ex.I.jobs`, `Ex.I.jobStart`, `Ex.I.jobStop`, and `Ex.I.jobResume`.
- `zero-ui/src/utter/variant-api/i.job.*` contains API adapter mappings for
  the job endpoints.
- Mock/menu entries include `/system/job`, `/job/schedule`, `/job/dashboard`,
  `/job/management`, and `/job/integration`.

Current UI behavior to remember:

- Runtime control buttons call start/stop/resume with `record.opKey`, which is
  populated from `mission.code`.
- Editing calls `/api/job/info/mission/:key` with `record.key`, which is the
  `I_JOB` key.
- The UI cab still labels a `PLAN` type. Backend work should treat that as a
  legacy display/mapping issue and verify whether the UI needs migration to
  `FORMULA` or `FIXED`.

## 13. Graph-Assisted Evidence

Code-review graph can help orient the job subsystem, but this guide is
source-verified.

Observed graph context for `zero-ecotope`:

- graph size: 80,588 nodes and 440,911 edges across 3,948 files;
- reported risk for this documentation-only change: low;
- graph search was useful for repository scale/risk orientation;
- graph semantic/keyword lookup did not reliably find all task-control classes,
  so EventBus/DPA chains must be verified from source files.

For job-controller work, prefer source anchors:

- backend API chain: `TaskApi`, `TaskActor`, `JobStub`, `JobService`, `JobKit`;
- runtime chain: `JobClientImpl`, `JobQueue`, `ZeroScheduler`, `Agha*`;
- model chain: `Mission`, `KScheduler`, `EmService.JobType`;
- dynamic chain: `JtHypnos`, `JtJob`, `JtThanatos`, `I_JOB`, `I_SERVICE`;
- frontend chain: `zero-ui/src/extension/components/system/job`,
  `zero-ui/src/extension/library/interface.ajax.js`,
  `zero-ui/src/utter/variant-api/i.job.*`.

## 14. AI Agent Rules

- When a task mentions scheduling or mission lifecycle, inspect `JobExtractor`,
  `KScheduler`, `Agha*`, and `JobIntervalVertx` before changing API code.
- Treat `Mission` as runtime truth and MBSE job APIs as consumer-side management surfaces.
- Do not confuse `@Job` tasks with `@Actor` boot modules.
- Do not assume dynamic MBSE jobs are active just because `zero-exmodule-mbseapi`
  is on the classpath. Verify the configured `job.store.component`.
- Do not write `PLAN` as a backend job type unless `EmService.JobType` has been
  extended and the full scheduler path supports it.
- For database-backed dynamic jobs, inspect `I_JOB`, `I_SERVICE`, `JtHypnos`,
  `JtJob`, and `JtThanatos`.
- For task control behavior, inspect `TaskApi`, `TaskActor`, `JobClientImpl`,
  and `JobQueue`.
- For controller work, verify RBAC resources under the MBSE task-control
  resource directory before diagnosing the route itself.
