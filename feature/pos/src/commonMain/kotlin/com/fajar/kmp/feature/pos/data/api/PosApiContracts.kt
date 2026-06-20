package com.fajar.kmp.feature.pos.data.api

import kotlinx.serialization.Serializable
import com.fajar.kmp.core.network.NetworkJson
import kotlinx.serialization.json.JsonElement

object PosApiJson {
    val tolerant = NetworkJson.tolerant
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
data class StoreSummaryResponse(
    val id: String? = null,
    val name: String? = null,
    val slug: String? = null,
    val ownerUserId: String? = null,
    val isActive: Boolean = false,
    val createdAt: String? = null,
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
