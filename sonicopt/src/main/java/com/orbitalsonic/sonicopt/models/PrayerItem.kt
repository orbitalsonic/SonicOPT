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
 * @property date The date of the prayer times in milliseconds since epoch.
 * @property prayerList A list containing the details of all five prayers, including sunrise and sunset times.
 */
data class PrayerItem(
    var date: Long, // The date of the specific day's prayer times in milliseconds.
    var prayerList: List<PrayerTimes> // List of prayer times and additional details for the day.
)

