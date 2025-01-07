package com.orbitalsonic.sonicopt.models

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,Jan,2025.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/**
 * Represents manual corrections to prayer times.
 *
 * Each correction is specified in minutes and applied to adjust prayer times.
 * Corrections are restricted to a range of -59 to +59 minutes.
 * If a correction exceeds this range, it is reset to 0 (no correction).
 *
 * @property fajrMinute Manual adjustment for Fajr prayer.
 * @property zuhrMinute Manual adjustment for Zuhr prayer.
 * @property asrMinute Manual adjustment for Asr prayer.
 * @property maghribMinute Manual adjustment for Maghrib prayer.
 * @property ishaMinute Manual adjustment for Isha prayer.
 */
data class PrayerManualCorrection(
    var fajrMinute: Int = 0, // Adjustment for Fajr prayer in minutes.
    var zuhrMinute: Int = 0, // Adjustment for Zuhr prayer in minutes.
    var asrMinute: Int = 0, // Adjustment for Asr prayer in minutes.
    var maghribMinute: Int = 0, // Adjustment for Maghrib prayer in minutes.
    var ishaMinute: Int = 0 // Adjustment for Isha prayer in minutes.
)
