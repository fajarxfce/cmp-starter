Create a production-ready Kotlin Compose Multiplatform starter kit using Clean Architecture, MVI architecture, SOLID principles, Gradle convention plugins, modular architecture, Navigation 3, dependency injection, local-first data layer, build flavors, TDD-ready structure, and scalable project organization.

Target platforms:
- Android
- iOS
- Desktop JVM
- Optional Web/Wasm support, but keep it isolated so it can be enabled later

Core requirements:

1. Architecture style
Use Clean Architecture + MVI.

Each feature must follow this structure:

feature:todo
├── api
├── di
├── domain
│   ├── model
│   ├── repository
│   └── usecase
├── data
│   ├── local
│   ├── remote
│   ├── mapper
│   └── repository
├── presentation
│   ├── contract
│   │   ├── TodoIntent.kt
│   │   ├── TodoState.kt
│   │   ├── TodoEffect.kt
│   │   └── TodoReducer.kt
│   ├── viewmodel
│   ├── route
│   └── screen
└── test

MVI rules:
- UI emits Intent
- ViewModel receives Intent
- ViewModel calls UseCase
- Reducer transforms old State + PartialChange into new State
- ViewModel exposes StateFlow<UiState>
- ViewModel exposes SharedFlow<UiEffect> for one-time events
- Composables render state only
- Composables do not contain business logic
- Side effects are handled explicitly
- State must be immutable
- Intent, State, Effect, and PartialChange must be sealed interfaces/classes where appropriate

Use this MVI flow:

User Action
-> Intent
-> ViewModel
-> UseCase
-> Repository
-> Local/Remote DataSource
-> PartialChange
-> Reducer
-> UiState
-> Compose UI

2. SOLID principles
Apply SOLID principles across the project.

Requirements:
- Single Responsibility Principle:
  Each class has one reason to change.
  UseCases must contain one business operation only.
  Repositories coordinate data sources only.
  Mappers map only.
  Reducers reduce only.

- Open/Closed Principle:
  Feature registration, navigation entries, sync handlers, and analytics trackers should be extendable without modifying existing core code.

- Liskov Substitution Principle:
  Interfaces must have predictable behavior and be replaceable by fakes in tests.

- Interface Segregation Principle:
  Avoid large interfaces.
  Split repository contracts when needed.
  Do not create god interfaces.

- Dependency Inversion Principle:
  Domain depends on abstractions.
  Data layer implements domain repository interfaces.
  Presentation depends on domain use cases, not concrete repositories.

3. TDD
Use a Test-Driven Development friendly structure.

Add tests before or alongside each important implementation.

Include test examples for:
- reducer tests
- ViewModel intent handling tests
- use case tests
- repository tests with fake local and remote data sources
- sync manager tests
- mapper tests
- navigation reducer/back stack tests
- validation tests
- error mapping tests

Use:
- kotlin.test
- kotlinx-coroutines-test
- Turbine
- fake implementations instead of heavy mocking where possible

Testing rules:
- Reducers must be pure and easy to test.
- UseCases must be independently testable.
- Repositories must be testable using fake data sources.
- ViewModels must be testable using fake use cases.
- UI state changes must be testable using StateFlow assertions.
- One-time effects must be testable using SharedFlow/Turbine.
- No real network call in unit tests.
- No real database dependency in unit tests unless integration test module is used.

Add test fixtures in:

core:testing

Include:
- FakeTodoRepository
- FakeNetworkMonitor
- FakeAuthRepository
- TestDispatcherProvider
- sample domain models
- sample DTOs
- sample database entities
- common assertion helpers

4. Project architecture
Use Kotlin Multiplatform with Compose Multiplatform.
Use Clean Architecture with clear separation between:
- presentation
- domain
- data
- platform-specific implementations

Use a modular structure:

root
├── build-logic
│   └── convention
├── composeApp
├── core
│   ├── common
│   ├── design-system
│   ├── navigation
│   ├── database
│   ├── network
│   ├── datastore
│   ├── model
│   ├── domain
│   ├── sync
│   ├── testing
│   ├── analytics
│   └── feature-flag
├── feature
│   ├── auth
│   ├── home
│   ├── settings
│   ├── profile
│   └── todo
└── shared

Each feature module must expose only its public API.
Avoid feature-to-feature direct dependencies.
Use core:navigation or app-level wiring for cross-feature navigation.

5. Gradle build convention
Create a build-logic module using convention plugins.
Do not duplicate Gradle configuration in every module.

Create convention plugins for:
- kotlinMultiplatformLibrary
- composeMultiplatformLibrary
- composeMultiplatformApplication
- androidApplication
- androidLibrary
- featureModule
- domainModule
- dataModule
- testModule
- serialization
- koin
- sqldelight or room
- detekt
- ktlint
- buildConfig
- flavors
- composeCompiler
- mviFeature

