plugins {
    alias(libs.plugins.cmpDomain)
    alias(libs.plugins.cmpSerialization)
    alias(libs.plugins.cmpKoin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}
