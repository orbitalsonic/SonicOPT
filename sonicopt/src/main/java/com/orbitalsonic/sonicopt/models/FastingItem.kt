package com.orbitalsonic.sonicopt.models

/**
 * Represents the Sehri (pre-dawn meal) and Iftaar (breaking fast) times for a specific day.
 *
 * @property date in milli seconds.
 * @property sehriTime The time for Sehri, equivalent to Fajr prayer time.
 * @property iftaarTime The time for Iftaar, equivalent to Maghrib prayer time.
 */
data class FastingItem(
    var date:Long,
    val sehriTime: String,
    val iftaarTime: String
)
