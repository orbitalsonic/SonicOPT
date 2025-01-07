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
 * @property date in milli seconds.
 * @property prayerList List of all five prayers and sunrise & sunset.
 */
data class PrayerItem(
    var date:Long,
    var prayerList:List<PrayerTimes>
)

