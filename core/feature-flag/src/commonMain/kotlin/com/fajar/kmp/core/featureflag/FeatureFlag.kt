package com.fajar.kmp.core.featureflag

enum class FeatureFlag {
    MockMode,
    Analytics,
    CrashReporting,
}

interface FeatureFlagProvider {
    fun isEnabled(flag: FeatureFlag): Boolean
}

class StaticFeatureFlagProvider(
    private val flags: Map<FeatureFlag, Boolean>,
) : FeatureFlagProvider {
    override fun isEnabled(flag: FeatureFlag): Boolean = flags[flag] == true
}

class MutableFeatureFlagProvider(
    initialFlags: Map<FeatureFlag, Boolean> = emptyMap(),
) : FeatureFlagProvider {
    private val flags = initialFlags.toMutableMap()

    override fun isEnabled(flag: FeatureFlag): Boolean = flags[flag] == true

    fun set(flag: FeatureFlag, enabled: Boolean) {
        flags[flag] = enabled
    }
}
