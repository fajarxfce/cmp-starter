package com.fajar.kmp.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toOkioPath

private lateinit var dataStoreContext: Context

fun initializePersistentKeyValueStore(context: Context) {
    dataStoreContext = context.applicationContext
}

actual fun createDataStore(name: String): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
    produceFile = {
        check(::dataStoreContext.isInitialized) { "Persistent KeyValueStore must be initialized with an Android Context before use." }
        dataStoreContext.filesDir.resolve(name).toOkioPath()
    },
)
