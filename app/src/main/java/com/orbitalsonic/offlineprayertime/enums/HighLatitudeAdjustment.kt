package com.orbitalsonic.offlineprayertime.enums

/**
 * Represents methods for adjusting prayer times at high latitudes
 * where the sun does not rise or set clearly during certain periods.
 */
enum class HighLatitudeAdjustment {

    /**
     * ANGLE_BASED: Uses the angle of the sun below the horizon to define times.
     */
    ANGLE_BASED,

    /**
     * MIDNIGHT: Divides the night into two halves to estimate prayer times.
     */
    MIDNIGHT,

    /**
     * ONE_SEVENTH: Divides the night into seven parts, with Fajr at one-seventh and Isha at six-sevenths.
     */
    ONE_SEVENTH
}
