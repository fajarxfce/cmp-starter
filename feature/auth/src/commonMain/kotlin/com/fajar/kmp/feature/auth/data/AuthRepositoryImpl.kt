package com.fajar.kmp.feature.auth.data

import com.fajar.kmp.core.datastore.KeyValueStore
import com.fajar.kmp.feature.auth.domain.model.AuthError
import com.fajar.kmp.feature.auth.domain.model.AuthResult
import com.fajar.kmp.feature.auth.domain.model.AuthSession
import com.fajar.kmp.feature.auth.domain.model.AuthTokens
import com.fajar.kmp.feature.auth.domain.model.AuthUser
import com.fajar.kmp.feature.auth.domain.model.UserId
import com.fajar.kmp.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AuthRepositoryImpl(
    private val tokenStore: KeyValueStore,
) : AuthRepository {
    private val session = MutableStateFlow<AuthSession?>(null)

    override fun observeSession(): Flow<AuthSession?> = session

    override suspend fun login(email: String, password: String): AuthResult<AuthSession> {
        if (email.isBlank() || password.length < 8) return AuthResult.Failure(AuthError.InvalidCredentials)

        val nextSession = AuthSession(
            user = AuthUser(UserId(email.lowercase()), email, email.substringBefore('@')),
            tokens = AuthTokens(
                accessToken = "access-${email.hashCode()}",
                refreshToken = "refresh-${password.hashCode()}",
            ),
        )
        persist(nextSession)
        session.value = nextSession
        return AuthResult.Success(nextSession)
    }

    override suspend fun logout() {
        tokenStore.clear()
        session.value = null
    }

    override suspend fun refreshToken(): AuthResult<AuthSession> {
        val current = session.value ?: return AuthResult.Failure(AuthError.MissingSession)
        val refreshed = current.copy(tokens = current.tokens.copy(accessToken = "access-refreshed-${current.user.id.value.hashCode()}"))
        persist(refreshed)
        session.value = refreshed
        return AuthResult.Success(refreshed)
    }

    private suspend fun persist(authSession: AuthSession) {
        tokenStore.putString("auth.accessToken", authSession.tokens.accessToken)
        tokenStore.putString("auth.refreshToken", authSession.tokens.refreshToken)
        tokenStore.putString("auth.email", authSession.user.email)
    }
}
