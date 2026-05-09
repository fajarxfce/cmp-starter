package com.fajar.kmp.feature.auth.domain.repository

import com.fajar.kmp.feature.auth.domain.model.AuthResult
import com.fajar.kmp.feature.auth.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeSession(): Flow<AuthSession?>
    suspend fun login(email: String, password: String): AuthResult<AuthSession>
    suspend fun logout()
    suspend fun refreshToken(): AuthResult<AuthSession>
}
