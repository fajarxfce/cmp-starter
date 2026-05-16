package com.fajar.kmp.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class AppNavigatorTest {
    @Test
    fun app_routes_are_navigation3_keys() {
        val routes = listOf(
            AppRoute.Splash,
            AppRoute.Onboarding,
            AppRoute.Login,
            AppRoute.Register,
            AppRoute.StoreSetup,
            AppRoute.Dashboard,
            AppRoute.Catalog,
            AppRoute.Checkout,
            AppRoute.Sync,
            AppRoute.Admin,
        )

        routes.forEach { route -> assertIs<NavKey>(route) }
    }

    @Test
    fun starts_at_splash() {
        val navigator = AppNavigator()

        assertEquals(listOf(AppRoute.Splash), navigator.backStack)
        assertSame(AppRoute.Splash, navigator.currentRoute)
    }

    @Test
    fun auth_to_store_to_home_path_replaces_route_stack() {
        val navigator = AppNavigator()

        navigator.goToLogin()
        navigator.completeAuthentication(hasActiveStore = false)
        navigator.completeStoreSetup()

        assertEquals(listOf(AppRoute.Dashboard), navigator.backStack)
    }

    @Test
    fun auth_success_with_active_store_opens_dashboard() {
        val navigator = AppNavigator()

        navigator.goToRegister()
        navigator.completeAuthentication(hasActiveStore = true)

        assertEquals(listOf(AppRoute.Dashboard), navigator.backStack)
    }

    @Test
    fun home_sections_push_and_back_to_dashboard_without_emptying_root() {
        val navigator = AppNavigator(AppRoute.Dashboard)

        navigator.openHomeRoute(AppRoute.Catalog)
        navigator.openHomeRoute(AppRoute.Checkout)
        navigator.openHomeRoute(AppRoute.Sync)
        navigator.openHomeRoute(AppRoute.Admin)
        navigator.goBack()
        navigator.goBack()
        navigator.goBack()
        navigator.goBack()
        navigator.goBack()

        assertEquals(listOf(AppRoute.Dashboard), navigator.backStack)
    }
}
