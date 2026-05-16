package com.fajar.kmp.core.datastore

import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest

class SessionPreferencesTest {
    @Test
    fun access_token_observes_save_and_clear_lifecycle() = runTest {
        val preferences = KeyValueSessionPreferences(
            preferencesStore = InMemoryKeyValueStore(),
            secureStore = InMemoryKeyValueStore(),
        )

        preferences.observeAccessToken().test {
            assertNull(awaitItem())

            preferences.saveAccessToken("abc")
            assertEquals("abc", awaitItem())

            preferences.clearAccessToken()
            assertNull(awaitItem())
        }
    }

    @Test
    fun active_store_id_observes_save_and_clear_lifecycle() = runTest {
        val preferences = KeyValueSessionPreferences(InMemoryKeyValueStore())

        preferences.observeActiveStoreId().test {
            assertNull(awaitItem())

            preferences.saveActiveStoreId("store-1")
            assertEquals("store-1", awaitItem())

            preferences.clearActiveStoreId()
            assertNull(awaitItem())
        }
    }

    @Test
    fun clear_session_removes_token_and_active_store_without_requiring_existing_values() = runTest {
        val preferences = KeyValueSessionPreferences(
            preferencesStore = InMemoryKeyValueStore(),
            secureStore = InMemoryKeyValueStore(),
        )

        preferences.clearSession()
        assertNull(preferences.getAccessToken())
        assertNull(preferences.getActiveStoreId())

        preferences.saveAccessToken("abc")
        preferences.saveActiveStoreId("store-1")
        preferences.clearSession()

        assertNull(preferences.getAccessToken())
        assertNull(preferences.getActiveStoreId())
    }

    @Test
    fun saving_null_values_is_null_safe_and_clears_existing_session_values() = runTest {
        val preferences = KeyValueSessionPreferences(
            preferencesStore = InMemoryKeyValueStore(),
            secureStore = InMemoryKeyValueStore(),
        )

        preferences.saveAccessToken(null)
        preferences.saveActiveStoreId(null)
        assertNull(preferences.getAccessToken())
        assertNull(preferences.getActiveStoreId())

        preferences.saveAccessToken("abc")
        preferences.saveActiveStoreId("store-1")
        preferences.saveAccessToken(null)
        preferences.saveActiveStoreId(null)

        assertNull(preferences.getAccessToken())
        assertNull(preferences.getActiveStoreId())
    }

    @Test
    fun session_facade_writes_only_session_keys_not_pos_collections() = runTest {
        val preferencesStore = RecordingKeyValueStore()
        val secureStore = RecordingKeyValueStore()
        val preferences = KeyValueSessionPreferences(
            preferencesStore = preferencesStore,
            secureStore = secureStore,
        )

        preferences.saveAccessToken("abc")
        preferences.saveActiveStoreId("store-1")
        preferences.clearSession()

        val writtenKeys = preferencesStore.writtenKeys + secureStore.writtenKeys
        assertEquals(setOf("session.accessToken", "session.activeStoreId"), writtenKeys)
        assertEquals(emptySet(), writtenKeys.filter { it.contains("product") || it.contains("category") || it.contains("transaction") }.toSet())
    }

    private class RecordingKeyValueStore : KeyValueStore {
        private val values = MutableStateFlow<Map<String, String>>(emptyMap())
        val writtenKeys = mutableSetOf<String>()

        override fun observeString(key: String): Flow<String?> = values.map { it[key] }

        override suspend fun getString(key: String): String? = values.value[key]

        override suspend fun putString(key: String, value: String?) {
            writtenKeys += key
            values.value = if (value == null) values.value - key else values.value + (key to value)
        }

        override suspend fun clear() {
            values.value = emptyMap()
        }
    }
}
