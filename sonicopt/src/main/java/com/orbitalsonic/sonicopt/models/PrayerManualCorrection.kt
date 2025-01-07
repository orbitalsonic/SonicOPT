package com.orbitalsonic.sonicopt.models

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,Jan,2025.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/**
 * Time Should be in minutes
 * Only allow manual corrections within the range of -59 to 59 minutes.
 * If the correction is outside this range, set it to 0 (no correction).
 */
data class PrayerManualCorrection(
    var fajrMinute: Int = 0,
    var zuhrMinute: Int = 0,
    var asrMinute: Int = 0,
    var maghribMinute: Int = 0,
    var ishaMinute: Int = 0
)

