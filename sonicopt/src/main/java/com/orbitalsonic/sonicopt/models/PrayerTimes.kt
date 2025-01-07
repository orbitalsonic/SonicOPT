package com.orbitalsonic.sonicopt.models

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,Jan,2025.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/**
 * Represents the prayer times for a specific day.
 *
 * @property prayerName The name of the prayer (e.g., Fajr, Dhuhr, etc.).
 * @property prayerTime The time of the prayer, formatted as per user settings.
 * @property isCurrentPrayer Indicates whether this prayer is currently active.
 */
data class PrayerTimes(
    var prayerName: String,
    var prayerTime: String,
    var isCurrentPrayer: Boolean = false
)

