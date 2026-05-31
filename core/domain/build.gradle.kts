plugins {
    alias(libs.plugins.cmpDomain)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.common)
        }
    }
}
