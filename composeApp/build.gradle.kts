plugins {
    id("cmp.compose.multiplatform.application")
    id("cmp.serialization")
}

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.designSystem)
            implementation(projects.core.navigation)
            implementation(projects.feature.todo)
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
