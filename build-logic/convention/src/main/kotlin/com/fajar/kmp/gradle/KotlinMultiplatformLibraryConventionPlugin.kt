package com.fajar.kmp.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.kotlin.dsl.configure

class KotlinMultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.library")

        configureKmpTargets()
        configureAndroidLibrary(defaultNamespace())

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.commonTest.dependencies {
                implementation(library("kotlin-test"))
            }
        }
    }
}
