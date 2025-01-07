package com.orbitalsonic.sonicopt.models

/**
 * @Author: Muhammad Yaqoob
 * @Date: 01,Jan,2025.
 * @Accounts
 *      -> https://github.com/orbitalsonic
 *      -> https://www.linkedin.com/in/myaqoob7
 */


/**
 * Represents custom angles for Fajr and Isha prayers.
 *
 * These angles are used when the prayer time convention is set to CUSTOM.
 *
 * @property fajrAngle The custom angle for calculating Fajr prayer time.
 * @property ishaAngle The custom angle for calculating Isha prayer time.
 */
data class PrayerCustomAngle(
    val fajrAngle: Double = 9.0, // Default angle for Fajr prayer.
    val ishaAngle: Double = 14.0 // Default angle for Isha prayer.
)
