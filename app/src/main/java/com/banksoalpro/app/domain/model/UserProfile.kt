package com.banksoalpro.app.domain.model

data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String?,
    val photoUrl: String? = null,
    val isGuest: Boolean = false,
    val targetExamCategory: String = "CPNS_SKD"
) {
    val initial: String
        get() = displayName.firstOrNull()?.uppercase() ?: "U"
}
