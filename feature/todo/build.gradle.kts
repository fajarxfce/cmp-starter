plugins {
    alias(libs.plugins.cmpFeature)
    alias(libs.plugins.cmpSerialization)
    alias(libs.plugins.cmpKoin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.model)
            implementation(projects.core.domain)
            implementation(projects.core.navigation)
            implementation(projects.core.designSystem)
            implementation(projects.core.sync)
            implementation(projects.core.network)
            implementation(libs.kotlinx.datetime)
        }
    }
}
