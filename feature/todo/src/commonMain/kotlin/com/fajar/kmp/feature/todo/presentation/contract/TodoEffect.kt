package com.fajar.kmp.feature.todo.presentation.contract

import com.fajar.kmp.core.model.TodoId

sealed interface TodoEffect {
    data class ShowSnackbar(val message: String) : TodoEffect
    data class NavigateToDetail(val id: TodoId) : TodoEffect
}
