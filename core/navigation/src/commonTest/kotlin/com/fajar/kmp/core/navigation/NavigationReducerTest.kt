package com.fajar.kmp.core.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationReducerTest {
    private val reducer = NavigationReducer()

    @Test
    fun push_adds_route_to_back_stack() {
        val state = reducer.reduce(AppBackStack(), NavigationAction.Push(AppRoute.AddTodo))

        assertEquals(listOf<AppRoute>(AppRoute.TodoList, AppRoute.AddTodo), state.entries.toList())
    }

    @Test
    fun pop_keeps_start_destination() {
        val state = reducer.reduce(AppBackStack(), NavigationAction.Pop)

        assertEquals(listOf<AppRoute>(AppRoute.TodoList), state.entries.toList())
    }
}
