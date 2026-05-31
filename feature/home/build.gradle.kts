plugins {
    alias(libs.plugins.cmpFeature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designSystem)
            implementation(projects.core.navigation)
            implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)
            implementation(projects.feature.pos)
        }
    }
}
