# Biological Network Cross-Repo Handoff

> Cross-repository handoff rules for AI agents moving between `rachel-momo`, `r2mo-spec`, `r2mo-rapid`, and `zero-ecotope`.
> This file owns the transfer contract only.

## 1. Purpose

Use this file when one repository has answered part of the question but another repository owns the next proof step.

One-line rule:

```text
Handoff only after one repository has established something concrete.
```

Default objective:

```text
Finish with one repository if possible, with two repositories if necessary.
```

## 2. Standard Handoff Paths

| From | To | When |
|---|---|---|
| `rachel-momo` | `r2mo-rapid` | versions are known, runtime behavior still needs Spring proof |
| `rachel-momo` | `zero-ecotope` | versions are known, runtime behavior still needs Zero proof |
| `r2mo-spec` | `r2mo-rapid` | shared contract is known, Spring execution still unclear |
| `r2mo-spec` | `zero-ecotope` | shared contract is known, Zero delivery/runtime behavior still unclear |
| `r2mo-rapid` | `r2mo-spec` | runtime references a shared marker/model and meaning must be confirmed |
| `zero-ecotope` | `r2mo-spec` | exmodule/runtime code references shared contracts and meaning must be confirmed |
| `r2mo-rapid` | `rachel-momo` | runtime issue turns out to be version/BOM/plugin governance |
| `zero-ecotope` | `rachel-momo` | runtime issue turns out to be dependency baseline or parent/BOM drift |

## 3. Fixed Handoff Order

Prefer this order:

1. `rachel-momo`
2. `r2mo-spec`
3. `r2mo-rapid`
4. `zero-ecotope`

Reverse handoff is valid only when runtime/source inspection reveals that an earlier layer must be confirmed.

## 3.1 Pairwise Handoff Constraint

Treat handoff as pairwise by default.

This means:

- `A -> B` is the normal pattern
- `A -> B -> C` is an escalation, not the baseline
- `A -> B -> C -> D` should be rare and explicitly justified

Only add another external repository when:

- the current pair cannot prove the answer
- semantic ownership and runtime ownership are still split after the pair is inspected
- governance drift is still unresolved after runtime proof

## 4. Handoff Contract

Every transfer should include:

```text
Current signal:
Current repository:
Established fact:
Unresolved question:
Recommended next repository:
Recommended owner document:
Exact proof anchor already verified:
Graph query already used:
```

## 5. Deep Retrieval in Handoff

Direct `code-review-graph` use is valid during handoff when:

- the next repository is known
- the next unresolved point is structural
- one targeted query can reveal the owner symbol or impact surface

Examples:

- `r2mo-spec` marker is known and the next step is locating all Zero consumers
- Momo governance is known and the next step is locating the Spring module actually applying the governed dependency

Rule:

```text
Use graph to land precisely in the next repository, not to skip the handoff contract.
```

## 6. AOP Handoff Matrix

| Current discovery | Next step |
|---|---|
| AspectJ plugin/dependency issue in Momo | hand off to `r2mo-rapid` or `zero-ecotope` only if runtime advice behavior is also questioned |
| marker or metadata wording mixed with advice behavior | leave `r2mo-spec`, hand off to runtime repo |
| Spring advice referencing shared contracts | hand off from `r2mo-rapid` to `r2mo-spec` for meaning only |
| Zero exmodule advice referencing shared contracts | hand off from `zero-ecotope` to `r2mo-spec` for meaning only |

## 7. Failure Rule

If two repositories both appear to own the same behavior:

1. separate semantic ownership from runtime ownership
2. separate governance ownership from implementation ownership
3. keep only one final owner for each conclusion

## 8. Final Rule

```text
Do not dump broad context into the next repository.
Pass one established fact, one unresolved question, and one recommended owner document.
```

Second rule:

```text
External repository dependencies are optional by default.
Each added repository must solve one unresolved point that the current pair cannot close.
```
