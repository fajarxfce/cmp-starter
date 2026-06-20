package com.fajar.kmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.fajar.kmp.core.designsystem.CmpTheme
import com.fajar.kmp.core.navigation.AppNavigator
import com.fajar.kmp.core.navigation.AppRoute
import com.fajar.kmp.feature.pos.data.api.SyncRequest
import com.fajar.kmp.feature.pos.data.api.TransactionCreateRequest
import com.fajar.kmp.feature.pos.data.api.TransactionItemRequest
import com.fajar.kmp.feature.auth.presentation.screen.AuthMode
import com.fajar.kmp.feature.auth.presentation.screen.AuthScreen
import com.fajar.kmp.feature.home.presentation.screen.HomeScreen
import com.fajar.kmp.feature.home.presentation.screen.OnboardingScreen
import com.fajar.kmp.feature.home.presentation.screen.SplashScreen
import com.fajar.kmp.feature.pos.presentation.screen.StoreSetupScreen
import com.fajar.kmp.feature.pos.presentation.shell.PosShellViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

private const val DefaultStoreId = "demo-store"
private const val DefaultSyncTimestamp = "2024-01-01T00:00:00Z"

@Composable
@Preview
fun App() {
    CmpTheme {
        val viewModel = koinInject<PosShellViewModel>()
        val shellState by viewModel.state.collectAsState()
        val navigator = remember { AppNavigator() }
        val backStack = remember { mutableStateListOf<AppRoute>().also { it += navigator.backStack } }
        fun syncBackStack() {
            backStack.clear()
            backStack += navigator.backStack
        }

        LaunchedEffect(shellState.isSessionLoaded, shellState.isAuthenticated, shellState.hasActiveStore) {
            if (!shellState.isSessionLoaded || navigator.currentRoute != AppRoute.Splash) return@LaunchedEffect
            delay(600)
            when {
                !shellState.isAuthenticated -> navigator.goToOnboarding()
                !shellState.hasActiveStore -> navigator.completeAuthentication(hasActiveStore = false)
                else -> navigator.completeAuthentication(hasActiveStore = true)
            }
            syncBackStack()
        }

        LaunchedEffect(shellState.isAuthenticated, shellState.hasActiveStore) {
            when {
                shellState.isAuthenticated && shellState.hasActiveStore && navigator.currentRoute == AppRoute.StoreSetup -> {
                    navigator.completeStoreSetup()
                    syncBackStack()
                }
                shellState.isAuthenticated && !shellState.hasActiveStore && navigator.currentRoute == AppRoute.Login -> {
                    navigator.completeAuthentication(hasActiveStore = false)
                    syncBackStack()
                }
            }
        }

        NavDisplay(
            backStack = backStack,
            onBack = {
                if (navigator.goBack()) syncBackStack()
            },
            entryProvider = { route ->
                NavEntry(route) {
                    when (route) {
                        AppRoute.Splash -> SplashScreen()
                        AppRoute.Onboarding -> OnboardingScreen(
                    onLogin = {
                        navigator.goToLogin()
                        syncBackStack()
                    },
                    onRegister = {
                        navigator.goToRegister()
                        syncBackStack()
                    },
                )
                        AppRoute.Login, AppRoute.Register -> AuthScreen(
                    mode = if (route == AppRoute.Register) AuthMode.Register else AuthMode.Login,
                    authStatus = shellState.authStatus,
                    isAuthLoading = shellState.isAuthLoading,
                    onModeChange = {
                        if (it == AuthMode.Register) navigator.goToRegister() else navigator.goToLogin()
                        syncBackStack()
                    },
                    onSubmit = { fullName, email, password, phone, tenant ->
                        if (route == AppRoute.Register) {
                            viewModel.register(email, fullName, password, phone, tenant)
                        } else {
                            viewModel.login(email, password)
                        }
                    },
                )
                        AppRoute.StoreSetup -> StoreSetupScreen(
                            storeStatus = shellState.storeStatus,
                            isStoreLoading = shellState.isStoreLoading,
                            onContinue = { request -> viewModel.registerStore(request) },
                        )
                        AppRoute.Dashboard, AppRoute.Catalog, AppRoute.Checkout, AppRoute.Sync, AppRoute.Admin -> HomeScreen(
                            route = route,
                            state = shellState,
                            onLoadCatalog = { viewModel.loadCatalog(shellState.activeStoreId ?: DefaultStoreId) },
                            onCheckout = { viewModel.checkout(shellState.activeStoreId ?: DefaultStoreId, defaultTransactionRequest()) },
                            onSync = { viewModel.sync(shellState.activeStoreId ?: DefaultStoreId, SyncRequest(lastSyncTimestamp = DefaultSyncTimestamp)) },
                            onLoadAdmin = { viewModel.loadAdmin() },
                            onNavigate = {
                                navigator.selectHomeTab(it)
                                syncBackStack()
                            },
                            onLogout = {
                                viewModel.logout()
                                navigator.logout()
                                syncBackStack()
                            },
                        )
                        is AppRoute.ProductDetail -> HomeScreen(
                            route = AppRoute.Catalog,
                            state = shellState,
                            onLoadCatalog = { viewModel.loadCatalog(shellState.activeStoreId ?: DefaultStoreId) },
                            onCheckout = { viewModel.checkout(shellState.activeStoreId ?: DefaultStoreId, defaultTransactionRequest()) },
                            onSync = { viewModel.sync(shellState.activeStoreId ?: DefaultStoreId, SyncRequest(lastSyncTimestamp = DefaultSyncTimestamp)) },
                            onLoadAdmin = { viewModel.loadAdmin() },
                            onNavigate = {
                                navigator.selectHomeTab(it)
                                syncBackStack()
                            },
                            onLogout = {
                                viewModel.logout()
                                navigator.logout()
                                syncBackStack()
                            },
                        )
                    }
                }
            }
        )
    }
}

private fun defaultTransactionRequest() = TransactionCreateRequest(
    items = listOf(
        TransactionItemRequest(
            productName = "Kopi Susu",
            productSku = "KPS-001",
            quantity = 2,
            unitPrice = 15_000,
            discountAmount = 0,
        ),
    ),
    discountAmount = 0,
    taxRate = 0,
    paymentMethod = "CASH",
    paidAmount = 50_000,
    customerName = "Pak Budi",
)
