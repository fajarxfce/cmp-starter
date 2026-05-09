plugins {
    `kotlin-dsl`
}

group = "com.fajar.kmp.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatformLibrary") {
            id = "cmp.kotlin.multiplatform.library"
            implementationClass = "com.fajar.kmp.gradle.KotlinMultiplatformLibraryConventionPlugin"
        }
        register("composeMultiplatformLibrary") {
            id = "cmp.compose.multiplatform.library"
            implementationClass = "com.fajar.kmp.gradle.ComposeMultiplatformLibraryConventionPlugin"
        }
        register("composeMultiplatformApplication") {
            id = "cmp.compose.multiplatform.application"
            implementationClass = "com.fajar.kmp.gradle.ComposeMultiplatformApplicationConventionPlugin"
        }
        register("serialization") {
            id = "cmp.serialization"
            implementationClass = "com.fajar.kmp.gradle.SerializationConventionPlugin"
        }
        register("featureModule") {
            id = "cmp.feature"
            implementationClass = "com.fajar.kmp.gradle.FeatureModuleConventionPlugin"
        }
        register("testModule") {
            id = "cmp.test"
            implementationClass = "com.fajar.kmp.gradle.TestConventionPlugin"
        }
        register("androidApplication") {
            id = "cmp.android.application"
            implementationClass = "com.fajar.kmp.gradle.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "cmp.android.library"
            implementationClass = "com.fajar.kmp.gradle.AndroidLibraryConventionPlugin"
        }
        register("domainModule") {
            id = "cmp.domain"
            implementationClass = "com.fajar.kmp.gradle.DomainModuleConventionPlugin"
        }
        register("dataModule") {
            id = "cmp.data"
            implementationClass = "com.fajar.kmp.gradle.DataModuleConventionPlugin"
        }
        register("koin") {
            id = "cmp.koin"
            implementationClass = "com.fajar.kmp.gradle.KoinConventionPlugin"
        }
        register("buildConfig") {
            id = "cmp.buildconfig"
            implementationClass = "com.fajar.kmp.gradle.BuildConfigConventionPlugin"
        }
        register("flavors") {
            id = "cmp.flavors"
            implementationClass = "com.fajar.kmp.gradle.FlavorsConventionPlugin"
        }
        register("composeCompiler") {
            id = "cmp.compose.compiler"
            implementationClass = "com.fajar.kmp.gradle.ComposeCompilerConventionPlugin"
        }
        register("mviFeature") {
            id = "cmp.mvi.feature"
            implementationClass = "com.fajar.kmp.gradle.MviFeatureConventionPlugin"
        }
        register("detekt") {
            id = "cmp.detekt"
            implementationClass = "com.fajar.kmp.gradle.DetektConventionPlugin"
        }
        register("ktlint") {
            id = "cmp.ktlint"
            implementationClass = "com.fajar.kmp.gradle.KtlintConventionPlugin"
        }
        register("sqldelight") {
            id = "cmp.sqldelight"
            implementationClass = "com.fajar.kmp.gradle.SqlDelightConventionPlugin"
        }
    }
}
