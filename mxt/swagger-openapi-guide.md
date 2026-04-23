# Swagger OpenAPI Guide

> Load this file when the task is about Swagger, OpenAPI, Knife4j, API documentation mounting, or `zero-plugins-swagger`.

## 1. Scope

This file owns `zero-plugins-swagger`.

It owns:

- Swagger/OpenAPI plugin capability
- API documentation mounting
- Swagger UI / Knife4j static resource exposure
- OpenAPI registry and config anchors

It does not own:

- API business behavior
- generated endpoint semantics
- RBAC permission mapping for APIs

## 2. Owning Module

- `zero-plugins-equip/zero-plugins-swagger`

Verified graph/source anchors:

- `SwaggerActor`
- `SwaggerAnalyzer`
- `SwaggerAxis`
- `SwaggerAxisFactory`
- `SwaggerConfig`
- `SwaggerManager`
- `swagger-ui/`

## 3. Responsibility Model

Swagger is a plugin capability for exposing and serving API documentation.

It should answer:

- whether API docs are enabled
- how OpenAPI metadata is mounted
- how Swagger UI or Knife4j static assets are served

It should not answer:

- whether an API should exist
- how a business endpoint behaves
- how permissions bind to an API

## 4. AI Agent Rules

- Start from `SwaggerActor` for boot and config behavior.
- Use `SwaggerAxis` when the issue is static documentation route mounting.
- Use API owner documents when the issue is endpoint behavior, not documentation exposure.
