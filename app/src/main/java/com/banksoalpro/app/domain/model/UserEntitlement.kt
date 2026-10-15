package com.banksoalpro.app.domain.model

data class UserEntitlement(
    val userId: String,
    val isPremiumUser: Boolean = false,
    val unlockedPackageIds: List<String> = emptyList(),
    val premiumExpiryDate: Long? = null,
    val lastSyncedAt: Long = 0L
) {
    fun isPackageAccessible(packageId: String, isFreePackage: Boolean): Boolean {
        if (isFreePackage) return true
        if (isPremiumUser) {
            val now = System.currentTimeMillis()
            if (premiumExpiryDate == null || premiumExpiryDate > now) {
                return true
            }
        }
        return unlockedPackageIds.contains(packageId)
    }

    val isSubscriptionActive: Boolean
        get() = isPremiumUser && (premiumExpiryDate == null || premiumExpiryDate > System.currentTimeMillis())
}
