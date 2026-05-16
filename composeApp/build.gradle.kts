import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("cmp.compose.multiplatform.application")
    id("cmp.serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.datastore)
            implementation(projects.core.designSystem)
            implementation(projects.core.navigation)
            implementation(projects.core.network)
            implementation(projects.feature.auth)
            implementation(projects.feature.pos)
            implementation(projects.feature.todo)
            implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.fajar.kmp.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CMP Starter"
            packageVersion = "1.0.0"
        }
    }
}
