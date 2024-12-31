package com.orbitalsonic.opt.models

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

