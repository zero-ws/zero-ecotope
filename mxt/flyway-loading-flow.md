# Flyway Loading Flow

> Load this file when the task is about Flyway startup, migration location assembly, or framework migration extension points.

## 1. Owner

- Framework owner: `zero-plugins-flyway`
- Main entry class: `io.zerows.plugins.flyway.FlywayActor`
- Config builder: `io.zerows.plugins.flyway.Flyway11Configurator`
- SPI contract: `io.zerows.epoch.store.DBFlyway`

## 2. Startup Position

- `FlywayActor` is annotated with `@Actor(value = "FLYWAY", sequence = -216)`.
- Because the sequence is negative, it runs in the pre-module actor phase.
- This makes Flyway a prerequisite bootstrap actor, not a late extension actor.

Rule:

```text
Treat Flyway as a prerequisite actor. Debug later plugin failures against Flyway first when schema state is involved.
```

## 3. Config Resolution

The actor key is `FLYWAY`.

Config is resolved through Zero actor boot lookup using the actor annotation value.

If `disabled = true`, the actor exits without running migrations.

## 4. Configuration Assembly

`Flyway11Configurator` builds the final `FluentConfiguration`.

Data source resolution order:

1. explicit `url` / `user` / `password`
2. fallback to `DBSActor` using:
   - the default database, or
   - the configured named database

## 5. Location Assembly

Migration locations are assembled from two sources:

1. configured `locations`
2. dynamic `DBFlyway` contributions

`DBFlyway` contributions are loaded through:

```text
HPI.findMany(DBFlyway.class)
```

This means migration path extension is SPI-driven, not hardcoded in `FlywayActor`.

Resolved runtime database type comes from:

```text
ENV.of().get(EnvironmentVariable.DB_TYPE)
```

## 6. SPI Contract

`DBFlyway` implementations are registered through:

```text
META-INF/services/io.zerows.epoch.store.DBFlyway
```

Use `DBFlyway` when a plugin or module must append database-specific migration trees.

Do not patch `FlywayActor` just to add extra SQL locations.

Verified implementation anchors:

- `zero-plugins-oauth2/src/main/resources/META-INF/services/io.zerows.epoch.store.DBFlyway`
- `zero-boot-cloud-actor/src/main/resources/META-INF/services/io.zerows.epoch.store.DBFlyway`
- `zero-exmodule-lbs-provider/src/main/resources/META-INF/services/io.zerows.epoch.store.DBFlyway`

## 7. Execution Path

Final execution path:

```text
FlywayActor
  -> Flyway11Configurator.from(config)
  -> new Flyway(configuration)
  -> migrate()
```

## 8. Agent Rules

- If Flyway is not picking up module SQL, inspect `DBFlyway` registration first.
- If migrations point at the wrong database, inspect data source resolution before touching SQL.
- If a plugin depends on schema bootstrap, confirm its `DBFlyway` registration before debugging plugin actors.
- Do not diagnose migration loading from `pom.xml` alone. Verify runtime SPI registration and resolved locations.
