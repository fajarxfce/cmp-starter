package com.fajar.kmp.feature.todo.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class TodoDto(
    val id: String,
    val title: String,
    val completed: Boolean,
    val version: Long,
)

interface TodoRemoteDataSource {
    suspend fun fetchTodos(): List<TodoDto>
    suspend fun pushTodos(todos: List<TodoDto>): List<TodoDto>
}

class FakeTodoRemoteDataSource : TodoRemoteDataSource {
    private val remoteTodos = mutableListOf<TodoDto>()

    override suspend fun fetchTodos(): List<TodoDto> = remoteTodos.toList()

    override suspend fun pushTodos(todos: List<TodoDto>): List<TodoDto> {
        todos.forEach { incoming ->
            remoteTodos.removeAll { it.id == incoming.id }
            remoteTodos += incoming
        }
        return fetchTodos()
    }
}
