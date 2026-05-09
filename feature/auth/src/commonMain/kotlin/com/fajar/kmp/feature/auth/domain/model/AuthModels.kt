package com.fajar.kmp.feature.auth.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class UserId(val value: String)

data class AuthUser(
    val id: UserId,
    val email: String,
    val displayName: String,
)

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)

data class AuthSession(
    val user: AuthUser,
    val tokens: AuthTokens,
)

sealed interface AuthError {
    data object InvalidCredentials : AuthError
    data object MissingSession : AuthError
    data class Unknown(val message: String) : AuthError
}

sealed interface AuthResult<out T> {
    data class Success<T>(val value: T) : AuthResult<T>
    data class Failure(val error: AuthError) : AuthResult<Nothing>
}
