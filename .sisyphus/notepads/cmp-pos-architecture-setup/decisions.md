# Decisions

## 2026-05-10 Start Work
- Work directly in current repo; no worktree specified.
- Follow plan wave order and mark plan checkboxes only after Atlas verification.

## 2026-05-10 Research pass
- Treat the Android Nav3 recipes as the source of truth for API shape, but adapt carefully for CMP because some samples use Android-only dependencies and scene strategies.
- Prefer service/repository ownership of `HttpClient` over UI ownership when translating examples into the POS shell.

## 2026-05-10 Task 1 scope cleanup
- Removed unrelated Gradle runtime/toolchain edits from Task 1 scope: no `org.gradle.configuration-cache.parallel`, no `org.gradle.parallel`, no `org.gradle.daemon.idletimeout`, no Foojay resolver plugin, and no generated `gradle-daemon-jvm.properties`. Required Task 1 Gradle verification still passes without them.

## 2026-05-16 POS Shell ViewModel
- Kept the shell minimal and direct: `PosShellViewModel` depends on `PosRepository` instead of introducing another use-case aggregate, matching the hard requirement and minimizing new wiring.
- `loadCatalog` currently uses `listProducts` as the catalog source because the repository exposes separate category/product methods and the minimal shell only needs catalog status text.
- `loadAdmin` currently uses `adminStats` as the representative admin load endpoint for shell status.
- Production coroutine behavior remains `viewModelScope`; test-only determinism is provided by the optional injected `CoroutineScope` constructor parameter.

## 2026-05-16 Task 8 UI Migration Wiring
- Kept POS shell wiring inside existing `App.kt` screens rather than introducing new screen files, because Task 8 asks to migrate the current shell and preserve visual style.
- Added a minimal app-level `appPosShellModule` instead of changing the feature `posModule`, because `PosShellViewModel` lives in `composeApp` and should not be owned by the reusable feature module.
- Used representative `DefaultStoreId`/`DefaultSyncTimestamp` and a local demo transaction payload for shell actions, since this UI unit is wiring actions/statuses and not implementing store selection or full checkout data entry.

## 2026-05-16 Task 8 Navigation Bugfix
- Used success-status-gated `LaunchedEffect` in `App.kt` instead of adding view model callbacks, keeping navigation ownership in the app shell and avoiding UI callbacks inside `PosShellViewModel`.
- Success gates are exact strings already emitted by `PosShellViewModel`: `Authenticated`, `Registered`, and `Store registered:`. Error strings do not match these gates and remain visible on the active route.
