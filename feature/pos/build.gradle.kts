plugins {
    id("cmp.kotlin.multiplatform.library")
    id("cmp.test")
    id("cmp.serialization")
    id("cmp.koin")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.datastore)
            implementation(projects.core.network)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
