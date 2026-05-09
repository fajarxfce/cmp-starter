plugins {
    id("cmp.kotlin.multiplatform.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.model)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