Use version catalogs with libs.versions.toml.
Use type-safe project accessors.
Use Kotlin DSL only.

6. Build flavors
Support build variants/flavors:
- dev
- staging
- prod

Each flavor must support:
- different base API URL
- different app name
- different package/application id suffix on Android
- different logging level
- different feature flags
- different database name if needed
- different mock mode
- different analytics enabled flag
- different crash reporting enabled flag
- different build config constants

Expose flavor config through common code:

interface AppConfig {
    val baseUrl: String
    val appName: String
    val isDebugLoggingEnabled: Boolean
    val isMockModeEnabled: Boolean
    val isAnalyticsEnabled: Boolean
    val flavor: AppFlavor
}

enum class AppFlavor {
    Dev,
    Staging,
    Prod
}

Use expect/actual or generated BuildConfig where appropriate.

7. Navigation
Use Navigation 3, not Navigation 2.
Create strongly typed destinations using NavKey.
Use kotlinx.serialization for navigation keys.
Use polymorphic serialization where needed for iOS/Web compatibility.

Create a core:navigation module that contains:
- AppRoute / NavKey definitions
- Navigator abstraction
- AppBackStack
- Navigation actions
- navigation reducer
- feature entry registration API

Use NavDisplay for rendering destinations.
Avoid string routes.

Support:
- bottom navigation
- nested flows
- authenticated vs unauthenticated graph
- deep link abstraction
- adaptive layout support for tablet/desktop
- save/restore back stack
- list-detail adaptive navigation
- one-pane/two-pane navigation behavior

Navigation must also follow MVI principles where possible:
- Navigation action is emitted as UiEffect
- App-level navigation state is explicit
- Back stack mutation is centralized
- Back stack reducer is testable

8. Dependency injection
Use Koin Multiplatform.

Create DI modules for:
- app
- platform
- core
- database
- network
- repositories
- use cases
- view models
- features
- dispatchers
- sync
- analytics
- feature flags

Use constructor injection where possible.
Avoid service locator anti-pattern inside business logic.
Provide platform-specific dependencies using expect/actual where needed.

9. Local-first architecture
Implement a local-first data layer.

Data flow:
UI -> Intent -> ViewModel -> UseCase -> Repository -> LocalDataSource + RemoteDataSource

Local database is the source of truth.
Remote API syncs into local database.
UI observes local database using Flow.

Support:
- offline-first reads
- queued writes
- sync worker / sync orchestrator
- conflict resolution strategy
- optimistic updates
- retry with exponential backoff
- network monitor
- sync status model
- last synced timestamp
- pending operations table
- idempotency key
- server version / local version
- dirty flag
- soft delete
- sync error reason

Use either:
- SQLDelight, preferred for broad KMP target support
or
- Room KMP, if stable for selected targets

Implement an example entity:
- User
- Todo
- TodoSyncOperation

Include:
- DAO/query layer
- local data source
- remote data source
- repository
- mapper
- sync manager
- conflict resolver
- use cases
- ViewModel
- Compose screen
- tests

10. Networking
Use Ktor Client Multiplatform.

Support:
- JSON serialization with kotlinx.serialization
- logging
- auth token injection
- refresh token flow
- timeout config
- retry policy
- error mapping
- Result/Either-style response wrapper
- environment-based base URL
- request id
- idempotency key
- network availability check

Create NetworkError and DomainError models.
Do not leak Ktor exceptions to domain or UI.

11. State management
Use MVI with shared/common ViewModel where possible.

Each screen should have:
- Intent
- State
- Effect
- PartialChange
- Reducer
- ViewModel
- Route composable
- Screen composable

Example:

sealed interface TodoIntent {
    data object LoadTodos : TodoIntent
    data class AddTodo(val title: String) : TodoIntent
    data class ToggleTodo(val id: TodoId) : TodoIntent
    data class DeleteTodo(val id: TodoId) : TodoIntent
    data object Refresh : TodoIntent
}

data class TodoState(
    val isLoading: Boolean = false,
    val todos: ImmutableList<TodoUiModel> = persistentListOf(),
    val errorMessage: String? = null,
    val syncStatus: SyncStatusUi = SyncStatusUi.Idle
)

sealed interface TodoEffect {
    data class ShowSnackbar(val message: String) : TodoEffect
    data class NavigateToDetail(val id: TodoId) : TodoEffect
}

sealed interface TodoPartialChange {
    data object Loading : TodoPartialChange
    data class TodosLoaded(val todos: List<Todo>) : TodoPartialChange
    data class Error(val error: DomainError) : TodoPartialChange
    data class SyncStatusChanged(val status: SyncStatus) : TodoPartialChange
}

