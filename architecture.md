# Architecture

The starter follows Clean Architecture with feature isolation.

## Layers

- Presentation: Compose screens, routes, MVI contracts, ViewModels.
- Domain: models, repository interfaces, use cases.
- Data: local data source, remote data source, mappers, repository implementation, sync manager.
- Platform: Android/iOS/Desktop entry points and platform configuration.

## SOLID

- SRP: reducers reduce, use cases perform one operation, repositories coordinate data sources.
- OCP: feature entries, navigation actions, analytics trackers, and sync managers are registered behind interfaces.
- LSP: repositories and data sources can be replaced by fakes in tests.
- ISP: small contracts such as `NetworkMonitor`, `SyncManager`, and `TodoRepository`.
- DIP: presentation depends on use cases; domain depends on repository abstraction; data implements it.

## Navigation

`core:navigation` models the back stack explicitly with serializable typed routes. This follows Navigation 3 principles while remaining common-code friendly. Android-only `NavDisplay` integration can be added at app rendering boundaries when its KMP story is acceptable.

## Starter extension points

- `core:database` ships SQLDelight-ready contracts and in-memory driver classes so the architecture compiles before choosing a concrete database driver per platform.
- `core:network` ships request/response wrappers, error mapping, and an in-memory API client for tests and mock mode.
- `feature:auth` ships a minimal session/token flow using the datastore abstraction; replace `AuthRepositoryImpl` internals with a real API client when connecting backend auth.
- `build-logic` centralizes repeated Android/KMP/Compose/test/flavor configuration. Quality tools are represented by convention plugin IDs so CI can enable real tool artifacts without changing module scripts.
