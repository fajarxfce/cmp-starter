plugins {
    id("cmp.kotlin.multiplatform.library")
    id("cmp.test")
    id("cmp.koin")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.datastore)
            implementation(projects.core.navigation)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
