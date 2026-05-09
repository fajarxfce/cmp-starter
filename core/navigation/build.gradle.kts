plugins {
    id("cmp.kotlin.multiplatform.library")
    id("cmp.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}
