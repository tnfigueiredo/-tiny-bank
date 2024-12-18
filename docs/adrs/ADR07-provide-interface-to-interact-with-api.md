# ADR 07: Provide UI Interface To Interact With App API

## Context and Problem Statement

In order to provide a UI to interact with the API operations it is necessary to choose a solution or 
external tool that will allow this interaction.

## Considered Options

1. Postman tool collection.
2. OpenAPI UI provided by a Gradle dependency.

## Decision Outcome

The option 2 was the chosen one. An integrated UI will reduce the need of having an external tool for 
presentation purpose. Also, any refactor action done will reflect in the application without the need of 
external artifacts' maintenance.