package com.fajar.kmp.core.network.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object PosApiPaths {
    const val authRegister = "/api/v1/auth/register"
    const val authLogin = "/api/v1/auth/login"
    const val storeRegister = "/api/v1/stores/register"
    const val adminStats = "/api/v1/admin/stats"
    const val adminStores = "/api/v1/admin/stores"
    const val adminUsers = "/api/v1/admin/users"

    fun storeCategories(storeId: String): String = "/api/v1/stores/$storeId/categories"
    fun storeProducts(storeId: String): String = "/api/v1/stores/$storeId/products"
    fun storeTransactions(storeId: String): String = "/api/v1/stores/$storeId/transactions"
    fun storeSync(storeId: String): String = "/api/v1/stores/$storeId/sync"
}

object PosApiJson {
    val tolerant: Json = Json {
        ignoreUnknownKeys = true
    }
}

@Serializable
data class AuthRegisterRequest(
    val email: String,
    val fullName: String,
    val password: String,
    val phone: String? = null,
    val tenantId: String? = null,
)

@Serializable
data class AuthLoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class StoreRegisterRequest(
    val name: String,
    val slug: String,
    val description: String,
    val timezone: String,
    val country: String,
    val phone: String,
    val email: String,
)

@Serializable
data class CategoryCreateRequest(
    val name: String,
    val description: String,
    val color: String,
    val icon: String,
)

@Serializable
data class ProductCreateRequest(
    val categoryId: String? = null,
    val name: String,
    val sku: String,
    val barcode: String,
    val sellingPrice: Long,
    val costPrice: Long,
    val unit: String,
    val initialStock: Int,
    val trackStock: Boolean,
)

@Serializable
data class TransactionItemRequest(
    val productId: String? = null,
    val productName: String,
    val productSku: String,
    val quantity: Int,
    val unitPrice: Long,
    val discountAmount: Long,
)

@Serializable
data class TransactionCreateRequest(
    val items: List<TransactionItemRequest>,
    val discountAmount: Long,
    val taxRate: Int,
    val paymentMethod: String,
    val paidAmount: Long,
    val customerName: String,
    val notes: String? = null,
)

@Serializable
data class SyncRequest(
    val lastSyncTimestamp: String,
    val clientChanges: List<JsonElement> = emptyList(),
)

@Serializable
data class AuthTokenData(
    val accessToken: String? = null,
)

@Serializable
data class AuthLoginResponse(
    val success: Boolean? = null,
    val data: AuthTokenData? = null,
    val message: String? = null,
    val error: String? = null,
)

@Serializable
data class AuthRegisterResponse(
    val success: Boolean? = null,
    val data: AuthTokenData? = null,
    val message: String? = null,
    val error: String? = null,
)

@Serializable
data class PosApiEnvelope<T>(
    val success: Boolean? = null,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null,
)

typealias StoreResponseEnvelope = PosApiEnvelope<JsonElement>
typealias CategoryResponseEnvelope = PosApiEnvelope<JsonElement>
typealias ProductResponseEnvelope = PosApiEnvelope<JsonElement>
typealias TransactionResponseEnvelope = PosApiEnvelope<JsonElement>
typealias SyncResponseEnvelope = PosApiEnvelope<JsonElement>
typealias AdminResponseEnvelope = PosApiEnvelope<JsonElement>
