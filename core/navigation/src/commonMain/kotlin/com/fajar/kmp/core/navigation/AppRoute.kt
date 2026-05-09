package com.fajar.kmp.core.navigation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
    @Serializable
    data object TodoList : AppRoute

    @Serializable
    data class TodoDetail(val id: String) : AppRoute

    @Serializable
    data object AddTodo : AppRoute

}

data class AppBackStack(
    val entries: ImmutableList<AppRoute> = persistentListOf(AppRoute.TodoList),
)

sealed interface NavigationAction {
    data class Push(val route: AppRoute) : NavigationAction
    data object Pop : NavigationAction
    data class ReplaceAll(val route: AppRoute) : NavigationAction
}

class NavigationReducer {
    fun reduce(backStack: AppBackStack, action: NavigationAction): AppBackStack = when (action) {
        is NavigationAction.Push -> backStack.copy(entries = (backStack.entries + action.route).toImmutableList())
        NavigationAction.Pop -> if (backStack.entries.size > 1) {
            backStack.copy(entries = backStack.entries.dropLast(1).toImmutableList())
        } else {
            backStack
        }
        is NavigationAction.ReplaceAll -> AppBackStack(persistentListOf(action.route))
    }
}

interface Navigator {
    val backStack: AppBackStack
    fun dispatch(action: NavigationAction)
}

interface FeatureEntry {
    val route: AppRoute
}
