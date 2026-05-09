package com.fajar.kmp.feature.auth

import app.cash.turbine.test
import com.fajar.kmp.core.datastore.InMemoryKeyValueStore
import com.fajar.kmp.feature.auth.data.AuthRepositoryImpl
import com.fajar.kmp.feature.auth.domain.model.AuthResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class AuthRepositoryTest {
    @Test
    fun login_persists_tokens_and_updates_session() = runTest {
        val store = InMemoryKeyValueStore()
        val repository = AuthRepositoryImpl(store)

        repository.observeSession().test {
            assertEquals(null, awaitItem())
            val result = repository.login("user@example.com", "password123")
            assertTrue(result is AuthResult.Success)
            assertEquals("user@example.com", awaitItem()?.user?.email)
            assertEquals((result as AuthResult.Success).value.tokens.accessToken, store.getString("auth.accessToken"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
