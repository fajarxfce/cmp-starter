package com.fajar.kmp.shared

data class AppModule(
    val name: String,
    val description: String,
)

object StarterModules {
    val modules: List<AppModule> = listOf(
        AppModule("core:common", "Shared config, dispatchers, and domain errors"),
        AppModule("core:navigation", "Typed routes and navigation reducer"),
        AppModule("feature:todo", "Clean Architecture + MVI local-first sample"),
        AppModule("feature:auth", "Session and token flow sample"),
    )
}
