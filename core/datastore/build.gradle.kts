plugins {
    alias(libs.plugins.cmpData)
    alias(libs.plugins.cmpTest)
    alias(libs.plugins.cmpKoin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
        }
    }
}
