package com.banksoalpro.app.data.repository

import com.banksoalpro.app.data.local.dao.UserAccountDao
import com.banksoalpro.app.data.local.entity.UserAccountEntity
import com.banksoalpro.app.domain.model.UserProfile
import com.banksoalpro.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class AuthRepositoryImpl(
    private val userAccountDao: UserAccountDao
) : AuthRepository {

    override val currentUser: Flow<UserProfile?> = userAccountDao.getActiveUser().map { entity ->
        entity?.toProfile()
    }

    override suspend fun getCurrentUser(): UserProfile? {
        var user = userAccountDao.getActiveUserSync()
        if (user == null) {
            // First run: automatically initialize guest mode
            val guestResult = signInAsGuest()
            user = userAccountDao.getActiveUserSync()
        }
        return user?.toProfile()
    }

    override suspend fun signInWithGoogle(
        displayName: String,
        email: String,
        photoUrl: String?
    ): Result<UserProfile> {
        return try {
            val current = userAccountDao.getActiveUserSync()
            val existingUnlockedJson = current?.unlockedPackageIdsJson ?: "[]"
            val existingTargetCategory = current?.targetExamCategory ?: "CPNS_SKD"
            val existingIsPremium = current?.isPremiumUser ?: false
            val existingExpiry = current?.premiumExpiryDate

            // Deactivate all existing users
            userAccountDao.deactivateAllUsers()

            val userId = "google_${email.replace("[^a-zA-Z0-9]".toRegex(), "_")}"
            val existingAccount = userAccountDao.getUserById(userId)

            val updatedAccount = if (existingAccount != null) {
                existingAccount.copy(
                    displayName = displayName.ifBlank { existingAccount.displayName },
                    photoUrl = photoUrl ?: existingAccount.photoUrl,
                    isActive = true
                )
            } else {
                UserAccountEntity(
                    userId = userId,
                    displayName = displayName,
                    email = email,
                    photoUrl = photoUrl,
                    isGuest = false,
                    targetExamCategory = existingTargetCategory,
                    isPremiumUser = existingIsPremium,
                    unlockedPackageIdsJson = existingUnlockedJson,
                    premiumExpiryDate = existingExpiry,
                    lastSyncedAt = System.currentTimeMillis(),
                    isActive = true
                )
            }

            userAccountDao.insertOrUpdate(updatedAccount)
            Result.success(updatedAccount.toProfile())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInAsGuest(): Result<UserProfile> {
        return try {
            userAccountDao.deactivateAllUsers()
            val guestId = "guest_" + UUID.randomUUID().toString().take(8)
            val guestUser = UserAccountEntity(
                userId = guestId,
                displayName = "Pengguna Tamu",
                email = null,
                photoUrl = null,
                isGuest = true,
                targetExamCategory = "CPNS_SKD",
                isPremiumUser = false,
                unlockedPackageIdsJson = "[]",
                premiumExpiryDate = null,
                lastSyncedAt = 0L,
                isActive = true
            )
            userAccountDao.insertOrUpdate(guestUser)
            Result.success(guestUser.toProfile())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTargetExamCategory(category: String): Result<Unit> {
        return try {
            val current = userAccountDao.getActiveUserSync()
            if (current != null) {
                userAccountDao.updateTargetCategory(current.userId, category)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            userAccountDao.deactivateAllUsers()
            signInAsGuest()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
