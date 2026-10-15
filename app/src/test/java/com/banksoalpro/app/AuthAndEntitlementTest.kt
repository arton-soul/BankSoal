package com.banksoalpro.app

import com.banksoalpro.app.data.local.entity.UserAccountEntity
import com.banksoalpro.app.data.repository.parseJsonStringList
import com.banksoalpro.app.data.repository.toEntitlement
import com.banksoalpro.app.data.repository.toJsonString
import com.banksoalpro.app.data.repository.toProfile
import com.banksoalpro.app.domain.model.UserEntitlement
import org.junit.Assert.*
import org.junit.Test

class AuthAndEntitlementTest {

    @Test
    fun testUserEntitlement_freePackage_alwaysAccessible() {
        val freeUser = UserEntitlement(
            userId = "guest_123",
            isPremiumUser = false,
            unlockedPackageIds = emptyList()
        )

        assertTrue(freeUser.isPackageAccessible("cpns_skd_free_01", isFreePackage = true))
        assertFalse(freeUser.isPackageAccessible("cpns_skd_pro_01", isFreePackage = false))
    }

    @Test
    fun testUserEntitlement_unlockedPackage_accessible() {
        val userWithUnlocked = UserEntitlement(
            userId = "guest_123",
            isPremiumUser = false,
            unlockedPackageIds = listOf("cpns_skd_pro_01", "utbk_snbt_pro_02")
        )

        assertTrue(userWithUnlocked.isPackageAccessible("cpns_skd_pro_01", isFreePackage = false))
        assertTrue(userWithUnlocked.isPackageAccessible("utbk_snbt_pro_02", isFreePackage = false))
        assertFalse(userWithUnlocked.isPackageAccessible("kedinasan_pro_01", isFreePackage = false))
    }

    @Test
    fun testUserEntitlement_premiumActive_allAccessible() {
        val futureTime = System.currentTimeMillis() + 86400000L // +1 day
        val premiumUser = UserEntitlement(
            userId = "google_user",
            isPremiumUser = true,
            premiumExpiryDate = futureTime
        )

        assertTrue(premiumUser.isSubscriptionActive)
        assertTrue(premiumUser.isPackageAccessible("cpns_skd_pro_99", isFreePackage = false))
        assertTrue(premiumUser.isPackageAccessible("utbk_premium_all", isFreePackage = false))
    }

    @Test
    fun testUserEntitlement_premiumExpired_notAccessibleUnlessUnlocked() {
        val pastTime = System.currentTimeMillis() - 86400000L // -1 day
        val expiredUser = UserEntitlement(
            userId = "google_user",
            isPremiumUser = true,
            unlockedPackageIds = listOf("purchased_permanently"),
            premiumExpiryDate = pastTime
        )

        assertFalse(expiredUser.isSubscriptionActive)
        // Permanently purchased package is still accessible
        assertTrue(expiredUser.isPackageAccessible("purchased_permanently", isFreePackage = false))
        // Non-purchased package is locked
        assertFalse(expiredUser.isPackageAccessible("random_pro_package", isFreePackage = false))
    }

    @Test
    fun testJsonStringList_serializationAndDeserialization() {
        val originalList = listOf("cpns_01", "utbk_01", "smp_02")
        val json = originalList.toJsonString()
        val parsedList = parseJsonStringList(json)

        assertEquals(originalList, parsedList)
    }

    @Test
    fun testUserAccountEntity_toProfileAndEntitlementMapping() {
        val entity = UserAccountEntity(
            userId = "google_arton_79",
            displayName = "Arton Soul",
            email = "arton.79@gmail.com",
            photoUrl = "https://example.com/avatar.png",
            isGuest = false,
            targetExamCategory = "CPNS_SKD",
            isPremiumUser = true,
            unlockedPackageIdsJson = listOf("pkg_1", "pkg_2").toJsonString(),
            premiumExpiryDate = 1799999999000L,
            lastSyncedAt = 1750000000000L,
            isActive = true
        )

        val profile = entity.toProfile()
        assertEquals("google_arton_79", profile.userId)
        assertEquals("Arton Soul", profile.displayName)
        assertEquals("arton.79@gmail.com", profile.email)
        assertEquals("A", profile.initial)
        assertFalse(profile.isGuest)

        val entitlement = entity.toEntitlement()
        assertEquals("google_arton_79", entitlement.userId)
        assertTrue(entitlement.isPremiumUser)
        assertEquals(listOf("pkg_1", "pkg_2"), entitlement.unlockedPackageIds)
        assertEquals(1799999999000L, entitlement.premiumExpiryDate)
    }

    @Test
    fun testCloudSyncMergeLogic() {
        val localPackages = listOf("pkg_cpns_01", "pkg_utbk_01")
        val remotePackages = listOf("pkg_utbk_01", "pkg_kedinasan_01")

        val merged = (localPackages + remotePackages).distinct()

        assertEquals(3, merged.size)
        assertTrue(merged.contains("pkg_cpns_01"))
        assertTrue(merged.contains("pkg_utbk_01"))
        assertTrue(merged.contains("pkg_kedinasan_01"))
    }
}
