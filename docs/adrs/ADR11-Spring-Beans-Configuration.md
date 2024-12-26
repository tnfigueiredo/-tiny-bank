# ADR 11: Spring Beans Configuration

## Context and Problem Statement

In order to allow the customization for repositories, it was necessary to find a way to customize repositories configuration.

## Decision Outcome

It was decided to use a Bean Configuration class in order to create the repositories classes instead of Repositories linked 
to database configurations.