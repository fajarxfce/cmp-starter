package com.fajar.kmp.core.navigation

import org.koin.dsl.module

val navigationModule = module {
    factory { AppNavigator() }
}
