package com.fajar.kmp.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlin.test.Test
import kotlin.test.assertEquals

class DatastoreDependencyGuardTest {
    @Test
    fun preferences_datastore_types_are_available() {
        val key = stringPreferencesKey("token")

        assertEquals("token", key.name)
    }

    @Suppress("unused")
    private fun acceptsPreferencesStore(store: DataStore<Preferences>): DataStore<Preferences> = store
}
