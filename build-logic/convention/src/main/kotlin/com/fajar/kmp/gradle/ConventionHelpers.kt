package com.fajar.kmp.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal val Project.libs
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.version(name: String): String = libs.findVersion(name).get().requiredVersion

internal fun Project.library(name: String) = libs.findLibrary(name).get()

internal fun Project.configureKmpTargets() {
    extensions.configure<KotlinMultiplatformExtension> {
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }
        jvm("desktop")
        iosArm64()
        iosSimulatorArm64()
    }
}

internal fun Project.configureAndroidLibrary(namespace: String) {
    extensions.configure<LibraryExtension> {
        this.namespace = namespace
        compileSdk = version("android-compileSdk").toInt()
        defaultConfig {
            minSdk = version("android-minSdk").toInt()
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}

internal fun Project.configureAndroidApplication(namespace: String) {
    extensions.configure<ApplicationExtension> {
        this.namespace = namespace
        compileSdk = version("android-compileSdk").toInt()
        defaultConfig {
            applicationId = namespace
            minSdk = version("android-minSdk").toInt()
            targetSdk = version("android-targetSdk").toInt()
            versionCode = 1
            versionName = "1.0"
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}

internal fun Project.defaultNamespace(): String = "com.fajar.kmp." + path
    .trim(':')
    .replace(':', '.')
    .replace("-", "")
