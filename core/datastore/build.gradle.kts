plugins {
    id("cmp.kotlin.multiplatform.library")
    id("cmp.test")
    id("cmp.koin")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.preferencesCore)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
