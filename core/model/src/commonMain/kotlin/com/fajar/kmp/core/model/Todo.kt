package com.fajar.kmp.core.model

import kotlin.jvm.JvmInline
import kotlinx.datetime.Instant

@JvmInline
value class TodoId(val value: String)

data class Todo(
    val id: TodoId,
    val title: String,
    val isCompleted: Boolean,
    val isDirty: Boolean,
    val isDeleted: Boolean,
    val localVersion: Long,
    val serverVersion: Long?,
    val lastSyncedAt: Instant?,
)
