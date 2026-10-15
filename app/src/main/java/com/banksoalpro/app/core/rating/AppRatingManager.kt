package com.banksoalpro.app.core.rating

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object AppRatingManager {
    private const val PREF_NAME = "banksoal_rating_pref"
    const val KEY_FIRST_LAUNCH_TIME = "first_launch_time"
    const val KEY_RATING_PROMPT_SHOWN = "rating_prompt_shown"

    // Batas waktu pemakaian sebelum ajakan rating: 3 hari
    const val DAYS_BEFORE_PROMPT = 3L
    const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L

    fun initFirstLaunch(context: Context, currentTime: Long = System.currentTimeMillis()) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_FIRST_LAUNCH_TIME)) {
            prefs.edit().putLong(KEY_FIRST_LAUNCH_TIME, currentTime).apply()
        }
    }

    fun isEligibleForRatingPrompt(
        hasShownPrompt: Boolean,
        firstLaunchTimeMillis: Long,
        currentTimeMillis: Long
    ): Boolean {
        if (hasShownPrompt) return false
        if (firstLaunchTimeMillis <= 0L) return false
        val elapsedMillis = currentTimeMillis - firstLaunchTimeMillis
        val daysPassed = elapsedMillis / MILLIS_PER_DAY
        return daysPassed >= DAYS_BEFORE_PROMPT
    }

    fun shouldShowRatingPrompt(context: Context, currentTime: Long = System.currentTimeMillis()): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val hasShown = prefs.getBoolean(KEY_RATING_PROMPT_SHOWN, false)
        if (hasShown) return false

        val firstLaunch = prefs.getLong(KEY_FIRST_LAUNCH_TIME, 0L)
        if (firstLaunch == 0L) {
            prefs.edit().putLong(KEY_FIRST_LAUNCH_TIME, currentTime).apply()
            return false
        }

        return isEligibleForRatingPrompt(hasShown, firstLaunch, currentTime)
    }

    fun markRatingPromptShown(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_RATING_PROMPT_SHOWN, true).apply()
    }

    fun openPlayStore(context: Context) {
        val packageName = context.packageName
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            context.startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }
}
