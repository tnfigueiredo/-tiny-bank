# ADR 09: Application Exception Handling

## Context and Problem Statement

In order to simplify exception handling, the application structure needed to simplify excepting handling.

## Decision Outcome

the application structure was a bit different from a common production application.
The ControllerAdvice had some configuration issues, and als the kotlin structure was bases on the runCatching for not being
familiar with the best practices in this scenario. This approach allowed to encapsulate the errors in a RESFful response
structure. Even though, runtime exceptions might scape this approach.