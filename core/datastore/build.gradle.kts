plugins {
    alias(libs.plugins.cmpData)
    alias(libs.plugins.cmpTest)
    alias(libs.plugins.cmpKoin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.preferencesCore)
        }
    }
}
