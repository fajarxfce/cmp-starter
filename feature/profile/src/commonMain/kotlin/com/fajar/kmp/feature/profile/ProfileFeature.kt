package com.fajar.kmp.feature.profile

data class ProfileSummary(
    val email: String,
    val displayName: String,
)

class ProfileFormatter {
    fun initials(profile: ProfileSummary): String = profile.displayName
        .split(' ')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
}
