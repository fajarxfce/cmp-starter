package com.fajar.kmp.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.kotlin.dsl.configure

class TestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.commonTest.dependencies {
                implementation(library("kotlinx-coroutines-test"))
                implementation(library("turbine"))
            }
        }
    }
}
