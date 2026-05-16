package com.fajar.kmp.core.network.data

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PosApiContractsTest {
    private val requestJson = Json {
        encodeDefaults = true
        explicitNulls = true
    }

    private val tolerantJson = PosApiJson.tolerant

    @Test
    fun endpoint_paths_are_exact() {
        assertEquals("/api/v1/auth/register", PosApiPaths.authRegister)
        assertEquals("/api/v1/auth/login", PosApiPaths.authLogin)
        assertEquals("/api/v1/stores/register", PosApiPaths.storeRegister)
        assertEquals("/api/v1/stores/store-123/categories", PosApiPaths.storeCategories("store-123"))
        assertEquals("/api/v1/stores/store-123/products", PosApiPaths.storeProducts("store-123"))
        assertEquals("/api/v1/stores/store-123/transactions", PosApiPaths.storeTransactions("store-123"))
        assertEquals("/api/v1/stores/store-123/sync", PosApiPaths.storeSync("store-123"))
        assertEquals("/api/v1/admin/stats", PosApiPaths.adminStats)
        assertEquals("/api/v1/admin/stores", PosApiPaths.adminStores)
        assertEquals("/api/v1/admin/users", PosApiPaths.adminUsers)
    }

    @Test
    fun request_dtos_serialize_to_postman_bodies() {
        assertEquals(
            """{"email":"admin@posgg.dev","fullName":"Admin POS","password":"adminpass123","phone":null,"tenantId":null}""",
            requestJson.encodeToString(AuthRegisterRequest.serializer(), AuthRegisterRequest("admin@posgg.dev", "Admin POS", "adminpass123")),
        )
        assertEquals(
            """{"email":"admin@posgg.dev","password":"adminpass123"}""",
            requestJson.encodeToString(AuthLoginRequest.serializer(), AuthLoginRequest("admin@posgg.dev", "adminpass123")),
        )
        assertEquals(
            """{"name":"Toko Mas Bro","slug":"toko-mas-bro","description":"Toko serba ada","timezone":"Asia/Jakarta","country":"ID","phone":"081234567890","email":"toko@masbro.com"}""",
            requestJson.encodeToString(
                StoreRegisterRequest.serializer(),
                StoreRegisterRequest(
                    name = "Toko Mas Bro",
                    slug = "toko-mas-bro",
                    description = "Toko serba ada",
                    timezone = "Asia/Jakarta",
                    country = "ID",
                    phone = "081234567890",
                    email = "toko@masbro.com",
                ),
            ),
        )
        assertEquals(
            """{"name":"Makanan & Minuman","description":"Produk F&B","color":"#FF5733","icon":"food"}""",
            requestJson.encodeToString(CategoryCreateRequest.serializer(), CategoryCreateRequest("Makanan & Minuman", "Produk F&B", "#FF5733", "food")),
        )
        assertEquals(
            """{"categoryId":null,"name":"Kopi Susu","sku":"KPS-001","barcode":"8990001234567","sellingPrice":15000,"costPrice":7000,"unit":"cup","initialStock":100,"trackStock":true}""",
            requestJson.encodeToString(
                ProductCreateRequest.serializer(),
                ProductCreateRequest(
                    name = "Kopi Susu",
                    sku = "KPS-001",
                    barcode = "8990001234567",
                    sellingPrice = 15000,
                    costPrice = 7000,
                    unit = "cup",
                    initialStock = 100,
                    trackStock = true,
                ),
            ),
        )
        assertEquals(
            """{"items":[{"productId":null,"productName":"Kopi Susu","productSku":"KPS-001","quantity":2,"unitPrice":15000,"discountAmount":0}],"discountAmount":0,"taxRate":0,"paymentMethod":"CASH","paidAmount":50000,"customerName":"Pak Budi","notes":null}""",
            requestJson.encodeToString(
                TransactionCreateRequest.serializer(),
                TransactionCreateRequest(
                    items = listOf(
                        TransactionItemRequest(
                            productName = "Kopi Susu",
                            productSku = "KPS-001",
                            quantity = 2,
                            unitPrice = 15000,
                            discountAmount = 0,
                        ),
                    ),
                    discountAmount = 0,
                    taxRate = 0,
                    paymentMethod = "CASH",
                    paidAmount = 50000,
                    customerName = "Pak Budi",
                ),
            ),
        )
        assertEquals(
            """{"lastSyncTimestamp":"2024-01-01T00:00:00Z","clientChanges":[]}""",
            requestJson.encodeToString(
                SyncRequest.serializer(),
                SyncRequest("2024-01-01T00:00:00Z"),
            ),
        )
    }

    @Test
    fun login_response_parsing_reads_access_token_from_tolerant_envelope() {
        val response = tolerantJson.decodeFromString(
            AuthLoginResponse.serializer(),
            """{"success":true,"data":{"accessToken":"token-123","refreshToken":"ignored","expiresIn":3600},"message":"ok","unknownRoot":"ignored"}""",
        )

        assertEquals(true, response.success)
        assertEquals("token-123", response.data?.accessToken)
    }

    @Test
    fun unknown_response_fields_are_ignored_and_missing_token_stays_null() {
        val response = tolerantJson.decodeFromString(
            AuthLoginResponse.serializer(),
            """{"success":false,"data":{"refreshToken":"no-access-token"},"extraRoot":"ignored"}""",
        )

        assertEquals(false, response.success)
        assertNull(response.data?.accessToken)
    }
}
