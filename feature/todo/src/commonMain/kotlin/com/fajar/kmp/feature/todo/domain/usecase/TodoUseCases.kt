package com.fajar.kmp.feature.todo.domain.usecase

import com.fajar.kmp.core.model.TodoId
import com.fajar.kmp.feature.todo.domain.repository.TodoRepository

class ObserveTodosUseCase(private val repository: TodoRepository) {
    operator fun invoke() = repository.observeTodos()
}

class AddTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(title: String) = repository.addTodo(title.trim())
}

class ToggleTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(id: TodoId) = repository.toggleTodo(id)
}

class DeleteTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(id: TodoId) = repository.deleteTodo(id)
}

class SyncTodosUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke() = repository.syncTodos()
}
