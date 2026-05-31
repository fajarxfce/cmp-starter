plugins {
    alias(libs.plugins.cmpFeature)
    alias(libs.plugins.cmpKoin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.datastore)
            implementation(projects.core.navigation)
            implementation(projects.core.designSystem)
        }
    }
}
