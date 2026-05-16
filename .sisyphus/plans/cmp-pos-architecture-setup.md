# CMP POS-GG Architecture Setup

## TL;DR
> **Summary**: Convert the dummy/manual Compose Multiplatform starter into a POS-GG architecture shell with Koin DI, Ktor networking, key-value/session datastore, strict Navigation 3 routing, and UI wired to repository contracts from the Postman collection.
> **Deliverables**:
> - Koin modules replacing manual graph creation for app/core/features.
> - Ktor-backed API boundary for POS-GG auth/store/catalog/transaction/sync/admin endpoints.
> - Datastore/session module for token, active store, and lightweight preferences.
> - Strict Navigation 3 route model and app shell.
> - POS UI shell for auth, store onboarding, catalog, checkout/transactions, sync, admin.
> - TDD test coverage plus agent-executed QA evidence.
> **Effort**: Large
> **Parallel**: YES - 4 waves
> **Critical Path**: Task 1 → Task 2 → Task 4 → Task 6 → Task 8 → Final Verification

## Context
### Original Request
User asked in Indonesian to set up a dummy Compose Multiplatform project with DI using Koin, network module using Ktor, datastore module, Navigation 3, and then adjust the UI. User later provided the API contract at `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json` and asked to execute; Prometheus mode converts this into an execution plan for `/start-work`.

