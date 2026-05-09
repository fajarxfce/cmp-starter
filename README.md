# CMP Starter

Production-oriented Kotlin Compose Multiplatform starter for Android, iOS, and Desktop JVM.

## What is included

- Clean Architecture + MVI sample in `feature:todo`
- Local-first repository with in-memory SQLDelight-ready data source boundary
- KMP modules under `core:*`, `feature:*`, and `shared`
- Compose Multiplatform design system with cloud-dashboard inspired theme
- Strongly typed navigation model inspired by Navigation 3 `NavKey`/`NavDisplay`
- Koin-ready graph boundaries with constructor-injected business classes
- Android build flavors: `dev`, `staging`, `prod`
- TDD-friendly tests for reducer, use case, and repository
- Real included `build-logic` convention plugins for KMP, Compose, feature, serialization, test, Android, flavors, Koin, and extension plugin aliases

## Commands

```shell
./gradlew projects
./gradlew :composeApp:assembleDevDebug
./gradlew :feature:todo:allTests
./gradlew allTests
./gradlew :composeApp:run
```

## Module map

- `composeApp` wires app UI and platform entry points.
- `core:common` contains config, dispatchers, and domain errors.
- `core:design-system` contains theme, spacing, cards, and reusable UI states.
- `core:navigation` contains typed routes, back stack, reducer, and feature-entry API.
- `core:model` contains shared domain models.
- `core:network` contains network error and network monitor abstractions.
- `core:database` contains SQLDelight-ready database driver and sync operation contracts.
- `core:datastore` contains key-value and secure-storage abstractions.
- `core:analytics` contains analytics tracker composition.
- `core:feature-flag` contains static and mutable feature flag providers.
- `core:sync` contains sync status and sync manager contracts.
- `core:testing` contains fake repositories, fake monitors, dispatcher providers, and fixtures.
- `feature:todo` demonstrates the full architecture.
- `feature:auth` demonstrates session, token persistence, auth MVI contracts, and auth use cases.

## MVI flow

User action -> `TodoIntent` -> `TodoViewModel` -> use case -> repository -> local/remote data source -> `TodoPartialChange` -> `TodoReducer` -> immutable `TodoState` -> Compose UI.

Composables render state and emit intents only. Business logic stays in use cases, repositories, sync managers, and reducers.

## Local-first data flow

The Todo repository writes to the local data source first, queues sync operations with idempotency keys, and exposes local data as `Flow`. `TodoSyncManager` pushes pending operations to a remote data source and refreshes local state from remote data. The current starter uses in-memory implementations so tests are fast; the interfaces are shaped for SQLDelight replacement.

## Build flavors

Android flavors are configured in `composeApp`:

- `dev`: mock mode on, development API URL, `.dev` suffix
- `staging`: staging API URL, `.staging` suffix
- `prod`: production API URL

Common code uses `AppConfig` from `core:common`; platform generated config can be adapted into that interface.

## Build logic

`build-logic` is an included build and exposes convention plugins including:

- `cmp.kotlin.multiplatform.library`
- `cmp.compose.multiplatform.library`
- `cmp.compose.multiplatform.application`
- `cmp.feature`
- `cmp.mvi.feature`
- `cmp.serialization`
- `cmp.test`
- `cmp.koin`
- `cmp.android.application`
- `cmp.android.library`
- `cmp.flavors`
- `cmp.buildconfig`
- `cmp.sqldelight`
- `cmp.detekt`
- `cmp.ktlint`

Detekt, ktlint, and SQLDelight plugins are exposed as compileable extension points. Add their external plugin artifacts to the build-logic catalog when enabling those tools for strict CI enforcement.

## Adding a feature

1. Create `feature:<name>`.
2. Add `api`, `di`, `domain`, `data`, `presentation`, and `test` packages.
3. Expose only public routes or contracts through `api`.
4. Wire navigation through `core:navigation`; do not depend directly on another feature.
5. Add reducer/use case/repository tests alongside implementation.

## Platform-specific parts

- Android: Activity, flavors, generated BuildConfig, Android resources.
- iOS: `MainViewController` for Compose UI embedding.
- Desktop: `Main.kt` Compose window entry point.

Optional Web/Wasm is intentionally not enabled, but the shared module layout keeps it isolated for later.
