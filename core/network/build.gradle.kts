plugins {
    id("cmp.kotlin.multiplatform.library")
    id("cmp.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
