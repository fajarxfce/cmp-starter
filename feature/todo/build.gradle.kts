plugins {
    id("cmp.feature")
    id("cmp.serialization")
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
