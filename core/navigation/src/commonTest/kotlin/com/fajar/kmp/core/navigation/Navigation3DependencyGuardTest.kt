package com.fajar.kmp.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertSame

class Navigation3DependencyGuardTest {
    @Test
    fun navigation3_runtime_types_are_available() {
        val route: NavKey = GuardRoute

        assertSame(GuardRoute, route)
    }

    @Serializable
    private data object GuardRoute : NavKey
}
