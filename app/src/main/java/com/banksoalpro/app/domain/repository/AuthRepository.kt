package com.banksoalpro.app.domain.repository

import com.banksoalpro.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    suspend fun getCurrentUser(): UserProfile?
    suspend fun signInWithGoogle(displayName: String, email: String, photoUrl: String? = null): Result<UserProfile>
    suspend fun signInAsGuest(): Result<UserProfile>
    suspend fun updateTargetExamCategory(category: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
}
