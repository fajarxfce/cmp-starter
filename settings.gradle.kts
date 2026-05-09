rootProject.name = "cmp-starter"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":core:common")
include(":core:design-system")
include(":core:navigation")
include(":core:model")
include(":core:domain")
include(":core:database")
include(":core:network")
include(":core:datastore")
include(":core:sync")
include(":core:testing")
include(":core:analytics")
include(":core:feature-flag")
include(":feature:todo")
include(":feature:auth")
include(":feature:home")
include(":feature:settings")
include(":feature:profile")
include(":shared")
