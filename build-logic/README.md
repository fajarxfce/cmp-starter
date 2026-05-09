Convention plugins are intentionally documented here as the starter boundary for extracting Gradle logic from modules.

Planned plugin surface:
- `kotlinMultiplatformLibrary`
- `composeMultiplatformLibrary`
- `composeMultiplatformApplication`
- `androidApplication`
- `androidLibrary`
- `featureModule`
- `domainModule`
- `dataModule`
- `testModule`
- `serialization`
- `koin`
- `sqldelight`
- `detekt`
- `ktlint`
- `buildConfig`
- `flavors`
- `composeCompiler`
- `mviFeature`

This project keeps the generated starter compileable first, then the repeated Gradle blocks can be moved behind these plugin names without changing module source code.
