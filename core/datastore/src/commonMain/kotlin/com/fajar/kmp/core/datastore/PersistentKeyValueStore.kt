package com.fajar.kmp.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Storage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

fun createDataStore(storage: Storage<Preferences>): DataStore<Preferences> =
    DataStoreFactory.create(storage = storage)

internal const val dataStoreFileName = "cmp_pos.preferences_pb"

expect fun createDataStore(name: String = dataStoreFileName): DataStore<Preferences>

fun persistentKeyValueStore(name: String): KeyValueStore = DataStoreKeyValueStore(createDataStore("$name.preferences_pb"))

private class DataStoreKeyValueStore(
    private val dataStore: DataStore<Preferences>,
) : KeyValueStore {
    override fun observeString(key: String): Flow<String?> = dataStore.data.map { preferences ->
        preferences[stringPreferencesKey(key)]
    }

    override suspend fun getString(key: String): String? = dataStore.data.first()[stringPreferencesKey(key)]

    override suspend fun putString(key: String, value: String?) {
        dataStore.edit { preferences ->
            val preferenceKey = stringPreferencesKey(key)
            if (value == null) preferences.remove(preferenceKey) else preferences[preferenceKey] = value
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
