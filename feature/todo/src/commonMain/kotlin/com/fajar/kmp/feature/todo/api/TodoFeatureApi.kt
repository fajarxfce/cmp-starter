package com.fajar.kmp.feature.todo.api

import com.fajar.kmp.core.navigation.AppRoute
import com.fajar.kmp.core.navigation.FeatureEntry

object TodoListEntry : FeatureEntry {
    override val route: AppRoute = AppRoute.Dashboard
}

object AddTodoEntry : FeatureEntry {
    override val route: AppRoute = AppRoute.Catalog
}
