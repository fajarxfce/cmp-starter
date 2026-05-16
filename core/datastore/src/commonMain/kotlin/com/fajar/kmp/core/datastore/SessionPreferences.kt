package com.fajar.kmp.core.datastore

import kotlinx.coroutines.flow.Flow

interface SessionPreferences {
    fun observeAccessToken(): Flow<String?>
    suspend fun getAccessToken(): String?
    suspend fun saveAccessToken(accessToken: String?)
    suspend fun clearAccessToken()
    fun observeActiveStoreId(): Flow<String?>
    suspend fun getActiveStoreId(): String?
    suspend fun saveActiveStoreId(activeStoreId: String?)
    suspend fun clearActiveStoreId()
    suspend fun clearSession()
}

class KeyValueSessionPreferences(
    private val preferencesStore: KeyValueStore,
    private val secureStore: KeyValueStore = preferencesStore,
) : SessionPreferences {
    override fun observeAccessToken(): Flow<String?> = secureStore.observeString(ACCESS_TOKEN_KEY)

    override suspend fun getAccessToken(): String? = secureStore.getString(ACCESS_TOKEN_KEY)

    override suspend fun saveAccessToken(accessToken: String?) {
        secureStore.putString(ACCESS_TOKEN_KEY, accessToken)
    }

    override suspend fun clearAccessToken() {
        secureStore.putString(ACCESS_TOKEN_KEY, null)
    }

    override fun observeActiveStoreId(): Flow<String?> = preferencesStore.observeString(ACTIVE_STORE_ID_KEY)

    override suspend fun getActiveStoreId(): String? = preferencesStore.getString(ACTIVE_STORE_ID_KEY)

    override suspend fun saveActiveStoreId(activeStoreId: String?) {
        preferencesStore.putString(ACTIVE_STORE_ID_KEY, activeStoreId)
    }

    override suspend fun clearActiveStoreId() {
        preferencesStore.putString(ACTIVE_STORE_ID_KEY, null)
    }

    override suspend fun clearSession() {
        clearAccessToken()
        clearActiveStoreId()
    }

    private companion object {
        const val ACCESS_TOKEN_KEY = "session.accessToken"
        const val ACTIVE_STORE_ID_KEY = "session.activeStoreId"
    }
}
