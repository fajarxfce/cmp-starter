# Testing

Use `kotlin.test`, `kotlinx-coroutines-test`, and Turbine.

Recommended test coverage:

- Reducers: pure state transitions.
- Use cases: business rules such as trimming and validation.
- Repositories: fake local/remote data sources and sync behavior.
- ViewModels: intent handling, state flow, and shared-flow effects.
- Navigation: back-stack reducer actions.
- Auth: reducer transitions, use case input normalization, token persistence.
- Network: HTTP status to domain-safe network error mapping.

Run:

```shell
./gradlew allTests
./gradlew :feature:todo:allTests
```
