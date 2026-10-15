package com.banksoalpro.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey val userId: String,
    val displayName: String,
    val email: String?,
    val photoUrl: String? = null,
    val isGuest: Boolean = false,
    val targetExamCategory: String = "CPNS_SKD",
    val isPremiumUser: Boolean = false,
    val unlockedPackageIdsJson: String = "[]",
    val premiumExpiryDate: Long? = null,
    val lastSyncedAt: Long = 0L,
    val isActive: Boolean = true
)
