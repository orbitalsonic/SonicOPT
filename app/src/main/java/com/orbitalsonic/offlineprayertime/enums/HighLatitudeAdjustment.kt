package com.orbitalsonic.offlineprayertime.enums

/**
 * Represents methods for adjusting prayer times at high latitudes
 * where the sun does not rise or set clearly during certain periods.
 */
enum class HighLatitudeAdjustment {
    /**
     * NONE: No adjustment applied for high latitudes.
     */
    NONE,

    /**
     * ANGLE_BASED: Uses the angle of the sun below the horizon
     * to define Fajr and Isha times.
     */
    ANGLE_BASED,

    /**
     * MID_NIGHT: Divides the night into two equal halves for
     * estimating Fajr and Isha times.
     */
    MID_NIGHT,

    /**
     * ONE_SEVENTH: Divides the night into seven parts, with
     * Fajr at one-seventh and Isha at six-sevenths of the night.
     */
    ONE_SEVENTH
}
