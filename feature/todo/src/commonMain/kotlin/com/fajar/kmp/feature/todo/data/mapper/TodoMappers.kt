package com.fajar.kmp.feature.todo.data.mapper

import com.fajar.kmp.core.model.Todo
import com.fajar.kmp.core.model.TodoId
import com.fajar.kmp.feature.todo.data.local.TodoEntity
import com.fajar.kmp.feature.todo.data.remote.TodoDto
import kotlinx.datetime.Instant

fun TodoEntity.toDomain(): Todo = Todo(
    id = TodoId(id),
    title = title,
    isCompleted = isCompleted,
    isDirty = isDirty,
    isDeleted = isDeleted,
    localVersion = localVersion,
    serverVersion = serverVersion,
    lastSyncedAt = lastSyncedAtEpochMilliseconds?.let(Instant::fromEpochMilliseconds),
)

fun Todo.toEntity(): TodoEntity = TodoEntity(
    id = id.value,
    title = title,
    isCompleted = isCompleted,
    isDirty = isDirty,
    isDeleted = isDeleted,
    localVersion = localVersion,
    serverVersion = serverVersion,
    lastSyncedAtEpochMilliseconds = lastSyncedAt?.toEpochMilliseconds(),
)

fun TodoEntity.toDto(): TodoDto = TodoDto(
    id = id,
    title = title,
    completed = isCompleted,
    version = localVersion,
)

fun TodoDto.toEntity(): TodoEntity = TodoEntity(
    id = id,
    title = title,
    isCompleted = completed,
    isDirty = false,
    isDeleted = false,
    localVersion = version,
    serverVersion = version,
    lastSyncedAtEpochMilliseconds = null,
)