### Interview Summary
- API source: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json`.
- Base URL: use config placeholders; executor must not hardcode real dev/staging/prod URL.
- Navigation: strict Navigation 3 only; do not keep the custom stack as a runtime fallback.
- Test strategy: TDD strict.
- Datastore: key-value/session/token persistence only; POS entities/offline-first data belongs in database/local-first repository boundaries.
- UI target: POS-GG shell for auth, store setup/selection, catalog, transactions/checkout, sync, and admin placeholders.

### Metis Review (gaps addressed)
- Platform scope defaulted to README scope: Android, iOS, and Desktop JVM.
- API response shapes are not included in the Postman collection; plan requires tolerant serializers and typed request contracts first, with response DTOs derived from observed `success/data` auth token convention and endpoint names.
- Secure storage is required by interface boundary but platform encryption may remain behind `SecureKeyValueStore` wrappers if no secure implementation exists yet.
- Auth lifecycle is limited to register/login/logout/session observation; no refresh endpoint exists in the Postman collection, so refresh must be a no-network no-op or retained existing behavior until backend contract appears.
- Store scope defaults to one active store persisted as `activeStoreId` while UI may show selection-ready structure.
- Sync defaults to minimal push/pull request support with server-authoritative conflict handling; no custom conflict engine in this plan.
- Admin screens are endpoint-backed minimal shells that gracefully display Unauthorized/Forbidden/empty states.

## Work Objectives
### Core Objective
Make the app compile and run as a POS-GG Compose Multiplatform shell backed by explicit DI, network, session, navigation, and repository boundaries.

### Deliverables
- Version catalog and Gradle dependencies for Ktor, Koin Compose/ViewModel where needed, Navigation 3, and platform engines/storage support.
- Ktor `ApiClient` implementation behind the existing `core:network` boundary.
- POS endpoint contracts and repositories for auth, stores, catalog, transactions, sync, and admin.
- Koin modules in core/feature/app modules and one app-level Koin bootstrap.
- Session datastore facade for access token and active store ID.
- Strict Navigation 3 route keys and app navigation display.
- UI refactor from local enum state to ViewModel/repository/navigation-driven flow.

### Definition of Done (verifiable conditions with commands)
- `./gradlew allTests` passes.
- `./gradlew :composeApp:assembleDevDebug` passes.
- `./gradlew :composeApp:run` starts the Desktop app shell without DI/navigation startup crash.
- No feature module imports Ktor `HttpClient` directly.
- No runtime code path uses the old `AppStage` enum for navigation.
- `.sisyphus/evidence/` contains task evidence files for every task.

### Must Have
- TDD RED-GREEN-REFACTOR in every implementation task with repo-supported unit tests.
- Constructor injection remains the business-class pattern; Koin only wires dependencies.
- `HttpClient` stays internal to `core:network` implementation.
- Token/session reads go through `core:datastore` abstractions.
- Navigation uses Navigation 3 route/display concepts; custom reducer may only be deleted or migrated, not used as fallback runtime navigation.
- All network calls use the Postman endpoint paths exactly.

### Must NOT Have
- No hardcoded production/dev base URL.
- No new backend API assumptions beyond the Postman collection.
- No raw `HttpClient` in features.
- No per-screen Koin application startup.
- No POS entity persistence inside key-value datastore.
- No full offline conflict engine, payment integration, receipt printing, inventory mutations, or admin role-management beyond listed endpoints.
- No source edits outside the planned implementation scope.

## Verification Strategy
> ZERO HUMAN INTERVENTION - all verification is agent-executed.
- Test decision: TDD strict using Kotlin test, `kotlinx-coroutines-test`, Turbine, and Koin test where applicable.
- QA policy: Every task has agent-executed scenarios.
- Evidence: `.sisyphus/evidence/task-{N}-{slug}.{ext}`.
- UI QA: use Desktop run or Android debug build smoke via `interactive_bash`; use Playwright only if a browser target is added later.

## Execution Strategy
### Parallel Execution Waves
> Target: 5-8 tasks per wave. This plan has fewer total tasks because architecture dependencies are high; run only tasks marked parallel in each wave.

Wave 1: Task 1 foundations; Task 2 API contracts can begin after Task 1 dependency names are known.
Wave 2: Task 3 DI, Task 4 Ktor, Task 5 datastore/session.
Wave 3: Task 6 POS repositories, Task 7 strict Navigation 3.
Wave 4: Task 8 UI migration, Task 9 cleanup/build hardening.

### Dependency Matrix (full, all tasks)
| Task | Blocks | Blocked By |
|---|---|---|
| 1. Gradle foundations and config placeholders | 2,3,4,5,7,8 | None |
| 2. POS API contracts and DTO tests | 4,6 | 1 |
| 3. Koin module graph | 6,8 | 1 |
| 4. Ktor network implementation | 6 | 1,2 |
| 5. Datastore/session persistence | 6,8 | 1 |
| 6. POS repositories/use cases | 8 | 2,3,4,5 |
| 7. Strict Navigation 3 module | 8 | 1 |
| 8. POS UI migration | 9 | 3,5,6,7 |
| 9. Cleanup and build hardening | Final Verification | 8 |

### Agent Dispatch Summary (wave → task count → categories)
- Wave 1 → 2 tasks → `unspecified-high`, `quick`
- Wave 2 → 3 tasks → `unspecified-high`
- Wave 3 → 2 tasks → `unspecified-high`, `visual-engineering`
- Wave 4 → 2 tasks → `visual-engineering`, `unspecified-high`

## TODOs
> Implementation + Test = ONE task. Never separate.
> EVERY task MUST have: Agent Profile + Parallelization + QA Scenarios.

- [x] 1. Gradle foundations and config placeholders

  **What to do**: RED: add/adjust dependency resolution tests or compile checks that fail due to missing Ktor/Navigation 3/config placeholders. GREEN: update `gradle/libs.versions.toml` with Ktor client core/content-negotiation/json/logging/auth plus platform engines, Koin Compose/ViewModel if needed, and Navigation 3 artifacts compatible with current Kotlin/Compose versions. Update relevant build scripts so `core:network`, `core:datastore`, `core:navigation`, `feature:auth`, and `composeApp` can consume only their needed dependencies. Add config placeholders for base URL per environment without hardcoding real URL.
  **Must NOT do**: Do not change package names. Do not remove existing flavors. Do not hardcode `localhost`, `10.0.2.2`, or production domains.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Gradle/KMP dependency compatibility is cross-module and can break all targets.
  - Skills: [] - No external skill required.
  - Omitted: [`frontend-ui-ux`] - No UI design work in this task.

  **Parallelization**: Can Parallel: NO | Wave 1 | Blocks: 2,3,4,5,7,8 | Blocked By: None

  **References**:
  - Module map: `settings.gradle.kts:35-53` - existing modules to wire.
  - Version catalog: `gradle/libs.versions.toml:12-20`, `gradle/libs.versions.toml:41-48` - existing Compose/Kotlin/Koin/coroutines/test versions.
  - App dependencies: `composeApp/build.gradle.kts:8-19` - app currently only depends on core navigation/design and todo.
  - Network build: `core/network/build.gradle.kts:1-13` - currently no Ktor.
  - Navigation build: `core/navigation/build.gradle.kts:1-12` - currently custom serializable route model only.
  - App flavors support: `build-logic/convention/src/main/kotlin/com/fajar/kmp/gradle/ComposeMultiplatformApplicationConventionPlugin.kt:53-58` - environment flavor dimension exists.
  - External: `https://insert-koin.io/docs/reference/koin-core/kmp-setup` - Koin KMP setup.
  - External: `https://ktor.io/docs/client-create-multiplatform-application.html` - Ktor multiplatform client guidance.

  **Acceptance Criteria**:
  - [ ] `./gradlew :core:network:allTests :core:datastore:allTests :core:navigation:allTests` passes after dependency edits.
  - [ ] `./gradlew :composeApp:assembleDevDebug` fails only if baseUrl placeholder is intentionally unset at runtime, not at compile time.
  - [ ] `gradle/libs.versions.toml` contains no duplicate aliases for the same artifact.

  **QA Scenarios**:
  ```
  Scenario: Gradle dependency graph resolves
    Tool: Bash
    Steps: Run `./gradlew :core:network:dependencies --configuration commonMainImplementationDependenciesMetadata --dry-run` then `./gradlew :composeApp:assembleDevDebug`.
    Expected: Commands complete without dependency resolution errors.
    Evidence: .sisyphus/evidence/task-1-gradle-foundations.txt

  Scenario: Missing base URL is not hardcoded away
    Tool: Bash
    Steps: Search changed files for `10.0.2.2`, `localhost`, and `http://` literals outside tests and config placeholder definitions.
    Expected: No hardcoded runtime base URL appears outside approved placeholder/test files.
    Evidence: .sisyphus/evidence/task-1-gradle-foundations-error.txt
  ```

  **Commit**: YES | Message: `build(cmp): add architecture dependency foundations` | Files: `gradle/libs.versions.toml`, module `build.gradle.kts`, build-logic files if needed

- [x] 2. POS API contracts and DTO tests

  **What to do**: RED: create tests for request serialization and endpoint path generation for every Postman endpoint. GREEN: add typed request DTOs and lightweight response envelopes for auth, store, category, product, transaction, sync, and admin. Use tolerant serializers with nullable/optional fields where response examples are absent. Model auth login/register around `success` and `data.accessToken` because the Postman login test extracts that field. Keep DTOs in network/data packages, not UI.
  **Must NOT do**: Do not invent backend fields not present in request bodies except generic `success`, `data`, `message`, and `error` envelope fields needed for tolerant parsing. Do not implement repository behavior here.

  **Recommended Agent Profile**:
  - Category: `quick` - Reason: Contract files and serialization tests are bounded once dependencies exist.
  - Skills: [] - No special skill required.
  - Omitted: [`frontend-ui-ux`] - No UI work.

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: 4,6 | Blocked By: 1

  **References**:
  - Postman auth register: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:13-37` - request body and path.
  - Postman login token convention: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:42-72` - `data.accessToken` extraction.
  - Store register: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:118-130` - request body and path.
  - Categories: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:162-205` - create/get paths.
  - Products: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:233-276` - create/get paths.
  - Transactions: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:309-352` - create/get paths.
  - Sync/admin: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:385-493` - sync and admin paths.
  - Serialization dependency: `core/network/build.gradle.kts:1-4` - module already applies serialization.

  **Acceptance Criteria**:
  - [ ] Tests assert exact paths: `/api/v1/auth/login`, `/api/v1/stores/{storeId}/products`, etc.
  - [ ] Tests assert login response parser reads `data.accessToken`.
  - [ ] `./gradlew :core:network:allTests` passes.

  **QA Scenarios**:
  ```
  Scenario: Endpoint contract tests pass
    Tool: Bash
    Steps: Run `./gradlew :core:network:allTests`.
    Expected: All contract serialization and path tests pass.
    Evidence: .sisyphus/evidence/task-2-api-contracts.txt

  Scenario: Unknown response fields are tolerated
    Tool: Bash
    Steps: Run the test case that decodes a response envelope containing extra fields not modeled by DTOs.
    Expected: Decoder succeeds and expected known fields remain available.
    Evidence: .sisyphus/evidence/task-2-api-contracts-error.txt
  ```

  **Commit**: YES | Message: `test(network): lock pos api contracts` | Files: `core/network/src/commonMain/**`, `core/network/src/commonTest/**`

