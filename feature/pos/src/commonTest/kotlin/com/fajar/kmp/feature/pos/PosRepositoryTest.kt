package com.fajar.kmp.feature.pos

import com.fajar.kmp.core.datastore.InMemoryKeyValueStore
import com.fajar.kmp.core.datastore.KeyValueSessionPreferences
import com.fajar.kmp.core.network.ApiClient
import com.fajar.kmp.core.network.ApiRequest
import com.fajar.kmp.core.network.ApiResponse
import com.fajar.kmp.core.network.HttpMethod
import com.fajar.kmp.core.network.NetworkError
import com.fajar.kmp.core.network.NetworkResult
import com.fajar.kmp.core.network.data.AuthLoginRequest
import com.fajar.kmp.core.network.data.AuthRegisterRequest
import com.fajar.kmp.core.network.data.CategoryCreateRequest
import com.fajar.kmp.core.network.data.PosApiPaths
import com.fajar.kmp.core.network.data.ProductCreateRequest
import com.fajar.kmp.core.network.data.StoreRegisterRequest
import com.fajar.kmp.core.network.data.SyncRequest
import com.fajar.kmp.core.network.data.TransactionCreateRequest
import com.fajar.kmp.core.network.data.TransactionItemRequest
import com.fajar.kmp.feature.pos.data.PosRepositoryImpl
import com.fajar.kmp.feature.pos.domain.repository.PosError
import com.fajar.kmp.feature.pos.domain.repository.PosResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class PosRepositoryTest {
    @Test
    fun auth_login_posts_credentials_and_persists_access_token() = runTest {
        val apiClient = RecordingApiClient("""{"success":true,"data":{"accessToken":"token-123"}}""")
        val preferences = KeyValueSessionPreferences(InMemoryKeyValueStore())
        val repository = PosRepositoryImpl(apiClient, preferences)

        val result = repository.login(AuthLoginRequest("user@example.com", "password123"))

        assertIs<PosResult.Success<String>>(result)
        assertEquals("token-123", result.value)
        assertEquals("token-123", preferences.getAccessToken())
        assertEquals(PosApiPaths.authLogin, apiClient.singleRequest().path)
        assertEquals(HttpMethod.Post, apiClient.singleRequest().method)
        assertTrue(apiClient.singleRequest().body.orEmpty().contains("user@example.com"))
    }

    @Test
    fun auth_register_posts_profile_to_register_endpoint() = runTest {
        val apiClient = RecordingApiClient("""{"success":true,"data":{"accessToken":"registered-token"}}""")
        val repository = repository(apiClient)

        val result = repository.register(AuthRegisterRequest("new@example.com", "New User", "password123"))

        assertIs<PosResult.Success<String?>>(result)
        assertEquals("registered-token", result.value)
        assertEquals(PosApiPaths.authRegister, apiClient.singleRequest().path)
        assertEquals(HttpMethod.Post, apiClient.singleRequest().method)
    }

    @Test
    fun store_register_persists_active_store_id_from_response() = runTest {
        val apiClient = RecordingApiClient("""{"success":true,"data":{"storeId":"store-123"}}""")
        val preferences = KeyValueSessionPreferences(InMemoryKeyValueStore())
        val repository = PosRepositoryImpl(apiClient, preferences)

        val result = repository.registerStore(sampleStoreRequest())

        assertIs<PosResult.Success<String>>(result)
        assertEquals("store-123", result.value)
        assertEquals("store-123", preferences.getActiveStoreId())
        assertEquals(PosApiPaths.storeRegister, apiClient.singleRequest().path)
    }

    @Test
    fun store_register_missing_store_id_returns_clear_error() = runTest {
        val apiClient = RecordingApiClient("""{"success":true,"data":{"name":"Store"}}""")
        val preferences = KeyValueSessionPreferences(InMemoryKeyValueStore())
        val repository = PosRepositoryImpl(apiClient, preferences)

        val result = repository.registerStore(sampleStoreRequest())

        assertEquals(PosResult.Failure(PosError.MissingData("storeId missing from store register response")), result)
        assertEquals(null, preferences.getActiveStoreId())
    }

    @Test
    fun categories_list_and_create_use_store_category_endpoint() = runTest {
        val apiClient = RecordingApiClient("""{"success":true,"data":[]}""")
        val repository = repository(apiClient)

        assertIs<PosResult.Success<String>>(repository.listCategories("store-1"))
        assertIs<PosResult.Success<String>>(repository.createCategory("store-1", CategoryCreateRequest("Food", "Meals", "#fff", "utensils")))

        assertEquals(PosApiPaths.storeCategories("store-1"), apiClient.requests[0].path)
        assertEquals(HttpMethod.Get, apiClient.requests[0].method)
        assertEquals(PosApiPaths.storeCategories("store-1"), apiClient.requests[1].path)
        assertEquals(HttpMethod.Post, apiClient.requests[1].method)
    }

    @Test
    fun products_list_and_create_use_store_product_endpoint() = runTest {
        val apiClient = RecordingApiClient("""{"success":true,"data":[]}""")
        val repository = repository(apiClient)

        assertIs<PosResult.Success<String>>(repository.listProducts("store-1"))
        assertIs<PosResult.Success<String>>(repository.createProduct("store-1", sampleProductRequest()))

        assertEquals(PosApiPaths.storeProducts("store-1"), apiClient.requests[0].path)
        assertEquals(HttpMethod.Get, apiClient.requests[0].method)
        assertEquals(PosApiPaths.storeProducts("store-1"), apiClient.requests[1].path)
        assertEquals(HttpMethod.Post, apiClient.requests[1].method)
    }

    @Test
    fun transactions_list_and_create_use_store_transaction_endpoint() = runTest {
        val apiClient = RecordingApiClient("""{"success":true,"data":[]}""")
        val repository = repository(apiClient)

        assertIs<PosResult.Success<String>>(repository.listTransactions("store-1"))
        assertIs<PosResult.Success<String>>(repository.createTransaction("store-1", sampleTransactionRequest()))

        assertEquals(PosApiPaths.storeTransactions("store-1"), apiClient.requests[0].path)
        assertEquals(HttpMethod.Get, apiClient.requests[0].method)
        assertEquals(PosApiPaths.storeTransactions("store-1"), apiClient.requests[1].path)
        assertEquals(HttpMethod.Post, apiClient.requests[1].method)
    }

    @Test
    fun sync_request_uses_store_sync_endpoint() = runTest {
        val apiClient = RecordingApiClient("""{"success":true,"data":{"synced":true}}""")
        val repository = repository(apiClient)

        assertIs<PosResult.Success<String>>(repository.sync("store-1", SyncRequest("2026-05-12T00:00:00Z")))

        assertEquals(PosApiPaths.storeSync("store-1"), apiClient.singleRequest().path)
        assertEquals(HttpMethod.Post, apiClient.singleRequest().method)
    }

    @Test
    fun admin_stats_stores_and_users_use_admin_endpoints() = runTest {
        val apiClient = RecordingApiClient("""{"success":true,"data":[]}""")
        val repository = repository(apiClient)

        assertIs<PosResult.Success<String>>(repository.adminStats())
        assertIs<PosResult.Success<String>>(repository.adminStores())
        assertIs<PosResult.Success<String>>(repository.adminUsers())

        assertEquals(PosApiPaths.adminStats, apiClient.requests[0].path)
        assertEquals(PosApiPaths.adminStores, apiClient.requests[1].path)
        assertEquals(PosApiPaths.adminUsers, apiClient.requests[2].path)
        assertTrue(apiClient.requests.all { it.method == HttpMethod.Get })
    }

    @Test
    fun admin_unauthorized_maps_to_permission_error() = runTest {
        val apiClient = RecordingApiClient(NetworkResult.Failure(NetworkError.Unauthorized))
        val repository = repository(apiClient)

        val result = repository.adminStats()

        assertEquals(PosResult.Failure(PosError.Unauthorized), result)
    }

    @Test
    fun failure_result_does_not_leak_into_later_successful_call() = runTest {
        val apiClient = QueueApiClient(
            NetworkResult.Failure(NetworkError.Unauthorized),
            NetworkResult.Success(ApiResponse(200, """{"success":true,"data":[]}""")),
        )
        val repository = repository(apiClient)

        assertEquals(PosResult.Failure(PosError.Unauthorized), repository.adminStats())
        assertIs<PosResult.Success<String>>(repository.adminStores())
    }

    private fun repository(apiClient: ApiClient): PosRepositoryImpl =
        PosRepositoryImpl(apiClient, KeyValueSessionPreferences(InMemoryKeyValueStore()))

    private fun sampleStoreRequest() = StoreRegisterRequest(
        name = "Store",
        slug = "store",
        description = "Main store",
        timezone = "UTC",
        country = "ID",
        phone = "123",
        email = "store@example.com",
    )

    private fun sampleProductRequest() = ProductCreateRequest(
        categoryId = "cat-1",
        name = "Coffee",
        sku = "COF-1",
        barcode = "123",
        sellingPrice = 10_000,
        costPrice = 7_000,
        unit = "cup",
        initialStock = 10,
        trackStock = true,
    )

    private fun sampleTransactionRequest() = TransactionCreateRequest(
        items = listOf(TransactionItemRequest("prod-1", "Coffee", "COF-1", 1, 10_000, 0)),
        discountAmount = 0,
        taxRate = 0,
        paymentMethod = "cash",
        paidAmount = 10_000,
        customerName = "Customer",
    )
}

private class RecordingApiClient(
    private val result: NetworkResult<ApiResponse>,
) : ApiClient {
    constructor(body: String) : this(NetworkResult.Success(ApiResponse(200, body)))

    val requests = mutableListOf<ApiRequest>()

    override suspend fun execute(request: ApiRequest): NetworkResult<ApiResponse> {
        requests += request
        return result
    }

    fun singleRequest(): ApiRequest = requests.single()
}

private class QueueApiClient(
    vararg results: NetworkResult<ApiResponse>,
) : ApiClient {
    private val queuedResults = results.toMutableList()

    override suspend fun execute(request: ApiRequest): NetworkResult<ApiResponse> = queuedResults.removeAt(0)
}
