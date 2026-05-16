plugins {
    id("cmp.kotlin.multiplatform.library")
    id("cmp.serialization")
    id("cmp.koin")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}
