package com.fajar.kmp.feature.todo

import com.fajar.kmp.core.model.Todo
import com.fajar.kmp.core.model.TodoId
import com.fajar.kmp.feature.todo.presentation.contract.TodoPartialChange
import com.fajar.kmp.feature.todo.presentation.contract.TodoReducer
import com.fajar.kmp.feature.todo.presentation.contract.TodoState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Instant

class TodoReducerTest {
    private val reducer = TodoReducer()

    @Test
    fun loading_marks_state_as_loading() {
        val state = reducer.reduce(TodoState(), TodoPartialChange.Loading)

        assertEquals(true, state.isLoading)
    }

    @Test
    fun todos_loaded_maps_domain_to_ui() {
        val todo = Todo(
            id = TodoId("1"),
            title = "Write reducer test",
            isCompleted = false,
            isDirty = true,
            isDeleted = false,
            localVersion = 1,
            serverVersion = null,
            lastSyncedAt = Instant.fromEpochMilliseconds(0),
        )

        val state = reducer.reduce(TodoState(isLoading = true), TodoPartialChange.TodosLoaded(listOf(todo)))

        assertEquals(false, state.isLoading)
        assertEquals("Write reducer test", state.todos.single().title)
        assertEquals(true, state.todos.single().isDirty)
    }
}
