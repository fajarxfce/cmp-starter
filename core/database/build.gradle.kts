plugins {
    alias(libs.plugins.cmpData)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
        }
    }
}