interface Reducer<S, C> {
    fun reduce(state: S, change: C): S
}

12. Design system
Create core:design-system.

Visual style:
- Use a clean cloud-dashboard-inspired theme similar to DigitalOcean.
- Use bright ocean blue as the primary color.
- Use white and soft gray surfaces.
- Use clean cards with subtle borders.
- Use rounded corners.
- Use calm spacing.
- Use readable typography.
- Use simple dashboard-like components.

Do not copy DigitalOcean branding assets, logos, icons, or proprietary UI exactly.
Only use a similar cloud/SaaS/dashboard-inspired visual direction.

Suggested color palette:
- Primary: #0069FF
- Primary Hover/Pressed: #005FE6
- Primary Container: #EAF2FF
- Secondary: #00AEEF
- Background Light: #F7F9FC
- Surface Light: #FFFFFF
- Border Light: #D9E2EC
- Text Primary: #0F172A
- Text Secondary: #475569
- Success: #15CD72
- Warning: #FFB020
- Error: #E5484D
- Dark Background: #0B1220
- Dark Surface: #111827
- Dark Border: #253044

Include:
- app theme
- typography
- spacing
- color scheme
- buttons
- text fields
- loading state
- empty state
- error state
- app scaffold
- adaptive navigation components
- dashboard cards
- status badges
- sync status indicator
- previews or sample screen

Use Material 3 where available.
Support dark mode.
Support dynamic color on Android only as optional, but keep the DigitalOcean-like fallback palette consistent across platforms.

13. Authentication
Add an example auth flow:
- login
- logout
- token persistence
- refresh token
- authenticated route guard
- unauthenticated route guard
- secure storage abstraction
- auth state observer
- tests for auth use cases and auth reducer

Use expect/actual for secure storage:
- Android: Android Keystore-based encryption or EncryptedSharedPreferences abstraction
- iOS: Keychain
- Desktop: suitable local secure storage abstraction

14. Code quality
Add:
- ktlint
- detekt
- dependency analysis if possible
- explicit API mode for library modules if suitable
- CI-ready Gradle tasks
- README explaining architecture and commands

15. Developer experience
Include:
- README.md
- architecture.md
- testing.md
- mvi.md
- module graph explanation
- how to add a new feature
- how to add a new flavor
- how to add a new API endpoint
- how to add a new local-first entity
- how to run Android/iOS/Desktop
- how to write reducer tests
- how to write ViewModel tests
- sample .env or local.properties strategy
- Makefile or Gradle task aliases if useful

16. Example feature
Create a Todo feature demonstrating the full architecture.

Todo feature must include:
- TodoListScreen
- TodoDetailScreen
- AddTodoScreen
- TodoIntent
- TodoState
- TodoEffect
- TodoPartialChange
- TodoReducer
- TodoViewModel
- TodoRepository interface
- TodoRepositoryImpl
- ObserveTodosUseCase
- AddTodoUseCase
- ToggleTodoUseCase
- DeleteTodoUseCase
- SyncTodosUseCase
- local database table
- pending sync operation table
- remote DTO
- domain model
- UI model
- mappers
- navigation entries
- DI module
- reducer tests
- ViewModel tests
- repository tests
- use case tests
- sync manager tests

17. Important implementation constraints
- Avoid overengineering but keep the architecture scalable.
- Prefer simple, understandable abstractions.
- Every abstraction must have at least one real usage.
- Do not create empty enterprise boilerplate.
- Do not put business logic in Composables.
- Do not expose data-layer DTOs to domain or UI.
- Do not expose database entities to domain or UI.
- Do not use string-based navigation routes.
- Do not duplicate Gradle configs.
- Keep feature modules isolated.
- Use expect/actual only when necessary.
- Make the initial project compile successfully.
- Include comments only where they explain non-obvious architecture decisions.
- Follow TDD-friendly design.
- Keep reducers pure.
- Keep side effects explicit.
- Keep state immutable.
- Follow SOLID principles.

Deliverables:
- Full project structure
- Gradle files
- version catalog
- convention plugins
- sample source code
- README
- architecture documentation
- MVI documentation
- testing documentation
- example Todo feature
- example build flavors
- example local-first sync implementation
- example Navigation 3 setup
- example DI setup
- example tests
- DigitalOcean-like design system theme
- explanation of the architecture

After generating the project, explain:
- why each module exists
- how MVI flow works
- how data flows from UI to database/API
- how navigation works
- how SOLID is applied
- how TDD is supported
- how to add a new feature
- how to add a new flavor
- what parts are platform-specific
- what parts are shared
