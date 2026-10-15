package com.banksoalpro.app

import com.banksoalpro.app.core.rating.AppRatingManager
import org.junit.Assert.*
import org.junit.Test

class AppRatingManagerTest {

    private val oneDayMillis = 24L * 60L * 60L * 1000L

    @Test
    fun testRatingPrompt_notShownBefore3Days() {
        val firstLaunch = 1_000_000_000L

        // Hari ke-0 (beberapa jam setelah buka)
        val day0 = firstLaunch + (3 * 3600 * 1000L)
        assertFalse(
            "Ajakan rating tidak boleh muncul di hari ke-0",
            AppRatingManager.isEligibleForRatingPrompt(
                hasShownPrompt = false,
                firstLaunchTimeMillis = firstLaunch,
                currentTimeMillis = day0
            )
        )

        // Hari ke-2 (48 jam kemudian)
        val day2 = firstLaunch + (2 * oneDayMillis)
        assertFalse(
            "Ajakan rating tidak boleh muncul di hari ke-2 (harus minimal 3 hari)",
            AppRatingManager.isEligibleForRatingPrompt(
                hasShownPrompt = false,
                firstLaunchTimeMillis = firstLaunch,
                currentTimeMillis = day2
            )
        )
    }

    @Test
    fun testRatingPrompt_showsAfter3Days() {
        val firstLaunch = 1_000_000_000L

        // Tepat 3 hari (72 jam kemudian)
        val day3 = firstLaunch + (3 * oneDayMillis)
        assertTrue(
            "Ajakan rating harus muncul setelah genap 3 hari pemakaian",
            AppRatingManager.isEligibleForRatingPrompt(
                hasShownPrompt = false,
                firstLaunchTimeMillis = firstLaunch,
                currentTimeMillis = day3
            )
        )

        // Hari ke-5
        val day5 = firstLaunch + (5 * oneDayMillis)
        assertTrue(
            "Ajakan rating harus muncul jika sudah lebih dari 3 hari dan belum pernah tampil",
            AppRatingManager.isEligibleForRatingPrompt(
                hasShownPrompt = false,
                firstLaunchTimeMillis = firstLaunch,
                currentTimeMillis = day5
            )
        )
    }

    @Test
    fun testRatingPrompt_onlyShowsOnce() {
        val firstLaunch = 1_000_000_000L
        val day4 = firstLaunch + (4 * oneDayMillis)

        // Setelah pernah ditampilkan (hasShownPrompt = true)
        val eligible = AppRatingManager.isEligibleForRatingPrompt(
            hasShownPrompt = true,
            firstLaunchTimeMillis = firstLaunch,
            currentTimeMillis = day4
        )

        assertFalse(
            "Ajakan rating TIDAK boleh muncul lagi jika sudah pernah ditampilkan (cukup sekali saja)",
            eligible
        )
    }
}
