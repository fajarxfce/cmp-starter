package com.fajar.kmp.core.datastore

import org.koin.dsl.module

val datastoreModule = module {
    single { InMemoryKeyValueStore() }
    single<KeyValueStore> { get<InMemoryKeyValueStore>() }
    single { SecureKeyValueStore(get<InMemoryKeyValueStore>()) }
    single<SessionPreferences> {
        KeyValueSessionPreferences(
            preferencesStore = get<InMemoryKeyValueStore>(),
            secureStore = get<SecureKeyValueStore>(),
        )
    }
}
