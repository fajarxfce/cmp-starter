# Learnings

## 2026-05-10 Start Work
- Plan path: `.sisyphus/plans/cmp-pos-architecture-setup.md`.
- API contract: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json`.
- Decisions: baseUrl placeholder, strict Navigation 3, TDD strict, datastore only for key-value/session/token.

## 2026-05-10 Research pass
- Official Nav3 samples use `NavKey` + `NavDisplay` + `entryProvider`; Koin integration in `koin-compose-navigation3` favors `koinEntryProvider()` and modular `navigation<...>` declarations.
- In CMP examples, Ktor stays behind repository/service boundaries and engines are split by target (`okhttp`/`darwin`/`cio` depending on module).
- Shared CMP Nav3 samples tend to keep route types in `commonMain`; Android-only samples often rely on `DialogSceneStrategy` and other platform-leaning scene strategies.

## 2026-05-10 Task 1 Gradle foundations
- Compose Multiplatform Navigation 3 resolves with `org.jetbrains.androidx.navigation3:navigation3-ui:1.0.0-alpha05`; do not add a direct `navigation3-runtime` alias for common KMP use because the documented UI artifact supplies common/runtime transitively.
- `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-navigation3` uses the separate Compose Multiplatform lifecycle support version `2.10.0-alpha05`, not the base lifecycle Compose version.
- Ktor 3.3.3, DataStore 1.2.1, Koin Compose/ViewModel 4.1.0, and sentinel `REPLACE_WITH_*_BASE_URL` placeholders compile through the required core tests and dev debug app assemble.

## 2026-05-10 Task 2 API contracts
- Postman contract coverage is easiest to keep stable by centralizing exact route builders in `PosApiPaths` and reusing them in tests for both fixed and `storeId`-parameterized routes.
- `Json { ignoreUnknownKeys = true }` is sufficient for tolerant login/envelope decoding when the backend adds extra fields around `success`, `data`, and `message`.
- The request DTOs match the collection cleanly with nullable `phone`, `tenantId`, `categoryId`, and `productId`, plus numeric money/quantity fields kept as Kotlin numbers instead of strings.


## 2026-05-10 Task 5 datastore session
- `KeyValueSessionPreferences` is the datastore-facing session boundary; access tokens go through the injected secure store, while active store ID uses the regular preferences store.
- Auth logout now clears facade-owned session keys instead of clearing the whole `KeyValueStore`, so future non-session preferences are not wiped accidentally.
- Datastore tests need the `cmp.test` convention plugin for Turbine and `kotlinx.coroutines.test` support.

## 2026-05-10 Task 4 Ktor network
- `KtorApiClient` can stay behind the existing `ApiClient` interface while keeping MockEngine access internal for `commonTest`; feature code does not need to import Ktor `HttpClient`.
- Ktor `TextContent` carries `ContentType.Application.Json` on the body content rather than necessarily exposing it through request headers in MockEngine assertions.
- Platform engine factories fit cleanly as `expect/actual` functions: Android OkHttp, iOS Darwin, and desktop CIO.

## 2026-05-10 Task 4 cancellation safety
- `KtorApiClient.execute` must rethrow `kotlin.coroutines.cancellation.CancellationException` before mapping ordinary `Exception` failures to `NetworkError.Unknown`; broad `Throwable` catches can swallow cooperative coroutine cancellation.

## 2026-05-10 Task 3 Koin graph
- Core Koin modules now live beside their owned APIs: `networkModule`, `datastoreModule`, and `navigationModule`; feature auth owns `authModule` and the app composes them via `productionAppModules`.
- `productionAppModules(config, additionalModules)` is the intended extension point for Task 6 POS repository bindings, keeping POS repositories out of Task 3.
- Koin 4.1 module verification is JVM-oriented; the app-level verification test lives under `composeApp/src/desktopTest` and combines `verifyAll`, `checkModules`, and direct resolution assertions.

## 2026-05-16 Task 6 POS repositories
- POS data/domain integration lives in the dedicated `feature:pos` module so Todo sample repositories remain unchanged.
- `PosRepositoryImpl` uses `ApiClient.execute(ApiRequest(...))`, centralized `PosApiPaths`, tolerant POS JSON contracts, and `SessionPreferences` for login token plus active store persistence.
- Admin 401 responses map to `PosError.Unauthorized`; store registration without `storeId` or `id` maps to a clear `PosError.MissingData`.

## 2026-05-16 Task 6 Atlas follow-up
- `PosRepositoryImpl.execute` now returns `PosResult<ApiResponse>` directly, removing mutable shared `lastFailure` state and making per-call failures isolated.
- Added a repository regression test that verifies an Unauthorized failure does not leak into a later successful admin call on the same repository instance.

## 2026-05-16 POS Shell ViewModel
- Created `composeApp/src/commonMain/kotlin/com/fajar/kmp/pos/PosShellState.kt`, `PosShellViewModel.kt`, and `composeApp/src/commonTest/kotlin/com/fajar/kmp/pos/PosShellViewModelTest.kt` as the first atomic UI shell state unit.
- `composeApp` already depends on `feature:pos`, so the shell view model can directly consume `PosRepository` and existing `PosResult`/`PosError` contracts.
- Tests that exercise `ViewModel` coroutine launches need an injected test `CoroutineScope`; otherwise `viewModelScope` work may not complete before assertions under common tests.
- Evidence: `./gradlew :composeApp:allTests :composeApp:assembleDevDebug` succeeded. Full output saved at `/home/fajar/.local/share/opencode/tool-output/tool_e2ed50459001TvfR8s1Nx5ryzV`; success lines 1161-1164 show `:composeApp:allTests` and `BUILD SUCCESSFUL in 7s`.

## 2026-05-16 Task 8 UI Migration Wiring
- `App.kt` now uses `koinInject<PosShellViewModel>()` and `collectAsState()` to bridge the POS shell state into the static POS UI screens.
- Existing POS design-system components were sufficient for status/loading rendering; a local `StatusRow` helper in `App.kt` preserves the current card/list visual style.
- `KoinModuleVerificationTest` uses `verifyAll()`, which verifies modules independently. App-level definitions depending on feature-level definitions need those feature modules included in the same module via `includes(...)`, or verification can report missing definitions despite runtime module list ordering.
- Evidence files written: `.sisyphus/evidence/task-8-ui-migration.txt` and `.sisyphus/evidence/task-8-ui-migration-error.txt`.

## 2026-05-16 Task 8 Navigation Bugfix
- `App.kt` submit callbacks should not both start repository work and mutate routes. Route transitions now observe `PosShellState` success strings via `LaunchedEffect`, while failure strings remain rendered on the current auth/store screen.
- Added regression coverage at the `PosShellViewModel` layer for register and store failures because the project currently lacks a Compose UI route assertion setup for `NavDisplay`.
- Evidence refreshed in `.sisyphus/evidence/task-8-ui-migration.txt` and `.sisyphus/evidence/task-8-ui-migration-error.txt`.

## 2026-05-16 Task 9 build hardening
- `InMemoryApiClient` had no remaining references after Ktor production wiring landed; keep the focused `StaticNetworkMonitor` utility because `networkModule` and Ktor tests still use it.
- Task 9 guard scan is clean for feature `HttpClient` imports, stale production app navigation symbols, and obsolete `InMemoryApiClient` references. URL hits are test placeholders or scheme validation only.
