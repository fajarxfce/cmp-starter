# Issues

## 2026-05-10 Start Work
- None yet.

## 2026-05-10 Task 1 verification fix
- Initial Task 1 verification failed because `org.jetbrains.androidx.navigation3:navigation3-runtime:1.0.0-alpha06` did not resolve for desktop and `io.insert-koin:koin-compose-navigation3:4.1.0` was unavailable. Fixed by using documented `navigation3-ui:1.0.0-alpha05`, removing optional Koin Nav3 integration, and keeping Koin Compose/ViewModel only.
- LSP diagnostics could not complete in this environment due repeated Kotlin LSP initialization timeouts/address conflicts; Gradle tests and assemble passed after the fix.

## 2026-05-10 Task 2 verification
- `lsp_diagnostics` timed out on the new `core/network` contract files during initialization, so verification relied on `:core:network:allTests` instead.


## 2026-05-10 Task 5 verification notes
- `rg` is not installed in this environment, so required searches were performed with the available search tooling instead.
- Kotlin LSP diagnostics still timed out during initialization and reported address conflicts; Gradle `:core:datastore:allTests` and `:feature:auth:allTests` passed.

## 2026-05-10 Task 4 verification
- Kotlin LSP diagnostics timed out again during initialization with address-in-use errors, so verification relied on `./gradlew :core:network:allTests` plus source inspection.

## 2026-05-10 Task 3 verification notes
- Kotlin LSP diagnostics continued to time out during initialization with address-in-use errors, so verification relied on Gradle `allTests` and the desktop Koin verification test.
- `rg` is unavailable in this environment; required source searches were captured with local script/search tooling instead.

## 2026-05-16 Task 6 verification notes
- Kotlin LSP diagnostics still timed out during initialization/address binding, so verification relied on `./gradlew allTests` plus targeted POS edge-case tests captured under `.sisyphus/evidence/`.

## 2026-05-16 POS Shell ViewModel
- `lsp_diagnostics` timed out during Kotlin LSP initialization for the new files, so Gradle compilation/tests were used as the mandatory verification path.
- First test run failed because assertions read state before `viewModelScope.launch` completed. Fixed by allowing `PosShellViewModel` to accept an optional `CoroutineScope` for tests while defaulting to `viewModelScope` in production.
- Final Gradle verification passes, with existing expect/actual beta warnings and test `advanceUntilIdle` opt-in warnings only.

## 2026-05-16 Task 8 UI Migration Wiring
- Kotlin `lsp_diagnostics` continues to time out during language-server initialization for edited commonMain files.
- `./gradlew :composeApp:run` is blocked by `AppRuntimeConfig.desktop` placeholder base URL: `REPLACE_WITH_DESKTOP_BASE_URL`. This is documented in evidence and was not replaced with a real URL to honor the no-hardcoded-environment-URL constraint.

## 2026-05-16 Task 8 Navigation Bugfix
- Kotlin `lsp_diagnostics` still times out during initialization, so Gradle compile/test remains the verification source.
- Compose route-level regression tests were not added because existing tests do not include UI test infrastructure for inspecting `NavDisplay` state; view model failure-state tests cover the observable failure condition instead.

## 2026-05-16 Task 9 verification notes
- Kotlin `lsp_diagnostics` still times out during initialization, so verification relied on `./gradlew allTests :composeApp:assembleDevDebug` plus source guard scans.
- `rg` is unavailable from shell in this environment; required guard evidence was captured with a local Python source walker instead.
