package com.fajar.kmp.core.datastore

import org.koin.dsl.module

val datastoreModule = module {
    single<KeyValueStore> { persistentKeyValueStore("cmp_pos_preferences") }
    single { SecureKeyValueStore(persistentKeyValueStore("cmp_pos_secure")) }
    single<SessionPreferences> {
        KeyValueSessionPreferences(
            preferencesStore = get<KeyValueStore>(),
            secureStore = get<SecureKeyValueStore>(),
        )
    }
}
