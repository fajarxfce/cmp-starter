package com.fajar.kmp.core.analytics

interface AnalyticsTracker {
    fun track(event: String, properties: Map<String, String> = emptyMap())
}

class NoOpAnalyticsTracker : AnalyticsTracker {
    override fun track(event: String, properties: Map<String, String>) = Unit
}

data class AnalyticsEvent(
    val name: String,
    val properties: Map<String, String> = emptyMap(),
)

class CompositeAnalyticsTracker(
    private val trackers: List<AnalyticsTracker>,
) : AnalyticsTracker {
    override fun track(event: String, properties: Map<String, String>) {
        trackers.forEach { it.track(event, properties) }
    }
}

class AnalyticsEventLogger(private val tracker: AnalyticsTracker) {
    fun log(event: AnalyticsEvent) {
        tracker.track(event.name, event.properties)
    }
}
