package com.banksoalpro.app.data.remote.sync

import com.banksoalpro.app.domain.model.ExamAttempt
import com.banksoalpro.app.domain.model.UserEntitlement

interface RemoteEntitlementGateway {
    suspend fun fetchCloudEntitlement(userId: String): Result<UserEntitlement>
    suspend fun pushEntitlement(entitlement: UserEntitlement): Result<Unit>
    suspend fun syncAttempts(userId: String, localAttempts: List<ExamAttempt>): Result<List<ExamAttempt>>
}

class RemoteEntitlementGatewayImpl : RemoteEntitlementGateway {
    override suspend fun fetchCloudEntitlement(userId: String): Result<UserEntitlement> {
        // Cloud simulator / REST Gateway
        return Result.success(
            UserEntitlement(
                userId = userId,
                isPremiumUser = false,
                unlockedPackageIds = emptyList(),
                premiumExpiryDate = null,
                lastSyncedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun pushEntitlement(entitlement: UserEntitlement): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun syncAttempts(userId: String, localAttempts: List<ExamAttempt>): Result<List<ExamAttempt>> {
        return Result.success(localAttempts)
    }
}
