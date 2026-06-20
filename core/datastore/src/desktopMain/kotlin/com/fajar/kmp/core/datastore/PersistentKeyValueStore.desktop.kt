package com.fajar.kmp.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import java.io.File
import okio.Path.Companion.toOkioPath

actual fun createDataStore(name: String): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
    produceFile = {
        File(System.getProperty("user.home"), ".cmp-starter/datastore/$name")
            .also { it.parentFile?.mkdirs() }
            .toOkioPath()
    },
)
