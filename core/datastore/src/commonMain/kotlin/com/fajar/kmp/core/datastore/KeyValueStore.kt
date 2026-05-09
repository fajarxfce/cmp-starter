package com.fajar.kmp.core.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

interface KeyValueStore {
    fun observeString(key: String): Flow<String?>
    suspend fun getString(key: String): String?
    suspend fun putString(key: String, value: String?)
    suspend fun clear()
}

class InMemoryKeyValueStore : KeyValueStore {
    private val values = MutableStateFlow<Map<String, String>>(emptyMap())

    override fun observeString(key: String): Flow<String?> = values.map { it[key] }

    override suspend fun getString(key: String): String? = values.value[key]

    override suspend fun putString(key: String, value: String?) {
        values.value = if (value == null) values.value - key else values.value + (key to value)
    }

    override suspend fun clear() {
        values.value = emptyMap()
    }
}

class SecureKeyValueStore(private val delegate: KeyValueStore) : KeyValueStore by delegate
