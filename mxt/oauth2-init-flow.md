# OAuth2 Initialization Flow

> Load this file when the task is about OAuth2 startup, registered-client bootstrap, or the Flyway dependency chain for OAuth2 schema initialization.

## 1. Owner

- Framework owner: `zero-plugins-oauth2`

## 2. Key Principle

Database bootstrap for OAuth2 is not performed by an OAuth2 actor.

It is contributed through:

```text
OAuth2Flyway -> DBFlyway
```

This means the OAuth2 schema path enters Flyway before OAuth2 actor startup.

Rule:

```text
OAuth2 schema bootstrap is a Flyway concern first and an OAuth2 actor concern second.
```

## 3. Startup Order

Relevant actors and sequence values:

- `FlywayActor` = `-216`
- `OAuth2ServerActor` = `-160`
- `OAuth2ClientActor` = default `-1` from `@Actor.sequence()`

All of them are pre-module actors because all sequence values are negative.

Therefore:

```text
Flyway bootstrap
  -> OAuth2 server config bootstrap
  -> OAuth2 client registration bootstrap
```

## 4. Flyway Dependency

`OAuth2Flyway` implements `DBFlyway` and contributes:

```text
classpath:database/oauth2/{dbType}/
```

So OAuth2 schema creation depends on:

- Flyway actor startup
- correct database type detection
- correct SPI registration of `DBFlyway`
- actual SQL files under `database/oauth2/{dbType}/`

## 5. Runtime Initialization

`OAuth2ServerActor`:

- loads OAuth2 config into `OAuth2Manager`
- stores security-related runtime config and optional keystore metadata

`OAuth2ClientActor`:

- reads `registration` config from `OAuth2Manager`
- builds `Oauth2RegisteredClient` rows
- diffs stored clients by `clientId`
- inserts or updates registered clients in the database

Verified implementation anchors:

- `OAuth2Flyway.waitFlyway(...)` contributes `classpath:database/oauth2/{dbType}/`
- `src/main/resources/META-INF/services/io.zerows.epoch.store.DBFlyway` registers `io.zerows.plugins.oauth2.OAuth2Flyway`
- `OAuth2ClientActor.saveAsync(...)` persists into `Oauth2RegisteredClientDao`

## 6. Required Assumption

`OAuth2ClientActor` assumes the OAuth2 tables already exist.

If tables are missing, the problem is usually upstream in Flyway bootstrap, not in client registration logic.

## 7. Debug Order

If OAuth2 bootstrap fails, inspect in this order:

1. Flyway execution
2. `DBFlyway` SPI registration
3. effective database type and resolved migration locations
4. SQL tree under `database/oauth2/{dbType}/`
5. OAuth2 config loading into `OAuth2Manager`
6. client registration diff and persistence

## 8. Agent Rules

- Do not debug OAuth2 startup from actor code alone.
- Do not assume OAuth2 SQL paths are loaded just because the module is present.
- Always verify `META-INF/services/io.zerows.epoch.store.DBFlyway`.
- Always verify the final resolved `registration` config before changing persistence code.
- If client persistence fails, prove schema presence first.
