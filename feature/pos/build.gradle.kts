plugins {
    alias(libs.plugins.cmpFeature)
    alias(libs.plugins.cmpSerialization)
    alias(libs.plugins.cmpKoin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.datastore)
            implementation(projects.core.network)
            implementation(projects.core.designSystem)
            implementation(projects.core.navigation)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
        }
    }
}
