package com.fajar.kmp.pos

import com.fajar.kmp.core.network.data.AuthLoginRequest
import com.fajar.kmp.core.network.data.AuthRegisterRequest
import com.fajar.kmp.core.network.data.CategoryCreateRequest
import com.fajar.kmp.core.network.data.ProductCreateRequest
import com.fajar.kmp.core.network.data.StoreRegisterRequest
import com.fajar.kmp.core.network.data.SyncRequest
import com.fajar.kmp.core.network.data.TransactionCreateRequest
import com.fajar.kmp.feature.pos.domain.repository.PosError
import com.fajar.kmp.feature.pos.domain.repository.PosRepository
import com.fajar.kmp.feature.pos.domain.repository.PosResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

class PosShellViewModelTest {
    @Test
    fun login_failure_updates_auth_status() = runTest {
        val viewModel = PosShellViewModel(FakePosRepository(loginResult = PosResult.Failure(PosError.Unauthorized)), this)

        viewModel.login("user@example.com", "bad-password")
        advanceUntilIdle()

        assertEquals("Unauthorized", viewModel.state.value.authStatus)
        assertFalse(viewModel.state.value.isAuthLoading)
    }

    @Test
    fun register_failure_updates_auth_status() = runTest {
        val viewModel = PosShellViewModel(FakePosRepository(registerResult = PosResult.Failure(PosError.Network("Register failed"))), this)

        viewModel.register("user@example.com", "User", "password123", null, null)
        advanceUntilIdle()

        assertEquals("Register failed", viewModel.state.value.authStatus)
        assertFalse(viewModel.state.value.isAuthLoading)
    }

    @Test
    fun store_success_updates_store_status() = runTest {
        val viewModel = PosShellViewModel(FakePosRepository(registerStoreResult = PosResult.Success("store-1")), this)

        viewModel.registerStore(storeRequest())
        advanceUntilIdle()

        assertEquals("Store registered: store-1", viewModel.state.value.storeStatus)
        assertFalse(viewModel.state.value.isStoreLoading)
    }

    @Test
    fun store_failure_updates_store_status() = runTest {
        val viewModel = PosShellViewModel(
            FakePosRepository(registerStoreResult = PosResult.Failure(PosError.Network("Store register failed"))),
            this,
        )

        viewModel.registerStore(storeRequest())
        advanceUntilIdle()

        assertEquals("Store register failed", viewModel.state.value.storeStatus)
        assertFalse(viewModel.state.value.isStoreLoading)
    }

    @Test
    fun catalog_empty_and_error_update_catalog_status() = runTest {
        val emptyViewModel = PosShellViewModel(FakePosRepository(listProductsResult = PosResult.Success("")), this)
        emptyViewModel.loadCatalog("store-1")
        advanceUntilIdle()

        assertEquals("Catalog empty", emptyViewModel.state.value.catalogStatus)

        val errorViewModel = PosShellViewModel(
            FakePosRepository(listProductsResult = PosResult.Failure(PosError.Network("Catalog offline"))),
            this,
        )
        errorViewModel.loadCatalog("store-1")
        advanceUntilIdle()

        assertEquals("Catalog offline", errorViewModel.state.value.catalogStatus)
        assertFalse(errorViewModel.state.value.isCatalogLoading)
    }

    @Test
    fun checkout_success_and_failure_update_checkout_status() = runTest {
        val successViewModel = PosShellViewModel(FakePosRepository(createTransactionResult = PosResult.Success("txn-1")), this)
        successViewModel.checkout("store-1", transactionRequest())
        advanceUntilIdle()

        assertEquals("Checkout complete: txn-1", successViewModel.state.value.checkoutStatus)

        val failureViewModel = PosShellViewModel(
            FakePosRepository(createTransactionResult = PosResult.Failure(PosError.MissingData("Cart is empty"))),
            this,
        )
        failureViewModel.checkout("store-1", transactionRequest())
        advanceUntilIdle()

        assertEquals("Cart is empty", failureViewModel.state.value.checkoutStatus)
        assertFalse(failureViewModel.state.value.isCheckoutLoading)
    }

    @Test
    fun sync_error_updates_sync_status() = runTest {
        val viewModel = PosShellViewModel(
            FakePosRepository(syncResult = PosResult.Failure(PosError.Network("Sync failed"))),
            this,
        )

        viewModel.sync("store-1", SyncRequest(lastSyncTimestamp = "2026-05-16T00:00:00Z"))
        advanceUntilIdle()

        assertEquals("Sync failed", viewModel.state.value.syncStatus)
        assertFalse(viewModel.state.value.isSyncLoading)
    }

    @Test
    fun admin_unauthorized_updates_admin_status() = runTest {
        val viewModel = PosShellViewModel(FakePosRepository(adminStatsResult = PosResult.Failure(PosError.Unauthorized)), this)

        viewModel.loadAdmin()
        advanceUntilIdle()

        assertEquals("Unauthorized", viewModel.state.value.adminStatus)
        assertFalse(viewModel.state.value.isAdminLoading)
    }
}

private class FakePosRepository(
    private val loginResult: PosResult<String> = PosResult.Success("token"),
    private val registerResult: PosResult<String?> = PosResult.Success("token"),
    private val registerStoreResult: PosResult<String> = PosResult.Success("store"),
    private val listProductsResult: PosResult<String> = PosResult.Success("products"),
    private val createTransactionResult: PosResult<String> = PosResult.Success("transaction"),
    private val syncResult: PosResult<String> = PosResult.Success("sync"),
    private val adminStatsResult: PosResult<String> = PosResult.Success("admin"),
) : PosRepository {
    override suspend fun login(request: AuthLoginRequest): PosResult<String> = loginResult
    override suspend fun register(request: AuthRegisterRequest): PosResult<String?> = registerResult
    override suspend fun registerStore(request: StoreRegisterRequest): PosResult<String> = registerStoreResult
    override suspend fun listCategories(storeId: String): PosResult<String> = PosResult.Success("categories")
    override suspend fun createCategory(storeId: String, request: CategoryCreateRequest): PosResult<String> = PosResult.Success("category")
    override suspend fun listProducts(storeId: String): PosResult<String> = listProductsResult
    override suspend fun createProduct(storeId: String, request: ProductCreateRequest): PosResult<String> = PosResult.Success("product")
    override suspend fun listTransactions(storeId: String): PosResult<String> = PosResult.Success("transactions")
    override suspend fun createTransaction(storeId: String, request: TransactionCreateRequest): PosResult<String> = createTransactionResult
    override suspend fun sync(storeId: String, request: SyncRequest): PosResult<String> = syncResult
    override suspend fun adminStats(): PosResult<String> = adminStatsResult
    override suspend fun adminStores(): PosResult<String> = PosResult.Success("stores")
    override suspend fun adminUsers(): PosResult<String> = PosResult.Success("users")
}

private fun storeRequest() = StoreRegisterRequest(
    name = "Store",
    slug = "store",
    description = "Demo store",
    timezone = "Asia/Jakarta",
    country = "ID",
    phone = "0800000000",
    email = "store@example.com",
)

private fun transactionRequest() = TransactionCreateRequest(
    items = emptyList(),
    discountAmount = 0,
    taxRate = 0,
    paymentMethod = "cash",
    paidAmount = 0,
    customerName = "Walk-in",
)
