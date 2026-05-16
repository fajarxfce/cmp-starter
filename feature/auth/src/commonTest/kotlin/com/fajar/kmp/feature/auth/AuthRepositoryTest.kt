package com.fajar.kmp.feature.auth

import app.cash.turbine.test
import com.fajar.kmp.core.datastore.InMemoryKeyValueStore
import com.fajar.kmp.core.datastore.KeyValueSessionPreferences
import com.fajar.kmp.feature.auth.data.AuthRepositoryImpl
import com.fajar.kmp.feature.auth.domain.model.AuthResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class AuthRepositoryTest {
    @Test
    fun login_persists_access_token_and_updates_session() = runTest {
        val preferences = KeyValueSessionPreferences(InMemoryKeyValueStore())
        val repository = AuthRepositoryImpl(preferences)

        repository.observeSession().test {
            assertNull(awaitItem())
            val result = repository.login("user@example.com", "password123")
            assertTrue(result is AuthResult.Success)
            assertEquals("user@example.com", awaitItem()?.user?.email)
            assertEquals((result as AuthResult.Success).value.tokens.accessToken, preferences.getAccessToken())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun logout_clears_access_token_active_store_and_session() = runTest {
        val preferences = KeyValueSessionPreferences(InMemoryKeyValueStore())
        val repository = AuthRepositoryImpl(preferences)

        repository.login("user@example.com", "password123")
        preferences.saveActiveStoreId("store-1")

        repository.observeSession().test {
            assertEquals("user@example.com", awaitItem()?.user?.email)

            repository.logout()

            assertNull(awaitItem())
            assertNull(preferences.getAccessToken())
            assertNull(preferences.getActiveStoreId())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
