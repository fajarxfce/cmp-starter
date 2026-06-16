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
                implementation(library("koin-compose"))
                implementation(library("koin-compose-viewmodel"))
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
                fun com.android.build.api.dsl.ApplicationProductFlavor.configureRuntime(
                    baseUrl: String,
                    flavorName: String,
                    mockMode: Boolean,
                    proxyEnabled: Boolean = false,
                    proxyHost: String = "10.144.0.185",
                    proxyPort: Int = 6969,
                ) {
                    buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
                    buildConfigField("String", "FLAVOR_NAME", "\"$flavorName\"")
                    buildConfigField("Boolean", "MOCK_MODE", mockMode.toString())
                    buildConfigField("Boolean", "PROXY_ENABLED", proxyEnabled.toString())
                    buildConfigField("String", "PROXY_HOST", "\"$proxyHost\"")
                    buildConfigField("String", "PROXY_PORT", "\"$proxyPort\"")
                }

                create("dev") {
                    dimension = "environment"
                    applicationIdSuffix = ".dev"
                    resValue("string", "app_name", "Kasir POS Dev")
                    configureRuntime(baseUrl = "http://10.144.0.185:8080", flavorName = "dev", mockMode = true)
                }
                create("devProxy") {
                    dimension = "environment"
                    applicationIdSuffix = ".dev.proxy"
                    resValue("string", "app_name", "Kasir POS Dev Proxy")
                    configureRuntime(baseUrl = "http://10.144.0.185:8080", flavorName = "devProxy", mockMode = true, proxyEnabled = true)
                }
                create("staging") {
                    dimension = "environment"
                    applicationIdSuffix = ".staging"
                    resValue("string", "app_name", "Kasir POS Staging")
                    configureRuntime(baseUrl = "http://10.144.0.185:8080", flavorName = "staging", mockMode = false)
                }
                create("prod") {
                    dimension = "environment"
                    resValue("string", "app_name", "Kasir POS")
                    configureRuntime(baseUrl = "http://10.144.0.185:8080", flavorName = "prod", mockMode = false)
                }
                create("prodProxy") {
                    dimension = "environment"
                    applicationIdSuffix = ".prod.proxy"
                    resValue("string", "app_name", "Kasir POS Proxy")
                    configureRuntime(baseUrl = "http://10.144.0.185:8080", flavorName = "prodProxy", mockMode = false, proxyEnabled = true)
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
