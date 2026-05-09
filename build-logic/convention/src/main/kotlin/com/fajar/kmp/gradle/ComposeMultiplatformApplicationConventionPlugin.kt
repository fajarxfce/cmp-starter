package com.fajar.kmp.gradle

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.application")
        pluginManager.apply("org.jetbrains.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        configureKmpTargets()
        configureAndroidApplication("com.fajar.kmp")

        extensions.configure<KotlinMultiplatformExtension> {
            listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = "ComposeApp"
                    isStatic = true
                }
            }
            sourceSets.androidMain.dependencies {
                implementation(library("compose-uiToolingPreview"))
                implementation(library("androidx-activity-compose"))
            }
            sourceSets.commonMain.dependencies {
                implementation(library("compose-runtime"))
                implementation(library("compose-foundation"))
                implementation(library("compose-material3"))
                implementation(library("compose-ui"))
                implementation(library("compose-components-resources"))
                implementation(library("compose-uiToolingPreview"))
                implementation(library("androidx-lifecycle-viewmodelCompose"))
                implementation(library("androidx-lifecycle-runtimeCompose"))
                implementation(library("kotlinx-coroutines-core"))
                implementation(library("kotlinx-collections-immutable"))
                implementation(library("kotlinx-serialization-json"))
                implementation(library("koin-core"))
            }
            sourceSets.commonTest.dependencies {
                implementation(library("kotlin-test"))
                implementation(library("kotlinx-coroutines-test"))
                implementation(library("turbine"))
                implementation(library("koin-test"))
            }
        }

        extensions.configure<ApplicationExtension> {
            packaging {
                resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
            flavorDimensions += "environment"
            productFlavors {
                create("dev") {
                    dimension = "environment"
                    applicationIdSuffix = ".dev"
                    resValue("string", "app_name", "CMP Starter Dev")
                    buildConfigField("String", "BASE_URL", "\"https://dev.api.example.com\"")
                    buildConfigField("String", "FLAVOR_NAME", "\"dev\"")
                    buildConfigField("Boolean", "MOCK_MODE", "true")
                }
                create("staging") {
                    dimension = "environment"
                    applicationIdSuffix = ".staging"
                    resValue("string", "app_name", "CMP Starter Staging")
                    buildConfigField("String", "BASE_URL", "\"https://staging.api.example.com\"")
                    buildConfigField("String", "FLAVOR_NAME", "\"staging\"")
                    buildConfigField("Boolean", "MOCK_MODE", "false")
                }
                create("prod") {
                    dimension = "environment"
                    resValue("string", "app_name", "CMP Starter")
                    buildConfigField("String", "BASE_URL", "\"https://api.example.com\"")
                    buildConfigField("String", "FLAVOR_NAME", "\"prod\"")
                    buildConfigField("Boolean", "MOCK_MODE", "false")
                }
            }
            buildFeatures.buildConfig = true
            buildTypes.getByName("release") {
                isMinifyEnabled = false
            }
        }

        dependencies.add("debugImplementation", library("compose-uiTooling"))
        }
    }
}
