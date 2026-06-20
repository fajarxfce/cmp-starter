package com.fajar.kmp.feature.pos.presentation.shell

import com.fajar.kmp.feature.pos.data.api.AuthLoginRequest
import com.fajar.kmp.feature.pos.data.api.AuthRegisterRequest
import com.fajar.kmp.feature.pos.data.api.CategoryCreateRequest
import com.fajar.kmp.feature.pos.data.api.ProductCreateRequest
import com.fajar.kmp.feature.pos.data.api.StoreRegisterRequest
import com.fajar.kmp.feature.pos.data.api.SyncRequest
import com.fajar.kmp.feature.pos.data.api.TransactionCreateRequest
import com.fajar.kmp.core.common.result.Try
import com.fajar.kmp.feature.pos.domain.repository.PosRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

class PosShellViewModelTest {
    @Test
    fun login_failure_updates_auth_status() = runTest {
        val viewModel = PosShellViewModel(FakePosRepository(loginResult = Try.Error(Throwable("Email atau password belum sesuai"))), this)

        viewModel.login("user@example.com", "bad-password")
        advanceUntilIdle()

        assertEquals("Email atau password belum sesuai", viewModel.state.value.authStatus)
        assertFalse(viewModel.state.value.isAuthLoading)
    }

    @Test
    fun register_failure_updates_auth_status() = runTest {
        val viewModel = PosShellViewModel(FakePosRepository(registerResult = Try.Error(Throwable("Register failed"))), this)

        viewModel.register("user@example.com", "User", "password123", null, null)
        advanceUntilIdle()

        assertEquals("Register failed", viewModel.state.value.authStatus)
        assertFalse(viewModel.state.value.isAuthLoading)
    }

    @Test
    fun store_success_updates_store_status() = runTest {
        val viewModel = PosShellViewModel(FakePosRepository(registerStoreResult = Try.Success("store-1")), this)

        viewModel.registerStore(storeRequest())
        advanceUntilIdle()

        assertEquals("Toko siap digunakan", viewModel.state.value.storeStatus)
        assertFalse(viewModel.state.value.isStoreLoading)
    }

    @Test
    fun store_failure_updates_store_status() = runTest {
        val viewModel = PosShellViewModel(
            FakePosRepository(registerStoreResult = Try.Error(Throwable("Store register failed"))),
            this,
        )

        viewModel.registerStore(storeRequest())
        advanceUntilIdle()

        assertEquals("Store register failed", viewModel.state.value.storeStatus)
        assertFalse(viewModel.state.value.isStoreLoading)
    }

    @Test
    fun catalog_empty_and_error_update_catalog_status() = runTest {
        val emptyViewModel = PosShellViewModel(FakePosRepository(listProductsResult = Try.Success("")), this)
        emptyViewModel.loadCatalog("store-1")
        advanceUntilIdle()

        assertEquals("Belum ada produk", emptyViewModel.state.value.catalogStatus)

        val errorViewModel = PosShellViewModel(
            FakePosRepository(listProductsResult = Try.Error(Throwable("Catalog offline"))),
            this,
        )
        errorViewModel.loadCatalog("store-1")
        advanceUntilIdle()

        assertEquals("Catalog offline", errorViewModel.state.value.catalogStatus)
        assertFalse(errorViewModel.state.value.isCatalogLoading)
    }

    @Test
    fun checkout_success_and_failure_update_checkout_status() = runTest {
        val successViewModel = PosShellViewModel(FakePosRepository(createTransactionResult = Try.Success("txn-1")), this)
        successViewModel.checkout("store-1", transactionRequest())
        advanceUntilIdle()

        assertEquals("Transaksi berhasil disimpan", successViewModel.state.value.checkoutStatus)

        val failureViewModel = PosShellViewModel(
            FakePosRepository(createTransactionResult = Try.Error(Throwable("Cart is empty"))),
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
            FakePosRepository(syncResult = Try.Error(Throwable("Sync failed"))),
            this,
        )

        viewModel.sync("store-1", SyncRequest(lastSyncTimestamp = "2026-05-16T00:00:00Z"))
        advanceUntilIdle()

        assertEquals("Sync failed", viewModel.state.value.syncStatus)
        assertFalse(viewModel.state.value.isSyncLoading)
    }

    @Test
    fun admin_unauthorized_updates_admin_status() = runTest {
        val viewModel = PosShellViewModel(FakePosRepository(adminStatsResult = Try.Error(Throwable("Email atau password belum sesuai"))), this)

        viewModel.loadAdmin()
        advanceUntilIdle()

        assertEquals("Email atau password belum sesuai", viewModel.state.value.adminStatus)
        assertFalse(viewModel.state.value.isAdminLoading)
    }
}

private class FakePosRepository(
    private val loginResult: Try<String, Throwable> = Try.Success("token"),
    private val registerResult: Try<String?, Throwable> = Try.Success("token"),
    private val registerStoreResult: Try<String, Throwable> = Try.Success("store"),
    private val listProductsResult: Try<String, Throwable> = Try.Success("products"),
    private val createTransactionResult: Try<String, Throwable> = Try.Success("transaction"),
    private val syncResult: Try<String, Throwable> = Try.Success("sync"),
    private val adminStatsResult: Try<String, Throwable> = Try.Success("admin"),
) : PosRepository {
    override suspend fun login(request: AuthLoginRequest): Try<String, Throwable> = loginResult
    override suspend fun register(request: AuthRegisterRequest): Try<String?, Throwable> = registerResult
    override suspend fun registerStore(request: StoreRegisterRequest): Try<String, Throwable> = registerStoreResult
    override suspend fun listCategories(storeId: String): Try<String, Throwable> = Try.Success("categories")
    override suspend fun createCategory(storeId: String, request: CategoryCreateRequest): Try<String, Throwable> = Try.Success("category")
    override suspend fun listProducts(storeId: String): Try<String, Throwable> = listProductsResult
    override suspend fun createProduct(storeId: String, request: ProductCreateRequest): Try<String, Throwable> = Try.Success("product")
    override suspend fun listTransactions(storeId: String): Try<String, Throwable> = Try.Success("transactions")
    override suspend fun createTransaction(storeId: String, request: TransactionCreateRequest): Try<String, Throwable> = createTransactionResult
    override suspend fun sync(storeId: String, request: SyncRequest): Try<String, Throwable> = syncResult
    override suspend fun adminStats(): Try<String, Throwable> = adminStatsResult
    override suspend fun adminStores(): Try<String, Throwable> = Try.Success("stores")
    override suspend fun adminUsers(): Try<String, Throwable> = Try.Success("users")
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
