package com.fajar.kmp.feature.todo.presentation.viewmodel

import com.fajar.kmp.core.common.DomainError
import com.fajar.kmp.core.model.TodoId
import com.fajar.kmp.feature.todo.domain.usecase.AddTodoUseCase
import com.fajar.kmp.feature.todo.domain.usecase.DeleteTodoUseCase
import com.fajar.kmp.feature.todo.domain.usecase.ObserveTodosUseCase
import com.fajar.kmp.feature.todo.domain.usecase.SyncTodosUseCase
import com.fajar.kmp.feature.todo.domain.usecase.ToggleTodoUseCase
import com.fajar.kmp.feature.todo.presentation.contract.TodoEffect
import com.fajar.kmp.feature.todo.presentation.contract.TodoIntent
import com.fajar.kmp.feature.todo.presentation.contract.TodoPartialChange
import com.fajar.kmp.feature.todo.presentation.contract.TodoReducer
import com.fajar.kmp.feature.todo.presentation.contract.TodoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel(
    private val observeTodos: ObserveTodosUseCase,
    private val addTodo: AddTodoUseCase,
    private val toggleTodo: ToggleTodoUseCase,
    private val deleteTodo: DeleteTodoUseCase,
    private val syncTodos: SyncTodosUseCase,
    private val reducer: TodoReducer = TodoReducer(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val mutableState = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = mutableState.asStateFlow()

    private val mutableEffect = MutableSharedFlow<TodoEffect>()
    val effect: SharedFlow<TodoEffect> = mutableEffect.asSharedFlow()

    fun onIntent(intent: TodoIntent) {
        when (intent) {
            TodoIntent.LoadTodos -> loadTodos()
            is TodoIntent.AddTodo -> add(intent.title)
            is TodoIntent.ToggleTodo -> toggle(intent.id)
            is TodoIntent.DeleteTodo -> delete(intent.id)
            is TodoIntent.OpenDetail -> emitDetail(intent.id)
            TodoIntent.Refresh -> refresh()
        }
    }

    private fun loadTodos() {
        applyChange(TodoPartialChange.Loading)
        scope.launch {
            observeTodos().collect { todos ->
                applyChange(TodoPartialChange.TodosLoaded(todos))
            }
        }
    }

    private fun add(title: String) {
        if (title.isBlank()) {
            applyChange(TodoPartialChange.Error(DomainError.Validation("Todo title cannot be blank")))
            return
        }
        scope.launch {
            addTodo(title)
            mutableEffect.emit(TodoEffect.ShowSnackbar("Todo added"))
        }
    }

    private fun toggle(id: TodoId) {
        scope.launch { toggleTodo(id) }
    }

    private fun delete(id: TodoId) {
        scope.launch {
            deleteTodo(id)
            mutableEffect.emit(TodoEffect.ShowSnackbar("Todo deleted"))
        }
    }

    private fun refresh() {
        scope.launch {
            applyChange(TodoPartialChange.SyncStatusChanged(syncTodos()))
        }
    }

    private fun emitDetail(id: TodoId) {
        scope.launch { mutableEffect.emit(TodoEffect.NavigateToDetail(id)) }
    }

    private fun applyChange(change: TodoPartialChange) {
        mutableState.value = reducer.reduce(mutableState.value, change)
    }
}