- [x] 3. Koin module graph migration

  **What to do**: RED: write Koin verification tests that fail until modules can resolve `AuthViewModel`, network clients, session store, and POS repositories without manual graph classes. GREEN: add Koin modules for `core:network`, `core:datastore`, `core:navigation`, `feature:auth`, and app-level composition. Replace manual `AuthGraph` and any `TodoGraph`-style wiring with Koin module functions while preserving constructor injection. Start Koin exactly once from app bootstrap.
  **Must NOT do**: Do not call `startKoin` inside composable screen bodies or per navigation destination. Do not make service locator globals outside Koin.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Cross-module DI wiring affects startup, tests, and feature boundaries.
  - Skills: [] - No special skill required.
  - Omitted: [`frontend-ui-ux`] - No UI design in this task.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 6,8 | Blocked By: 1

  **References**:
  - Existing Koin aliases: `build-logic/convention/src/main/kotlin/com/fajar/kmp/gradle/AliasConventionPlugins.kt:40-49` - Koin core/test plugin pattern.
  - App Koin dependency already present: `build-logic/convention/src/main/kotlin/com/fajar/kmp/gradle/ComposeMultiplatformApplicationConventionPlugin.kt:43-49`.
  - Manual auth graph: `feature/auth/src/commonMain/kotlin/com/fajar/kmp/feature/auth/data/AuthGraph.kt:12-20` - migrate this wiring.
  - Feature auth deps: `feature/auth/build.gradle.kts:8-13` - add network/session dependencies as required.
  - Koin docs: `https://insert-koin.io/docs/reference/koin-core/kmp-setup`.

  **Acceptance Criteria**:
  - [ ] A Koin test verifies all production modules with `checkModules` or equivalent supported Koin 4.x verification.
  - [ ] `AuthGraph` is deleted or deprecated unused; no app code instantiates it directly.
  - [ ] `./gradlew allTests` passes.

  **QA Scenarios**:
  ```
  Scenario: Koin resolves app graph
    Tool: Bash
    Steps: Run `./gradlew allTests` and capture the Koin module verification output.
    Expected: Auth/session/network/navigation bindings resolve without missing definition errors.
    Evidence: .sisyphus/evidence/task-3-koin-graph.txt

  Scenario: No per-screen Koin startup
    Tool: Bash
    Steps: Search changed Kotlin files for `startKoin(` and confirm it appears only in app/bootstrap initialization.
    Expected: `startKoin(` does not appear in screen composables or ViewModels.
    Evidence: .sisyphus/evidence/task-3-koin-graph-error.txt
  ```

  **Commit**: YES | Message: `refactor(di): wire app graph with koin` | Files: `core/**`, `feature/**`, `composeApp/**`

- [x] 4. Ktor-backed network module

  **What to do**: RED: add tests for `ApiClient` success, offline, unauthorized, server error, JSON body encoding, bearer token header injection, and base URL placeholder failure. GREEN: implement a Ktor-backed `ApiClient` behind the existing `ApiClient` interface or an evolved compatible interface. Add platform engine factories in platform source sets. Preserve `InMemoryApiClient` for tests only if useful. Use `ContentNegotiation`, `kotlinx.serialization`, logging in debug-safe form, request timeout, and error mapping through `NetworkErrorMapper`.
  **Must NOT do**: Do not expose Ktor `HttpClient` from `core:network` public feature APIs. Do not add feature-level Ktor dependencies.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Multiplatform engine and error handling are failure-prone.
  - Skills: [] - No special skill required.
  - Omitted: [`frontend-ui-ux`] - No UI work.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 6 | Blocked By: 1,2

  **References**:
  - Existing network boundary: `core/network/src/commonMain/kotlin/com/fajar/kmp/core/network/NetworkModels.kt:14-37`.
  - Existing error mapper: `core/network/src/commonMain/kotlin/com/fajar/kmp/core/network/NetworkModels.kt:39-45`.
  - Existing in-memory client: `core/network/src/commonMain/kotlin/com/fajar/kmp/core/network/InMemoryApiClient.kt:3-23`.
  - Network monitor: `core/network/src/commonMain/kotlin/com/fajar/kmp/core/network/NetworkModels.kt:10-12`.
  - Ktor docs: `https://ktor.io/docs/client-create-multiplatform-application.html`.

  **Acceptance Criteria**:
  - [ ] `core:network` has no API that requires features to import `io.ktor.client.HttpClient`.
  - [ ] Unauthorized HTTP 401 maps to `NetworkError.Unauthorized`.
  - [ ] Offline monitor false returns `NetworkError.Offline` before network execution.
  - [ ] `./gradlew :core:network:allTests` passes.

  **QA Scenarios**:
  ```
  Scenario: Ktor ApiClient maps successful login
    Tool: Bash
    Steps: Run the network test using Ktor MockEngine for `POST /api/v1/auth/login` returning `{ "success": true, "data": { "accessToken": "token-123" } }`.
    Expected: `NetworkResult.Success` and parsed access token `token-123`.
    Evidence: .sisyphus/evidence/task-4-ktor-network.txt

  Scenario: Unauthorized response maps correctly
    Tool: Bash
    Steps: Run the network test using Ktor MockEngine returning HTTP 401 for `GET /api/v1/admin/users`.
    Expected: `NetworkResult.Failure(NetworkError.Unauthorized)`.
    Evidence: .sisyphus/evidence/task-4-ktor-network-error.txt
  ```

  **Commit**: YES | Message: `feat(network): add ktor api client` | Files: `core/network/**`, `gradle/libs.versions.toml`, relevant build scripts

- [x] 5. Datastore/session persistence module

  **What to do**: RED: add tests for token save/observe/clear, active store ID save/observe/clear, logout clearing session keys, and null-safe behavior. GREEN: add a session/preferences facade on top of `KeyValueStore`, wire `SecureKeyValueStore` for token-like secrets, and keep `InMemoryKeyValueStore` for tests. If adding multiplatform settings/datastore dependency is necessary, hide it behind `KeyValueStore` and platform factories.
  **Must NOT do**: Do not store product/category/transaction lists in `KeyValueStore`. Do not log access tokens.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Session handling affects auth, network headers, and logout correctness.
  - Skills: [] - No special skill required.
  - Omitted: [`frontend-ui-ux`] - No UI work.

  **Parallelization**: Can Parallel: YES | Wave 2 | Blocks: 6,8 | Blocked By: 1

  **References**:
  - Key-value abstraction: `core/datastore/src/commonMain/kotlin/com/fajar/kmp/core/datastore/KeyValueStore.kt:7-12`.
  - In-memory implementation: `core/datastore/src/commonMain/kotlin/com/fajar/kmp/core/datastore/KeyValueStore.kt:14-28`.
  - Secure wrapper placeholder: `core/datastore/src/commonMain/kotlin/com/fajar/kmp/core/datastore/KeyValueStore.kt:30`.
  - Auth currently creates secure in-memory store manually: `feature/auth/src/commonMain/kotlin/com/fajar/kmp/feature/auth/data/AuthGraph.kt:12-13`.
  - README boundary: `README.md:35-36` - database vs datastore responsibility.

  **Acceptance Criteria**:
  - [ ] Session store tests pass for token and active store ID lifecycle.
  - [ ] Logout clears token and active store ID.
  - [ ] No POS collection data is persisted via `KeyValueStore`.
  - [ ] `./gradlew :core:datastore:allTests` passes.

  **QA Scenarios**:
  ```
  Scenario: Session survives observer subscription
    Tool: Bash
    Steps: Run datastore session tests using Turbine: observe token, save `abc`, assert observer emits `abc`.
    Expected: Observer emits null then `abc`, then null after clear.
    Evidence: .sisyphus/evidence/task-5-datastore-session.txt

  Scenario: Logout clears active store
    Tool: Bash
    Steps: Run test storing token `abc` and activeStoreId `store-1`, invoke clear/logout, then read both keys.
    Expected: Both reads return null.
    Evidence: .sisyphus/evidence/task-5-datastore-session-error.txt
  ```

  **Commit**: YES | Message: `feat(datastore): add session persistence` | Files: `core/datastore/**`, `feature/auth/**`

- [x] 6. POS repositories and use cases

  **What to do**: RED: add repository/use-case tests for auth login/register, store register, category/product list+create, transaction list+create, sync request, and admin stats/stores/users success/failure paths using fake `ApiClient`. GREEN: implement repository classes that call the Ktor-backed API boundary, persist access token and active store ID through session store, add bearer header via network/session wiring, and expose UI-friendly state/result models. Keep Todo sample intact unless replacing app dependencies requires removing it from the shell.
  **Must NOT do**: Do not implement update/delete endpoints not in Postman. Do not create fake successful admin data if API returns Unauthorized.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: This is the domain/data integration center for the app.
  - Skills: [] - No special skill required.
  - Omitted: [`frontend-ui-ux`] - UI migration is separate.

  **Parallelization**: Can Parallel: NO | Wave 3 | Blocks: 8 | Blocked By: 2,3,4,5

  **References**:
  - Existing repository test pattern: `feature/todo/src/commonTest/kotlin/com/fajar/kmp/feature/todo/TodoRepositoryTest.kt:13-27`.
  - MVI/data flow guidance: `README.md:44-52`.
  - Auth build deps: `feature/auth/build.gradle.kts:8-13`.
  - Login endpoint: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:59-72`.
  - Store endpoint: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:118-130`.
  - Catalog endpoints: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:162-276`.
  - Transaction endpoints: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:309-352`.
  - Sync endpoint: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:385-397`.
  - Admin endpoints: `/home/fajar/IdeaProjects/pos-gg/pos-gg-postman-collection.json:430-493`.

  **Acceptance Criteria**:
  - [ ] Each listed endpoint has at least one repository/use-case test.
  - [ ] Auth login success persists access token.
  - [ ] Store register success persists active store ID if response includes store ID; otherwise exposes a clear missing-data error.
  - [ ] Admin Unauthorized is displayed as an auth/permission state, not crash.
  - [ ] `./gradlew allTests` passes.

  **QA Scenarios**:
  ```
  Scenario: Auth and store happy path
    Tool: Bash
    Steps: Run repository tests with fake responses for login token `token-123` and store register `store-123`.
    Expected: Session store contains token `token-123` and activeStoreId `store-123`.
    Evidence: .sisyphus/evidence/task-6-pos-repositories.txt

  Scenario: Admin unauthorized does not crash
    Tool: Bash
    Steps: Run admin repository test with fake 401 response for `/api/v1/admin/users`.
    Expected: Repository emits Unauthorized/PermissionDenied state and no exception escapes.
    Evidence: .sisyphus/evidence/task-6-pos-repositories-error.txt
  ```

  **Commit**: YES | Message: `feat(data): add pos api repositories` | Files: `feature/**`, `core/model/**`, `core/network/**`, `core/datastore/**`

- [x] 7. Strict Navigation 3 module

  **What to do**: RED: add navigation tests that assert route keys for Splash, Onboarding, Login/Register, StoreSetup, Dashboard, Catalog, Checkout, Sync, Admin, and details where needed. GREEN: replace the custom `AppBackStack`/`NavigationReducer` runtime usage with Navigation 3 route keys/display setup. Keep serializable typed route definitions if compatible, but runtime rendering must use Navigation 3. Wire feature entry registration through DI where practical.
  **Must NOT do**: Do not use Compose Navigation, Voyager, Decompose, or the old reducer as runtime fallback. Do not keep `AppStage` enum navigation in `App.kt`.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Navigation 3 + CMP compatibility is a high-risk architectural change.
  - Skills: [] - No special skill required.
  - Omitted: [`git-master`] - No git history work required.

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: 8 | Blocked By: 1

  **References**:
  - Current custom route model: `core/navigation/src/commonMain/kotlin/com/fajar/kmp/core/navigation/AppRoute.kt:8-19`.
  - Current reducer model: `core/navigation/src/commonMain/kotlin/com/fajar/kmp/core/navigation/AppRoute.kt:21-45`.
  - Current app enum navigation: `composeApp/src/commonMain/kotlin/com/fajar/kmp/App.kt:66-109`.
  - README says current nav is only Nav3-inspired: `README.md:10-12`.
  - External: AndroidX Navigation 3 documentation for `NavKey`/`NavDisplay` APIs.

  **Acceptance Criteria**:
  - [ ] `AppStage` and `HomeSection` no longer drive runtime navigation.
  - [ ] Navigation tests verify route start destination and auth-to-store-to-home path.
  - [ ] `./gradlew :core:navigation:allTests :composeApp:assembleDevDebug` passes.

  **QA Scenarios**:
  ```
  Scenario: Auth success navigates to store setup
    Tool: Bash
    Steps: Run navigation unit test dispatching login success route event.
    Expected: Current route/display key becomes StoreSetup or Dashboard if active store exists.
    Evidence: .sisyphus/evidence/task-7-navigation3.txt

  Scenario: Back navigation does not exit root unexpectedly
    Tool: Bash
    Steps: Run navigation test from Catalog then back to Dashboard, then back at Dashboard.
    Expected: First back returns Dashboard; root back is ignored or handled by host without empty stack.
    Evidence: .sisyphus/evidence/task-7-navigation3-error.txt
  ```

  **Commit**: YES | Message: `feat(navigation): migrate shell to navigation3` | Files: `core/navigation/**`, `composeApp/**`, feature entry files

- [x] 8. POS UI migration

  **What to do**: RED: add ViewModel/reducer tests for screen states: unauthenticated, login loading/error, store setup, dashboard loaded, catalog empty/loaded, checkout transaction success/failure, sync loading/error, admin unauthorized. GREEN: refactor `composeApp/src/commonMain/kotlin/com/fajar/kmp/App.kt` from local dummy stage state into a Koin-injected app shell with Navigation 3 destinations and feature ViewModels. Reuse design-system components (`PosButton`, `PosCard`, `PosTextField`, status pills) but drive content from repositories/use cases. Add loading/empty/error states for every API-backed section.
  **Must NOT do**: Do not leave buttons that only change local fake enum state. Do not display fake admin stats as if fetched. Do not add payment processing beyond transaction create payload.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: UI migration requires preserving UX quality while wiring real state.
  - Skills: [] - No special skill required.
  - Omitted: [`git-master`] - No git history work required.

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: 9 | Blocked By: 3,5,6,7

  **References**:
  - Current app shell: `composeApp/src/commonMain/kotlin/com/fajar/kmp/App.kt:70-109`.
  - Current POS labels: `composeApp/src/commonMain/kotlin/com/fajar/kmp/App.kt:66-68`.
  - Design system imports already used: `composeApp/src/commonMain/kotlin/com/fajar/kmp/App.kt:53-63`.
  - App dependency list: `composeApp/build.gradle.kts:10-15` - add required feature modules.
  - README MVI guidance: `README.md:44-48`.

  **Acceptance Criteria**:
  - [ ] Login/register screens call auth ViewModel/use cases.
  - [ ] Store setup persists active store on success.
  - [ ] Catalog and transaction sections render loading, empty, loaded, and error states.
  - [ ] Admin tab renders Unauthorized state for 401.
  - [ ] `./gradlew :composeApp:assembleDevDebug` passes.

  **QA Scenarios**:
  ```
  Scenario: POS shell smoke run
    Tool: interactive_bash
    Steps: Start `./gradlew :composeApp:run` in tmux, verify app launches to onboarding/login flow, capture terminal output after navigation smoke.
    Expected: Desktop app starts without DI/navigation exceptions; visible shell includes POS-GG auth entry.
    Evidence: .sisyphus/evidence/task-8-ui-migration.txt

  Scenario: Login failure state
    Tool: Bash
    Steps: Run ViewModel test with fake auth repository returning Unauthorized for login.
    Expected: UI state contains a user-readable login error and remains on auth route.
    Evidence: .sisyphus/evidence/task-8-ui-migration-error.txt
  ```

  **Commit**: YES | Message: `feat(ui): wire pos shell to architecture` | Files: `composeApp/**`, `feature/**`, `core/design-system/**` only if reusable components need extension

- [x] 9. Cleanup and build hardening

  **What to do**: RED: add guard tests/static checks where practical for forbidden imports and stale dummy navigation. GREEN: remove unused dummy graph code, stale enum navigation, dead imports, and obsolete in-memory-only production wiring. Ensure README commands still work. Keep sample Todo code only if it compiles and no longer drives the main POS shell.
  **Must NOT do**: Do not delete useful sample tests unless replaced by POS tests. Do not broaden scope to CI setup unless requested separately.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Final cleanup spans build, modules, and architecture guardrails.
  - Skills: [] - No special skill required.
  - Omitted: [`frontend-ui-ux`] - UI design should already be done.

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: Final Verification | Blocked By: 8

  **References**:
  - README commands: `README.md:17-24`.
  - README module boundaries: `README.md:27-42`.
  - Current dummy navigation: `composeApp/src/commonMain/kotlin/com/fajar/kmp/App.kt:66-109`.
  - Current in-memory network client: `core/network/src/commonMain/kotlin/com/fajar/kmp/core/network/InMemoryApiClient.kt:3-27`.

  **Acceptance Criteria**:
  - [ ] `./gradlew allTests` passes.
  - [ ] `./gradlew :composeApp:assembleDevDebug` passes.
  - [ ] Search confirms no feature source imports `io.ktor.client.HttpClient`.
  - [ ] Search confirms no production app code references `AppStage`.
  - [ ] Evidence files exist for tasks 1-9.

  **QA Scenarios**:
  ```
  Scenario: Full build and tests
    Tool: Bash
    Steps: Run `./gradlew allTests :composeApp:assembleDevDebug`.
    Expected: Command exits 0.
    Evidence: .sisyphus/evidence/task-9-build-hardening.txt

  Scenario: Architecture guardrail scan
    Tool: Bash
    Steps: Search feature source for `io.ktor.client.HttpClient`, search production app source for `AppStage`, and search for hardcoded base URL literals.
    Expected: No forbidden references remain.
    Evidence: .sisyphus/evidence/task-9-build-hardening-error.txt
  ```

  **Commit**: YES | Message: `chore(app): harden pos architecture build` | Files: cleanup across touched modules

## Final Verification Wave (MANDATORY — after ALL implementation tasks)
> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.
> **Do NOT auto-proceed after verification. Wait for user's explicit approval before marking work complete.**
> **Never mark F1-F4 as checked before getting user's okay.** Rejection or user feedback -> fix -> re-run -> present again -> wait for okay.
- [ ] F1. Plan Compliance Audit — oracle
- [ ] F2. Code Quality Review — unspecified-high
- [ ] F3. Real Manual QA — unspecified-high (+ interactive_bash Desktop run; Playwright only if browser target exists)
- [ ] F4. Scope Fidelity Check — deep

## Commit Strategy
- Commit per task after tests and QA evidence pass.
- Use conventional messages listed per task.
- Do not push unless user explicitly asks.
- If pre-commit hooks modify files, create a new commit or amend only if allowed by git safety rules.

## Success Criteria
- App starts with Koin once and renders the POS-GG shell.
- Network module uses Ktor internally and maps POS-GG endpoint responses/errors.
- Session datastore persists token and active store ID and clears them on logout.
- Navigation is strict Navigation 3, not enum/manual reducer runtime navigation.
- TDD tests cover contracts, DI, network, session, repositories, navigation, and ViewModel state.
- Build/test commands pass with evidence captured.
