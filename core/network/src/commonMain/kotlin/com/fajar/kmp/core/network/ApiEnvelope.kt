package com.fajar.kmp.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseEnvelope<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiErrorResponse? = null,
)

@Serializable
data class ApiErrorResponse(
    val code: String,
    val message: String,
)
