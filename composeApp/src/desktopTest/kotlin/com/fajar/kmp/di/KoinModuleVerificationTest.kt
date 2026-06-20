package com.fajar.kmp.di

import com.fajar.kmp.core.common.AppConfig
import com.fajar.kmp.core.common.AppFlavor
import com.fajar.kmp.core.common.ProxyConfig
import com.fajar.kmp.core.datastore.SessionPreferences
import com.fajar.kmp.core.navigation.AppNavigator
import com.fajar.kmp.core.network.ApiClient
import com.fajar.kmp.core.network.KtorApiClient
import com.fajar.kmp.feature.auth.presentation.viewmodel.AuthViewModel
import com.fajar.kmp.feature.todo.presentation.viewmodel.TodoViewModel
import kotlin.test.Test
import kotlin.test.assertIs
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.check.checkModules
import org.koin.test.verify.verifyAll

class KoinModuleVerificationTest {
    @Test
    fun productionModulesResolveAppGraph() {
        stopExistingKoin()

        val modules = productionAppModules(testConfig)

        val application = startKoin {
            modules(modules)
        }

        try {
            val koin = application.koin

            assertIs<AuthViewModel>(koin.get<AuthViewModel>())
            assertIs<TodoViewModel>(koin.get<TodoViewModel>())
            assertIs<SessionPreferences>(koin.get<SessionPreferences>())
            assertIs<ApiClient>(koin.get<ApiClient>())
            assertIs<KtorApiClient>(koin.get<ApiClient>())
            assertIs<AppNavigator>(koin.get<AppNavigator>())
            application.checkModules()
        } finally {
            stopKoin()
        }
    }

    private fun stopExistingKoin() {
        if (GlobalContext.getOrNull() != null) stopKoin()
    }

    private companion object {
        val testConfig = object : AppConfig {
            override val baseUrl: String = "https://pos.example.test"
            override val appName: String = "CMP Starter Test"
            override val isDebugLoggingEnabled: Boolean = true
            override val isMockModeEnabled: Boolean = false
            override val isAnalyticsEnabled: Boolean = false
            override val flavor: AppFlavor = AppFlavor.Dev
            override val proxy: ProxyConfig = ProxyConfig()
        }
    }
}
