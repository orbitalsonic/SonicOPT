package com.orbitalsonic.offlineprayertime.models

/**
 * Represents the Sehri (pre-dawn meal) and Iftaar (breaking fast) times for a specific day.
 *
 * @property sehriTime The time for Sehri, equivalent to Fajr prayer time.
 * @property iftaarTime The time for Iftaar, equivalent to Maghrib prayer time.
 */
data class SehriIftaarItem(
    val sehriTime: String,
    val iftaarTime: String
)
