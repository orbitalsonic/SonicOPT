package com.orbitalsonic.sonicopt.models

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,Jan,2025.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */

/**
 * Represents the details of an individual prayer time.
 *
 * @property prayerName The name of the prayer (e.g., Fajr, Dhuhr, Asr, Maghrib, Isha).
 * @property prayerTime The time of the prayer as a string, formatted based on user preferences.
 */
data class PrayerTimes(
    var prayerName: String, // The name of the prayer.
    var prayerTime: String, // The formatted prayer time.
)

