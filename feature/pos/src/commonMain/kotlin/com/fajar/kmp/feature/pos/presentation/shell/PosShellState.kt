package com.fajar.kmp.feature.pos.presentation.shell

data class PosShellState(
    val isSessionLoaded: Boolean = false,
    val isAuthenticated: Boolean = false,
    val hasActiveStore: Boolean = false,
    val activeStoreId: String? = null,
    val authStatus: String = "Belum masuk",
    val storeStatus: String = "Belum ada toko aktif",
    val catalogStatus: String = "Catalog not loaded",
    val checkoutStatus: String = "No checkout yet",
    val syncStatus: String = "Not synced",
    val adminStatus: String = "Admin not loaded",
    val isAuthLoading: Boolean = false,
    val isStoreLoading: Boolean = false,
    val isCatalogLoading: Boolean = false,
    val isCheckoutLoading: Boolean = false,
    val isSyncLoading: Boolean = false,
    val isAdminLoading: Boolean = false,
)
