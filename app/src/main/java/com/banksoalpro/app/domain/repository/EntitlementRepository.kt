package com.banksoalpro.app.domain.repository

import com.banksoalpro.app.domain.model.CloudSyncState
import com.banksoalpro.app.domain.model.UserEntitlement
import kotlinx.coroutines.flow.Flow

interface EntitlementRepository {
    val currentEntitlement: Flow<UserEntitlement>
    suspend fun getEntitlement(userId: String): UserEntitlement
    suspend fun unlockPackage(userId: String, packageId: String): Result<Unit>
    suspend fun setPremiumStatus(userId: String, isPremium: Boolean, expiryDate: Long?): Result<Unit>
    suspend fun syncWithCloud(userId: String): Result<UserEntitlement>
    suspend fun isPackageUnlocked(userId: String, packageId: String, isFreePackage: Boolean): Boolean
}
