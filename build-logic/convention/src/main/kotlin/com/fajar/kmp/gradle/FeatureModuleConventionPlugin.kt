package com.fajar.kmp.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.kotlin.dsl.configure

class FeatureModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("cmp.compose.multiplatform.library")
        pluginManager.apply("cmp.test")

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                implementation(library("kotlinx-coroutines-core"))
                implementation(library("kotlinx-collections-immutable"))
            }
        }
    }
}
