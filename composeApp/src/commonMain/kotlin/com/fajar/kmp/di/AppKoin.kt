package com.fajar.kmp.di

import com.fajar.kmp.AppRuntimeConfig
import com.fajar.kmp.core.common.AppConfig
import com.fajar.kmp.core.datastore.datastoreModule
import com.fajar.kmp.core.navigation.navigationModule
import com.fajar.kmp.core.network.networkModule
import com.fajar.kmp.feature.auth.di.authModule
import com.fajar.kmp.feature.pos.domain.repository.PosRepository
import com.fajar.kmp.feature.pos.di.posModule
import com.fajar.kmp.feature.todo.di.todoModule
import com.fajar.kmp.feature.pos.presentation.shell.PosShellViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

fun appConfigModule(config: AppConfig) = module {
    single<AppConfig> { config }
}

val appPosShellModule = module {
    includes(posModule)
    factory { PosShellViewModel(repository = get<PosRepository>(), sessionPreferences = get()) }
}

fun productionAppModules(
    config: AppConfig,
    additionalModules: List<Module> = emptyList(),
): List<Module> = listOf(
    appConfigModule(config),
    datastoreModule,
    networkModule,
    navigationModule,
    authModule,
    appPosShellModule,
    todoModule,
) + additionalModules

fun startAppKoin(config: AppConfig = AppRuntimeConfig.config) {
    if (KoinPlatformTools.defaultContext().getOrNull() == null) {
        startKoin {
            modules(productionAppModules(config))
        }
    }
}
