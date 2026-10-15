package com.banksoalpro.app.data.repository

import com.banksoalpro.app.data.local.dao.UserAccountDao
import com.banksoalpro.app.data.remote.sync.RemoteEntitlementGateway
import com.banksoalpro.app.domain.model.UserEntitlement
import com.banksoalpro.app.domain.repository.EntitlementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EntitlementRepositoryImpl(
    private val userAccountDao: UserAccountDao,
    private val remoteGateway: RemoteEntitlementGateway
) : EntitlementRepository {

    override val currentEntitlement: Flow<UserEntitlement> =
        userAccountDao.getActiveUser().map { entity ->
            entity?.toEntitlement() ?: UserEntitlement(userId = "")
        }

    override suspend fun getEntitlement(userId: String): UserEntitlement {
        val entity = userAccountDao.getUserById(userId) ?: userAccountDao.getActiveUserSync()
        return entity?.toEntitlement() ?: UserEntitlement(userId = userId)
    }

    override suspend fun unlockPackage(userId: String, packageId: String): Result<Unit> {
        return try {
            val account = userAccountDao.getUserById(userId) ?: userAccountDao.getActiveUserSync()
                ?: return Result.failure(IllegalStateException("No active user"))

            val currentList = parseJsonStringList(account.unlockedPackageIdsJson).toMutableList()
            if (!currentList.contains(packageId)) {
                currentList.add(packageId)
            }
            val updatedJson = currentList.toJsonString()

            userAccountDao.updateEntitlement(
                userId = account.userId,
                isPremium = account.isPremiumUser,
                unlockedJson = updatedJson,
                expiryDate = account.premiumExpiryDate,
                syncedAt = System.currentTimeMillis()
            )

            // Push to cloud if logged in
            if (!account.isGuest) {
                remoteGateway.pushEntitlement(
                    UserEntitlement(
                        userId = account.userId,
                        isPremiumUser = account.isPremiumUser,
                        unlockedPackageIds = currentList,
                        premiumExpiryDate = account.premiumExpiryDate,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setPremiumStatus(userId: String, isPremium: Boolean, expiryDate: Long?): Result<Unit> {
        return try {
            val account = userAccountDao.getUserById(userId) ?: userAccountDao.getActiveUserSync()
                ?: return Result.failure(IllegalStateException("No active user"))

            userAccountDao.updateEntitlement(
                userId = account.userId,
                isPremium = isPremium,
                unlockedJson = account.unlockedPackageIdsJson,
                expiryDate = expiryDate,
                syncedAt = System.currentTimeMillis()
            )

            if (!account.isGuest) {
                remoteGateway.pushEntitlement(
                    UserEntitlement(
                        userId = account.userId,
                        isPremiumUser = isPremium,
                        unlockedPackageIds = parseJsonStringList(account.unlockedPackageIdsJson),
                        premiumExpiryDate = expiryDate,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncWithCloud(userId: String): Result<UserEntitlement> {
        return try {
            val account = userAccountDao.getUserById(userId) ?: userAccountDao.getActiveUserSync()
                ?: return Result.failure(IllegalStateException("No active user"))

            if (account.isGuest) {
                return Result.success(account.toEntitlement())
            }

            val remoteResult = remoteGateway.fetchCloudEntitlement(account.userId)
            val remoteEntitlement = remoteResult.getOrThrow()

            // Merge local and remote unlocked packages
            val localList = parseJsonStringList(account.unlockedPackageIdsJson)
            val mergedPackages = (localList + remoteEntitlement.unlockedPackageIds).distinct()

            val isPremium = account.isPremiumUser || remoteEntitlement.isPremiumUser
            val expiry = maxOf(account.premiumExpiryDate ?: 0L, remoteEntitlement.premiumExpiryDate ?: 0L).let {
                if (it == 0L) null else it
            }

            val syncedAt = System.currentTimeMillis()

            userAccountDao.updateEntitlement(
                userId = account.userId,
                isPremium = isPremium,
                unlockedJson = mergedPackages.toJsonString(),
                expiryDate = expiry,
                syncedAt = syncedAt
            )

            val mergedEntitlement = UserEntitlement(
                userId = account.userId,
                isPremiumUser = isPremium,
                unlockedPackageIds = mergedPackages,
                premiumExpiryDate = expiry,
                lastSyncedAt = syncedAt
            )

            remoteGateway.pushEntitlement(mergedEntitlement)
            Result.success(mergedEntitlement)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isPackageUnlocked(userId: String, packageId: String, isFreePackage: Boolean): Boolean {
        if (isFreePackage) return true
        val entitlement = getEntitlement(userId)
        return entitlement.isPackageAccessible(packageId, isFreePackage)
    }
}
