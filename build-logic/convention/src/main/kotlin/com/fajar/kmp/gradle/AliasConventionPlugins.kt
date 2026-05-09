package com.fajar.kmp.gradle

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        configureAndroidApplication("com.fajar.kmp")
    }
}

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        configureAndroidLibrary(defaultNamespace())
    }
}

class DomainModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("cmp.kotlin.multiplatform.library")
    }
}

class DataModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("cmp.kotlin.multiplatform.library")
        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                implementation(library("kotlinx-coroutines-core"))
            }
        }
    }
}

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                implementation(library("koin-core"))
            }
            sourceSets.commonTest.dependencies {
                implementation(library("koin-test"))
            }
        }
    }
}

class BuildConfigConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> {
                buildFeatures.buildConfig = true
            }
        }
    }
}

class FlavorsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> {
                if (!flavorDimensions.contains("environment")) flavorDimensions += "environment"
            }
        }
    }
}

class ComposeCompilerConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    }
}

class MviFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("cmp.feature")
    }
}

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = Unit
}

class KtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = Unit
}

class SqlDelightConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("cmp.data")
    }
}
