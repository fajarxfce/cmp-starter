package com.fajar.kmp.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Splash : AppRoute

    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Register : AppRoute

    @Serializable
    data object StoreSetup : AppRoute

    @Serializable
    data object Dashboard : AppRoute

    @Serializable
    data object Catalog : AppRoute

    @Serializable
    data object Checkout : AppRoute

    @Serializable
    data object Sync : AppRoute

    @Serializable
    data object Admin : AppRoute

    @Serializable
    data class ProductDetail(val id: String) : AppRoute
}

class AppNavigator(startRoute: AppRoute = AppRoute.Splash) {
    private val routeStack = mutableListOf(startRoute)

    val backStack: List<AppRoute>
        get() = routeStack.toList()

    val currentRoute: AppRoute
        get() = routeStack.last()

    fun goToOnboarding() = replaceAll(AppRoute.Onboarding)

    fun goToLogin() = replaceAll(AppRoute.Login)

    fun goToRegister() = replaceAll(AppRoute.Register)

    fun completeAuthentication(hasActiveStore: Boolean) = replaceAll(
        if (hasActiveStore) AppRoute.Dashboard else AppRoute.StoreSetup,
    )

    fun completeStoreSetup() = replaceAll(AppRoute.Dashboard)

    fun logout() = replaceAll(AppRoute.Login)

    fun selectHomeTab(route: AppRoute) {
        require(route.isHomeRoute) { "Route $route is not a home route" }
        replaceAll(route)
    }

    fun goBack(): Boolean {
        if (routeStack.size == 1) return false
        routeStack.removeAt(routeStack.lastIndex)
        return true
    }

    fun replaceAll(route: AppRoute) {
        routeStack.clear()
        routeStack += route
    }
}

val AppRoute.displayTitle: String
    get() = when (this) {
        AppRoute.Splash -> "Splash"
        AppRoute.Onboarding -> "Onboarding"
        AppRoute.Login -> "Login"
        AppRoute.Register -> "Register"
        AppRoute.StoreSetup -> "Setup toko"
        AppRoute.Dashboard -> "Beranda"
        AppRoute.Catalog -> "Katalog"
        AppRoute.Checkout -> "Kasir"
        AppRoute.Sync -> "Sync"
        AppRoute.Admin -> "Toko"
        is AppRoute.ProductDetail -> "Product detail"
    }

val AppRoute.isHomeRoute: Boolean
    get() = this == AppRoute.Dashboard ||
        this == AppRoute.Catalog ||
        this == AppRoute.Checkout ||
        this == AppRoute.Sync ||
        this == AppRoute.Admin

interface FeatureEntry {
    val route: AppRoute
}
