package com.fajar.kmp.pos

data class PosShellState(
    val authStatus: String = "Not authenticated",
    val storeStatus: String = "No store registered",
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
