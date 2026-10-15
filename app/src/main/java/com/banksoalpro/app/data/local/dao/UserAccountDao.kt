package com.banksoalpro.app.data.local.dao

import androidx.room.*
import com.banksoalpro.app.data.local.entity.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_account WHERE isActive = 1 LIMIT 1")
    fun getActiveUser(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_account WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveUserSync(): UserAccountEntity?

    @Query("SELECT * FROM user_account WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserAccountEntity)

    @Query("UPDATE user_account SET isActive = 0")
    suspend fun deactivateAllUsers()

    @Query("UPDATE user_account SET isPremiumUser = :isPremium, unlockedPackageIdsJson = :unlockedJson, premiumExpiryDate = :expiryDate, lastSyncedAt = :syncedAt WHERE userId = :userId")
    suspend fun updateEntitlement(userId: String, isPremium: Boolean, unlockedJson: String, expiryDate: Long?, syncedAt: Long)

    @Query("UPDATE user_account SET targetExamCategory = :targetCategory WHERE userId = :userId")
    suspend fun updateTargetCategory(userId: String, targetCategory: String)

    @Query("DELETE FROM user_account WHERE userId = :userId")
    suspend fun deleteUser(userId: String)
}
