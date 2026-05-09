package com.fajar.kmp.feature.home

data class HomeDashboardItem(
    val title: String,
    val subtitle: String,
)

class HomeDashboardProvider {
    fun items(): List<HomeDashboardItem> = listOf(
        HomeDashboardItem("Todos", "Local-first MVI example"),
        HomeDashboardItem("Sync", "Queued writes and retry status"),
        HomeDashboardItem("Auth", "Session guard example"),
    )
}
