s# CMP Starter Agent Guide

This file is mandatory context for agents working in this repository.

## Project shape

- `composeApp` is the application shell only: platform entry points, Koin startup, route wiring, and app-level composition.
- `core:*` modules contain reusable infrastructure only. They must not know feature endpoints, feature DTOs, or feature business rules.
- `feature:*` modules own their UI, data layer, API services, repositories, use cases, contracts, and tests.
- `build-logic` owns Gradle conventions. Module build files should apply `alias(libs.plugins.cmp...)` conventions, not raw plugin IDs.

## Network architecture rules

- `core:network` contains only generic HTTP primitives: `ApiClient`, `ApiRequest`, `ApiResponse`, `HttpMethod`, `NetworkError`, `NetworkJson`, proxy/token wiring, and base envelope models.
- Never place feature endpoint paths or feature DTOs in `core:network`.
- Endpoint paths live inside the owning feature API service as private implementation details.
- API services return raw parsed DTOs or raw response values. They do not return `Try` and they do not catch errors.
- Generic HTTP helpers such as `requestRaw` or `executeOrThrow` belong in `core:network` as `ApiClient` extensions. Feature API services may reuse those helpers, but must keep endpoint paths private inside the feature.
- Repositories are the error boundary. Repository methods call `runOperationCatching { ... }`, call API services/DAOs, map DTOs to domain models, and return `Try<DomainOrValue, Throwable>`.
- Do not create repository-specific result wrappers such as `PosResult`, `AuthResult`, or `FeatureResult` when `Try` is enough.
- Do not add mapper/helper methods like `toPosResult`, `toPosError`, generic `requestRaw`, or generic `execute` inside concrete repositories or feature API services.
- Bearer tokens are injected by `BearerTokenProvider` in `core:network`, backed by `SessionPreferences`.
- Proxy behavior is flavor-driven. `devProxy` and `prodProxy` force Ktor through the configured HTTP proxy for Burp/debugging.

## Data and persistence rules

- `core:datastore` owns key-value/session storage abstractions.
- App runtime should use persistent `KeyValueStore` implementations by default. Tests can use `InMemoryKeyValueStore`.
- Session state belongs in `SessionPreferences`: access token and active store id.
- After successful login/register, fetch the user's stores through the owning feature data service and persist the first active store id when available.

## UI and navigation rules

- Feature screens live in their feature modules, not in `composeApp`.
- UI copy must be user-facing. Do not show endpoint names, HTTP methods, payload wording, Postman wording, or API implementation details in screens.
- Bottom navigation is tab selection, not page push. Selecting a bottom tab replaces the selected home route; back should not walk through previously selected tabs.
- `composeApp` should wire routes and state only. It should not contain feature screen implementations.

## MVI rules

- Base MVI primitives live in `core:common/mvi`.
- ViewModels may delegate to `MVI<UiState, UiAction, UiEffect> by mvi(initialState)`.
- Composables render state and emit actions. Business/data logic stays in ViewModels/use cases/repositories/API services.

## Gradle rules

- Use version-catalog plugin aliases such as `alias(libs.plugins.cmpFeature)`.
- Do not use raw plugin strings in app/core/feature/shared build files.
- Repeated dependencies belong in convention plugins.

## Verification expectations

Before handing off meaningful changes, run the smallest relevant set plus app compile. Common commands:

```shell
./gradlew :composeApp:compileKotlinMetadata :composeApp:compileDevDebugKotlinAndroid --quiet
./gradlew :feature:pos:desktopTest :core:network:desktopTest :composeApp:desktopTest --quiet
```

For proxy work also run:

```shell
./gradlew :composeApp:compileDevProxyDebugKotlinAndroid :composeApp:assembleDevProxyDebug --quiet
```
